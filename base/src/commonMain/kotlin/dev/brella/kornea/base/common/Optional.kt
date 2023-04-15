package dev.brella.kornea.base.common

import dev.brella.kornea.annotations.AvailableSince
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline
import kotlin.reflect.KClass

@JvmInline
public value class Optional<out T>(@PublishedApi internal val innerValue: Any?) {
    public companion object {
        public val EMPTY: Optional<Any?> = Optional(EmptyValue)

        public inline fun <T> of(value: T): Optional<T> =
            Optional(value)

        public inline fun <T> ofNullable(value: T?): Optional<T> =
            if (value == null) empty() else Optional(value)


        public inline fun <T> empty(): Optional<T> =
            EMPTY.asType()
    }

    @PublishedApi
    internal object EmptyValue


    @Suppress("UNCHECKED_CAST")
    public inline val value: T
        get() =
            if (innerValue === EmptyValue) throw IllegalStateException("Missing value")
            else innerValue as T

    public inline val isPresent: Boolean
        get() = innerValue !== EmptyValue

    public inline val isEmpty: Boolean
        get() = innerValue === EmptyValue
}


@Suppress("UNCHECKED_CAST")
@PublishedApi
internal inline fun <T> Optional<T>.getUnsafe(): T =
    innerValue as T


@Suppress("UNCHECKED_CAST")
public inline fun <T> Optional<*>.asType(): Optional<T> =
    this as Optional<T>


@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T, reified R> Optional<T>.cast(): Optional<R> =
    if (value is R) asType()
    else Optional.empty()

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T, R> Optional<T>.map(transform: (T) -> R): Optional<R> =
    if (innerValue === Optional.EmptyValue) asType() else Optional(transform(getUnsafe()))

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T, R> Optional<T>.flatMap(transform: (T) -> Optional<R>): Optional<R> =
    if (innerValue === Optional.EmptyValue) asType() else transform(getUnsafe())

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T> Optional<T>.flatMapOrSelf(transform: (T) -> Optional<T>?): Optional<T> =
    when (innerValue) {
        Optional.EmptyValue -> this
        else -> when (val result = transform(getUnsafe())) {
            null -> this
            else -> result
        }
    }

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T> Optional<T>.filter(predicate: (T) -> Boolean): Optional<T> =
    when {
        innerValue === Optional.EmptyValue -> this
        predicate(getUnsafe()) -> this
        else -> Optional.empty()
    }


@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T> Optional<T?>.filterNotNull(): Optional<T> =
    if (innerValue == null) Optional.empty() else asType()


@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T> Optional<T?>.filterNotNull(onEmpty: () -> Optional<T>): Optional<T> =
    if (innerValue == null) onEmpty() else asType()


@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T> Optional<T?>.filterNotNull(default: Optional<T>): Optional<T> =
    if (innerValue == null) default else asType()

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T> Optional<T>.filterTo(transform: (T) -> Optional<T>?): Optional<T> =
    when {
        innerValue === Optional.EmptyValue -> this
        else -> when (val result = transform(getUnsafe())) {
            null -> Optional.empty()
            else -> result
        }
    }

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <reified R> Optional<*>.filterToInstance(): Optional<R> =
    if (innerValue is R) asType() else Optional.empty()

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <R : Any> Optional<*>.filterToInstance(klass: KClass<R>): Optional<R> =
    if (klass.isInstance(innerValue)) asType() else Optional.empty()

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <reified R> Optional<*>.filterToInstance(provider: () -> Optional<R>): Optional<R> =
    when {
        innerValue === Optional.EmptyValue -> asType()
        innerValue is R -> asType()

        else -> provider()
    }

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <R : Any> Optional<*>.filterToInstance(
    klass: KClass<R>,
    onEmpty: () -> Optional<R>
): Optional<R> =
    when {
        innerValue === Optional.EmptyValue -> asType()
        klass.isInstance(innerValue) -> asType()
        else -> onEmpty()
    }


@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <reified R> Optional<*>.filterToInstance(default: Optional<R>): Optional<R> =
    when {
        innerValue === Optional.EmptyValue -> asType()
        innerValue is R -> asType()

        else -> default
    }


@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <R : Any> Optional<*>.filterToInstance(
    default: Optional<R>,
    klass: KClass<R>
): Optional<R> =
    when {
        innerValue === Optional.EmptyValue -> asType()
        klass.isInstance(innerValue) -> asType()
        else -> default
    }


@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T, reified R : T> Optional<T>.filterToInstance(transform: (T) -> Optional<R>): Optional<R> =
    when {
        innerValue === Optional.EmptyValue -> asType()
        innerValue is R -> asType()

        else -> transform(getUnsafe())
    }

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T : Any, R : T> Optional<T>.filterToInstance(
    klass: KClass<R>,
    transform: (T) -> Optional<R>
): Optional<R> =
    when {
        innerValue === Optional.EmptyValue -> asType()
        klass.isInstance(innerValue) -> asType()

        else -> transform(getUnsafe())
    }

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T> Optional<T>.doOnPresent(block: (T) -> Unit): Optional<T> {
    if (isPresent) block(getUnsafe())
    return this
}

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T> Optional<T>.doOnEmpty(block: () -> Unit): Optional<T> {
    if (isEmpty) block()
    return this
}

public inline fun <T> Optional<T>.doIfPresent(block: (T) -> Unit): Optional<T> =
    doOnPresent(block)
public inline fun <T> Optional<T>.doIfEmpty(block: () -> Unit): Optional<T> =
    doOnEmpty(block)

//It's really rude, kotlin currently throws an error for this >:(
//@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
//public val <T> Optional<T>.valueOrNull: T?
//    get() = if (_value === Optional.EMPTY_VALUE) null else _value as T

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T> Optional<T>.get(): T = value

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T> Optional<T>.getOrNull(): T? = if (innerValue === Optional.EmptyValue) null else getUnsafe()

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T> Optional<T>.getOrDefault(default: T): T =
    if (innerValue === Optional.EmptyValue) default else getUnsafe()

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T> Optional<T>.getOrElseRun(block: () -> T): T {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return if (innerValue === Optional.EmptyValue) block() else getUnsafe()
}

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T> Optional<T>.orElse(default: Optional<T>): Optional<T> =
    if (innerValue === Optional.EmptyValue) default else this

@OptIn(ExperimentalContracts::class)
public inline fun <T> Optional<T>.getOrBreak(block: () -> Nothing): T {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return if (innerValue === Optional.EmptyValue) block() else getUnsafe()
}