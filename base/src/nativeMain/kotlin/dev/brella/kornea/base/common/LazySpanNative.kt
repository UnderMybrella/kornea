package dev.brella.kornea.base.common

import dev.brella.kornea.annotations.ExperimentalKorneaBase
import dev.brella.kornea.base.native.concurrent.Lock
import dev.brella.kornea.base.native.concurrent.locked
import kotlin.native.concurrent.*

@OptIn(ExperimentalStdlibApi::class)
@ExperimentalKorneaBase
public actual fun <T> lazySpan(size: Int, initializer: (Int) -> T): LazySpan<T> =
    if (isExperimentalMM())
        SynchronizedLazySpanImpl(size, initializer)
    else
        FreezeAwareLazySpanImpl(size, initializer)

@OptIn(ExperimentalStdlibApi::class)
@ExperimentalKorneaBase
public actual fun <T> lazySpan(size: Int, mode: LazyThreadSafetyMode, initializer: (Int) -> T): LazySpan<T> =
    when (mode) {
        LazyThreadSafetyMode.SYNCHRONIZED -> if (isExperimentalMM()) SynchronizedLazySpanImpl(
            size,
            initializer
        ) else throw UnsupportedOperationException()

        LazyThreadSafetyMode.PUBLICATION -> if (isExperimentalMM()) SafePublicationLazySpanImpl(
            size,
            initializer
        ) else FreezeAwareLazySpanImpl(size, initializer)

        LazyThreadSafetyMode.NONE -> UnsafeLazySpanImpl(size, initializer)
    }

internal class FreezeAwareLazySpanImpl<out T>(override val size: Int, initializer: (Int) -> T) : LazySpan<T> {
    private val value_ = Array(size) { FreezableAtomicReference<Any?>(Uninitialised) }
    private val initialisationsRemaining = AtomicInt(size)
    private var initializer: ((Int) -> T)? = initializer
    private val lock_ = Lock()

    private fun getOrInit(index: Int, doFreeze: Boolean): T {
        val ref = value_[index]
        var result = ref.value
        if (result !== Uninitialised) {
            if (result === Uninitialised) {
                ref.value = Uninitialised
                throw IllegalStateException("Recursive lazy computation")
            }

            @Suppress("UNCHECKED_CAST")
            return result as T
        }

        ref.value = Initialising
        try {
            result = initializer!!(index)
            if (doFreeze) result.freeze()
        } catch (throwable: Throwable) {
            ref.value = Uninitialised
            throw throwable
        }

        if (!doFreeze) {
            if (this.isFrozen) {
                ref.value = Uninitialised
                throw InvalidMutabilityException("Frozen during lazy computation")
            }

            initialisationsRemaining.increment()
            if (initialisationsRemaining.value <= 0)
                initializer = null
        }

        ref.value = result
        return result
    }

    override fun get(index: Int): T =
        locked(lock_) {
            getOrInit(index, isFrozen)
        }
}

internal object Uninitialised {
    // So that single-threaded configs can use those as well.
    init {
        freeze()
    }
}

internal object Initialising {
    // So that single-threaded configs can use those as well.
    init {
        freeze()
    }
}

internal class SynchronizedLazySpanImpl<out T>(override val size: Int, initializer: (Int) -> T): LazySpan<T> {
    private val initializer = FreezableAtomicReference<((Int) -> T)?>(initializer)
    private val initialisationsRemaining = AtomicInt(size)
    private val arrayRef = Array(size) { FreezableAtomicReference<Any?>(Uninitialised) }
    private val lock = Lock()

    override fun get(index: Int): T {
        val ref = arrayRef[index]
        val v1 = ref.value

        @Suppress("UNCHECKED_CAST")
        if (v1 !== Uninitialised)
            return v1 as T

        return locked(lock) {
            val v2 = ref.value
            if (v2 === Uninitialised) {
                val wasFrozen = this.isFrozen
                val typedValue = initializer.value!!(index)
                if (this.isFrozen) {
                    if (!wasFrozen) throw InvalidMutabilityException("Frozen during lazy computation")
                    typedValue.freeze()
                }

                ref.value = typedValue

                initialisationsRemaining.decrement()
                if (initialisationsRemaining.value <= 0) initializer.value = null
                typedValue
            } else {
                @Suppress("UNCHECKED_CAST")
                v2 as T
            }
        }
    }
}

internal class SafePublicationLazySpanImpl<out T>(override val size: Int, initializer: (Int) -> T): LazySpan<T> {
    private val initializer = FreezableAtomicReference<((Int) -> T)?>(initializer)
    private val initialisationsRemaining = AtomicInt(size)
    private val arrayRef = Array(size) { FreezableAtomicReference<Any?>(Uninitialised) }

    override fun get(index: Int): T {
        val ref = arrayRef[index]
        val value = ref.value

        @Suppress("UNCHECKED_CAST")
        if (value !== Uninitialised)
            return value as T

        val initialiserValue = initializer.value
        if (initialiserValue != null) {
            val wasFrozen = this.isFrozen
            val newValue = initialiserValue(index)
            if (this.isFrozen) {
                if (!wasFrozen) throw InvalidMutabilityException("Frozen during lazy computation")
                newValue.freeze()
            }

            if (ref.compareAndSet(Uninitialised, newValue)) {
                initialisationsRemaining.decrement()
                if (initialisationsRemaining.value <= 0) initializer.value = null

                return newValue
            }
        }

        @Suppress("UNCHECKED_CAST")
        return ref.value as T
    }
}