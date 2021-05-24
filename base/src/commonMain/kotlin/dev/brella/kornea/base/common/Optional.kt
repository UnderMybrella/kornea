@file:Suppress("UNCHECKED_CAST")

package dev.brella.kornea.base.common

import dev.brella.kornea.annotations.AvailableSince
import kotlin.jvm.JvmInline
import kotlin.reflect.KClass

@JvmInline
public value class Optional<out T>(@PublishedApi internal val _value: Any?) {
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

public inline fun <T> Optional.Companion.of(value: T): Optional<T> =
    Optional(value)

public inline fun <T> Optional.Companion.ofNullable(value: T?): Optional<T> =
    if (value == null) empty() else Optional(value)

public inline fun <T> Optional.Companion.empty(): Optional<T> =
    EMPTY as Optional<T>

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T, reified R> Optional<T>.cast(): Optional<R> =
    when (_value) {
        Optional.EMPTY_VALUE -> Optional.empty()
        is R -> this as Optional<R>
        else -> Optional.empty()
    }

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T, R> Optional<T>.map(transform: (T) -> R): Optional<R> =
    when (_value) {
        Optional.EMPTY_VALUE -> Optional.empty()
        else -> Optional(transform(_value as T))
    }

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T, R> Optional<T>.flatMap(transform: (T) -> Optional<R>): Optional<R> =
    when (_value) {
        Optional.EMPTY_VALUE -> Optional.empty()
        else -> transform(_value as T)
    }

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T> Optional<T>.flatMapOrSelf(transform: (T) -> Optional<T>?): Optional<T> =
    when (_value) {
        Optional.EMPTY_VALUE -> Optional.empty()
        else -> when (val result = transform(_value as T)) {
            null -> this
            else -> result
        }
    }

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T> Optional<T>.filter(predicate: (T) -> Boolean): Optional<T> =
    when {
        _value === Optional.EMPTY_VALUE -> Optional.empty()
        predicate(_value as T) -> this
        else -> Optional.empty()
    }

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T> Optional<T?>.filterNotNull(): Optional<T> =
    when {
        _value === Optional.EMPTY_VALUE -> Optional.empty()
        _value != null -> this as Optional<T>
        else -> Optional.empty()
    }

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T> Optional<T>.filterNotNull(onEmpty: () -> Optional<T>): Optional<T> =
    when {
        _value === Optional.EMPTY_VALUE -> onEmpty()
        _value != null -> this
        else -> onEmpty()
    }

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T> Optional<T>.filterNotNull(default: Optional<T>): Optional<T> =
    when {
        _value === Optional.EMPTY_VALUE -> default
        _value != null -> this
        else -> default
    }

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T> Optional<T>.filterTo(transform: (T) -> Optional<T>?): Optional<T> =
    when {
        _value === Optional.EMPTY_VALUE -> this
        else -> when (val result = transform(_value as T)) {
            null -> Optional.empty()
            else -> result
        }
    }

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <reified R> Optional<*>.filterToInstance(): Optional<R> =
    when (_value) {
        Optional.EMPTY_VALUE -> Optional.empty()
        is R -> this as Optional<R>
        else -> Optional.empty()
    }

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <R : Any> Optional<*>.filterToInstance(klass: KClass<R>): Optional<R> =
    when {
        _value === Optional.EMPTY_VALUE -> Optional.empty()
        klass.isInstance(_value) -> this as Optional<R>
        else -> Optional.empty()
    }

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <reified R> Optional<*>.filterToInstance(onEmpty: () -> Optional<R>): Optional<R> =
    when (_value) {
        Optional.EMPTY_VALUE -> onEmpty()
        is R -> this as Optional<R>
        else -> onEmpty()
    }

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <R : Any> Optional<*>.filterToInstance(
    klass: KClass<R>,
    onEmpty: () -> Optional<R>
): Optional<R> =
    when {
        _value === Optional.EMPTY_VALUE -> onEmpty()
        klass.isInstance(_value) -> this as Optional<R>
        else -> onEmpty()
    }

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <reified R> Optional<*>.filterToInstance(default: Optional<R>): Optional<R> =
    when (_value) {
        Optional.EMPTY_VALUE -> default
        is R -> this as Optional<R>
        else -> default
    }


@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <R : Any> Optional<*>.filterToInstance(
    default: Optional<R>,
    klass: KClass<R>
): Optional<R> =
    when {
        _value === Optional.EMPTY_VALUE -> default
        klass.isInstance(_value) -> this as Optional<R>
        else -> default
    }

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T, reified R : T> Optional<T>.filterToInstance(transform: (T) -> Optional<R>): Optional<R> =
    when (_value) {
        Optional.EMPTY_VALUE -> Optional.empty()
        is R -> this as Optional<R>
        else -> transform(_value as T)
    }

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T : Any, R : T> Optional<T>.filterToInstance(
    klass: KClass<R>,
    transform: (T) -> Optional<R>
): Optional<R> =
    when {
        _value === Optional.EMPTY_VALUE -> Optional.empty()
        klass.isInstance(_value) -> this as Optional<R>
        else -> transform(_value as T)
    }

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T> Optional<T>.doOnPresent(block: (T) -> Unit): Optional<T> =
    when (val value = _value) {
        Optional.EMPTY_VALUE -> this
        else -> {
            block(value as T)
            this
        }
    }

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T> Optional<T>.doOnEmpty(block: () -> Unit): Optional<T> =
    when (_value) {
        Optional.EMPTY_VALUE -> {
            block()
            this
        }
        else -> this
    }

//It's really rude, kotlin currently throws an error for this >:(
//@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
//public val <T> Optional<T>.valueOrNull: T?
//    get() = if (_value === Optional.EMPTY_VALUE) null else _value as T

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T> Optional<T>.get(): T = value

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T> Optional<T>.getOrNull(): T? = if (_value === Optional.EMPTY_VALUE) null else _value as T

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T> Optional<T>.getOrElse(default: T): T =
    if (_value === Optional.EMPTY_VALUE) default else _value as T

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T> Optional<T>.getOrElseRun(block: () -> T): T =
    if (_value === Optional.EMPTY_VALUE) block() else _value as T

@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
public inline fun <T> Optional<T>.orElse(default: Optional<T>): Optional<T> =
    if (_value === Optional.EMPTY_VALUE) default else this