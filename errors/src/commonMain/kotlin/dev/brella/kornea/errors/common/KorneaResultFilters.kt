package dev.brella.kornea.errors.common

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.annotations.ChangedSince
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalContracts::class)
@ChangedSince(
    KorneaErrors.VERSION_3_1_0_INDEV,
    "When a filter fails, the result has a chance to control the output"
)
public inline fun <T> KorneaResult<T>.filter(predicate: (T) -> Boolean): KorneaResult<T> {
    contract {
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
    }

    return if (isSuccess)
        if (predicate(value as T)) this
        else KorneaResult.failedPredicate()
    else
        asType()
}

@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_0_0_ALPHA)
public inline fun <R, T : R> KorneaResult<T>.filterWith(predicate: (T) -> Boolean, transform: (T) -> R): KorneaResult<R> {
    contract {
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
    }

    return if (isSuccess)
        if (predicate(value as T)) KorneaResult.success(transform(value as T))
        else KorneaResult.failedPredicate()
    else
        asType()
}

@Suppress("UNCHECKED_CAST")
@AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
public inline fun <T> KorneaResult<T?>.filterNotNull(): KorneaResult<T> =
    if (isSuccess)
        if (value == null) KorneaResult.failedPredicate()
        else this as KorneaResult<T>
    else
        asType()

@OptIn(ExperimentalContracts::class)
@Suppress("UNCHECKED_CAST")
@AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
public inline fun <R> KorneaResult<*>.filterNotNull(onEmpty: () -> KorneaResult<R>): KorneaResult<R> {
    contract {
        callsInPlace(onEmpty, InvocationKind.AT_MOST_ONCE)
    }

    return if (isSuccess)
        if (value == null) onEmpty()
        else this as KorneaResult<R>
    else
        asType()
}

@Suppress("UNCHECKED_CAST")
@AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
public inline fun <R> KorneaResult<*>.filterNotNull(default: KorneaResult<R>): KorneaResult<R> =
    if (isSuccess)
        if (value == null) default
        else this as KorneaResult<R>
    else
        asType()

@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalContracts::class)
@ChangedSince(
    KorneaErrors.VERSION_3_1_0_INDEV,
    "When a filter fails, the result has a chance to control the output"
)
public inline fun <T> KorneaResult<T>.filterTo(transform: (T) -> KorneaResult<T>?): KorneaResult<T> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return if (isSuccess)
        when (val result = transform(value as T)) {
            null -> KorneaResult.failedPredicate()
            else -> result
        }
    else
        asType()
}

@Suppress("UNCHECKED_CAST")
@ChangedSince(
    KorneaErrors.VERSION_3_1_0_INDEV,
    "When a filter fails, the result has a chance to control the output"
)
public inline fun <reified R> KorneaResult<*>.filterToInstance(): KorneaResult<R> =
    if (isSuccess)
        if (value is R) this as KorneaResult<R>
        else KorneaResult.typeCastEmpty()
    else
        asType()

@Suppress("UNCHECKED_CAST")
@AvailableSince(KorneaErrors.VERSION_3_4_0_INDEV)
public inline fun <R : Any> KorneaResult<*>.filterToInstance(klass: KClass<R>): KorneaResult<R> =
    if (isSuccess)
        if (klass.isInstance(value)) this as KorneaResult<R>
        else KorneaResult.typeCastEmpty()
    else
        asType()

@OptIn(ExperimentalContracts::class)
@Suppress("UNCHECKED_CAST")
public inline fun <reified R> KorneaResult<*>.filterToInstance(onEmpty: () -> KorneaResult<R>): KorneaResult<R> {
    contract {
        callsInPlace(onEmpty, InvocationKind.AT_MOST_ONCE)
    }

    return if (isSuccess)
        if (value is R) this as KorneaResult<R>
        else KorneaResult.typeCastEmpty()
    else
        asType()
}

@OptIn(ExperimentalContracts::class)
@Suppress("UNCHECKED_CAST")
@AvailableSince(KorneaErrors.VERSION_3_4_0_INDEV)
public inline fun <R : Any> KorneaResult<*>.filterToInstance(
    klass: KClass<R>,
    onEmpty: () -> KorneaResult<R>
): KorneaResult<R> {
    contract {
        callsInPlace(onEmpty, InvocationKind.AT_MOST_ONCE)
    }

    return if (isSuccess)
        if (klass.isInstance(value)) this as KorneaResult<R>
        else onEmpty()
    else
        asType()
}

@Suppress("UNCHECKED_CAST")
public inline fun <reified R> KorneaResult<*>.filterToInstance(default: KorneaResult<R>): KorneaResult<R> =
    if (isSuccess)
        if (value is R) this as KorneaResult<R>
        else default
    else
        asType()

@Suppress("UNCHECKED_CAST")
@AvailableSince(KorneaErrors.VERSION_3_4_0_INDEV)
public inline fun <R : Any> KorneaResult<*>.filterToInstance(
    default: KorneaResult<R>,
    klass: KClass<R>
): KorneaResult<R> =
    if (isSuccess)
        if (klass.isInstance(value)) this as KorneaResult<R>
        else default
    else
        asType()

@OptIn(ExperimentalContracts::class)
@Suppress("UNCHECKED_CAST")
public inline fun <T, reified R : T> KorneaResult<T>.filterToInstance(transform: (T) -> KorneaResult<R>): KorneaResult<R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return if (isSuccess) {
        if (value is R) this as KorneaResult<R>
        else transform(value as T)
    } else {
        asType()
    }
}

@OptIn(ExperimentalContracts::class)
@Suppress("UNCHECKED_CAST")
@AvailableSince(KorneaErrors.VERSION_3_4_0_INDEV)
public inline fun <T : Any, R : T> KorneaResult<T>.filterToInstance(
    klass: KClass<R>,
    transform: (T) -> KorneaResult<R>
): KorneaResult<R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return if (isSuccess) {
        if (klass.isInstance(value)) this as KorneaResult<R>
        else transform(value as T)
    } else {
        asType()
    }
}