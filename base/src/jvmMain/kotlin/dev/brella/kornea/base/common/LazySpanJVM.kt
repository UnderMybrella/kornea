package dev.brella.kornea.base.common

import kotlinx.atomicfu.atomicArrayOfNulls

public actual fun <T> lazySpan(size: Int, initializer: (Int) -> T): LazySpan<T> =
    SynchronizedLazySpanImpl(size, initializer)

public actual fun <T> lazySpan(size: Int, mode: LazyThreadSafetyMode, initializer: (Int) -> T): LazySpan<T> =
    when (mode) {
        LazyThreadSafetyMode.SYNCHRONIZED -> SynchronizedLazySpanImpl(size, initializer)
        LazyThreadSafetyMode.PUBLICATION -> SafePublicationLazySpanImpl(size, initializer)
        LazyThreadSafetyMode.NONE -> UnsafeLazySpanImpl(size, initializer)
    }

private class SynchronizedLazySpanImpl<out T>(
    override val size: Int,
    initializer: (Int) -> T,
    lock: Any? = null
) : LazySpan<T> {
    private var initializer: ((Int) -> T)? = initializer
    private var initialisationsRemaining = size
    private val _array = Array<Any?>(size) { UninitialisedElement }

    // final field is required to enable safe publication of constructed instance
    private val lock = lock ?: this

    override fun get(index: Int): T {
        val v1 = _array[index]
        if (v1 !== UninitialisedElement) {
            lazy {  }
            @Suppress("UNCHECKED_CAST")
            return v1 as T
        }

        return synchronized(lock) {
            val v2 = _array[index]
            if (v2 !== UninitialisedElement) {
                @Suppress("UNCHECKED_CAST")
                (v2 as T)
            } else {
                val typedValue = initializer!!(index)
                _array[index] = typedValue
                if (--initialisationsRemaining <= 0) {
                    initializer = null
                }

                typedValue
            }
        }
    }
}

private class SafePublicationLazySpanImpl<out T>(
    override val size: Int,
    initializer: (Int) -> T
) : LazySpan<T> {
    private var initializer: ((Int) -> T)? = initializer
    private var initialisationsRemaining = size
    private val _array = atomicArrayOfNulls<Any?>(size)

    // this final field is required to enable safe initialization of the constructed instance
    private val final: Any = UninitialisedElement

    override fun get(index: Int): T {
        val ref = _array[index]
        val value = ref.value
        if (value !== UninitialisedElement) {
            @Suppress("UNCHECKED_CAST")
            return value as T
        }

        val initializerValue = initializer
        if (initializerValue != null) {
            val newValue = initializerValue(index)
            if (ref.compareAndSet(UninitialisedElement, newValue)) {
                if (--initialisationsRemaining <= 0) {
                    initializer = null
                }

                return newValue
            }
        }

        @Suppress("UNCHECKED_CAST")
        return ref.value as T
    }

    init {
        repeat(size) { _array[it].value = UninitialisedElement }
    }
}