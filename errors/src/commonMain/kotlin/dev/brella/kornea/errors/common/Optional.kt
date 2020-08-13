@file:Suppress("UNCHECKED_CAST")

package dev.brella.kornea.errors.common

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.annotations.ChangedSince
import kotlin.reflect.KClass

@AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
public inline class Optional<out T>(@PublishedApi internal val _value: Any?) {
    public companion object {
        public val EMPTY: Optional<Any?> = Optional(EMPTY_VALUE)
    }

    @PublishedApi
    internal object EMPTY_VALUE

    @Suppress("UNCHECKED_CAST")
    public inline val value: T
        get() = if (_value === EMPTY_VALUE) throw IllegalStateException("Missing value") else _value as T

    public inline val isPresent: Boolean
        get() = _value !== EMPTY_VALUE

    public inline val isEmpty: Boolean
        get() = _value === EMPTY_VALUE
}

@AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
public inline fun <T> Optional.Companion.empty(): Optional<T> =
    EMPTY as Optional<T>

@AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
public inline fun <T> Optional<KorneaResult<T>>.flatten(): KorneaResult<T> =
    if (isEmpty) _value as KorneaResult<T> else KorneaResult.empty()

@AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
public inline fun <T> KorneaResult<Optional<T>>.filter(): KorneaResult<T> =
    when (this) {
        is KorneaResult.Success ->
            if (get().isPresent) this mapValue (get()._value as T)
            else KorneaResult.empty()
        is KorneaResult.Failure -> asType()
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(this)
        )
    }

@AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
public inline fun <T, reified R> Optional<T>.cast(): Optional<R> =
    when (_value) {
        Optional.EMPTY_VALUE -> Optional.empty()
        is R -> this as Optional<R>
        else -> Optional.empty()
    }

@AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
public inline fun <T, R> Optional<T>.map(transform: (T) -> R): Optional<R> =
    when (_value) {
        Optional.EMPTY_VALUE -> Optional.empty()
        else -> Optional(transform(_value as T))
    }

@AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
public inline fun <T, R> Optional<T>.flatMap(transform: (T) -> Optional<R>): Optional<R> =
    when (_value) {
        Optional.EMPTY_VALUE -> Optional.empty()
        else -> transform(_value as T)
    }

@AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
public inline fun <T> Optional<T>.flatMapOrSelf(transform: (T) -> Optional<T>?): Optional<T> =
    when (_value) {
        Optional.EMPTY_VALUE -> Optional.empty()
        else -> when (val result = transform(_value as T)) {
            null -> this
            else -> result
        }
    }

@AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
public inline fun <T> Optional<T>.filter(predicate: (T) -> Boolean): Optional<T> =
    when {
        _value === Optional.EMPTY_VALUE -> Optional.empty()
        predicate(_value as T) -> this
        else -> Optional.empty()
    }

@AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
public inline fun <T> Optional<T?>.filterNotNull(): Optional<T> =
    when {
        _value === Optional.EMPTY_VALUE -> Optional.empty()
        _value != null -> this as Optional<T>
        else -> Optional.empty()
    }

@AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
public inline fun <T> Optional<T>.filterNotNull(onEmpty: () -> Optional<T>): Optional<T> =
    when {
        _value === Optional.EMPTY_VALUE -> onEmpty()
        _value != null -> this
        else -> onEmpty()
    }

@AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
public inline fun <T> Optional<T>.filterNotNull(default: Optional<T>): Optional<T> =
    when {
        _value === Optional.EMPTY_VALUE -> default
        _value != null -> this
        else -> default
    }

@AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
public inline fun <T> Optional<T>.filterTo(transform: (T) -> Optional<T>?): Optional<T> =
    when {
        _value === Optional.EMPTY_VALUE -> this
        else -> when (val result = transform(_value as T)) {
            null -> Optional.empty()
            else -> result
        }
    }

@AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
public inline fun <reified R> Optional<*>.filterToInstance(): Optional<R> =
    when (_value) {
        Optional.EMPTY_VALUE -> Optional.empty()
        is R -> this as Optional<R>
        else -> Optional.empty()
    }

@AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
public inline fun <R : Any> Optional<*>.filterToInstance(klass: KClass<R>): Optional<R> =
    when {
        _value === Optional.EMPTY_VALUE -> Optional.empty()
        klass.isInstance(_value) -> this as Optional<R>
        else -> Optional.empty()
    }

@AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
public inline fun <reified R> Optional<*>.filterToInstance(onEmpty: () -> Optional<R>): Optional<R> =
    when (_value) {
        Optional.EMPTY_VALUE -> onEmpty()
        is R -> this as Optional<R>
        else -> onEmpty()
    }

@AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
public inline fun <R : Any> Optional<*>.filterToInstance(
    klass: KClass<R>,
    onEmpty: () -> Optional<R>
): Optional<R> =
    when {
        _value === Optional.EMPTY_VALUE -> onEmpty()
        klass.isInstance(_value) -> this as Optional<R>
        else -> onEmpty()
    }

@AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
public inline fun <reified R> Optional<*>.filterToInstance(default: Optional<R>): Optional<R> =
    when (_value) {
        Optional.EMPTY_VALUE -> default
        is R -> this as Optional<R>
        else -> default
    }


@AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
public inline fun <R : Any> Optional<*>.filterToInstance(
    default: Optional<R>,
    klass: KClass<R>
): Optional<R> =
    when {
        _value === Optional.EMPTY_VALUE -> default
        klass.isInstance(_value) -> this as Optional<R>
        else -> default
    }

@AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
public inline fun <T, reified R : T> Optional<T>.filterToInstance(transform: (T) -> Optional<R>): Optional<R> =
    when (_value) {
        Optional.EMPTY_VALUE -> Optional.empty()
        is R -> this as Optional<R>
        else -> transform(_value as T)
    }

@AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
public inline fun <T : Any, R : T> Optional<T>.filterToInstance(
    klass: KClass<R>,
    transform: (T) -> Optional<R>
): Optional<R> =
    when {
        _value === Optional.EMPTY_VALUE -> Optional.empty()
        klass.isInstance(_value) -> this as Optional<R>
        else -> transform(_value as T)
    }

@AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
public inline val <T> Optional<T>.valueOrNull: T?
    get() = if (_value === Optional.EMPTY_VALUE) null else _value as T

@AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
public inline fun <T> Optional<T>.get(): T = value
@AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
public inline fun <T> Optional<T>.getOrNull(): T? = valueOrNull
@AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
public inline fun <T> Optional<T>.getOrElse(default: T): T = if (_value === Optional.EMPTY_VALUE) default else _value as T

@AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
public inline fun <T> Optional<T>.getOrElseRun(block: () -> T): T =
    if (_value === Optional.EMPTY_VALUE) block() else _value as T

@AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
public inline fun <T> Optional<T>.orElse(default: Optional<T>): Optional<T> =
    if (_value === Optional.EMPTY_VALUE) default else this