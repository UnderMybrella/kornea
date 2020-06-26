package dev.brella.kornea.toolkit.common

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.errors.common.KorneaErrors
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.errors.common.asType

@Suppress("UNCHECKED_CAST")
public inline fun <R> KorneaResult<*>.filterToInstance(info: KorneaTypeChecker<R>): KorneaResult<R> =
    when (this) {
        is KorneaResult.Success -> when {
            info.isInstance(get()) -> this as KorneaResult<R>
            this is KorneaResult.Success.FailedPredicateObserver -> onFilterToInstanceFailed()
            else -> KorneaResult.typeCastEmpty()
        }

        is KorneaResult.Failure -> asType()
        else -> KorneaResult.badImplementation(this)
    }

@Suppress("UNCHECKED_CAST")
public inline fun <R> KorneaResult<*>.filterToInstance(
    info: KorneaTypeChecker<R>,
    onEmpty: () -> KorneaResult<R>
): KorneaResult<R> =
    when (this) {
        is KorneaResult.Success -> if (info.isInstance(get())) this as KorneaResult<R> else onEmpty()
        is KorneaResult.Failure -> asType()
        else -> KorneaResult.badImplementation(this)
    }

@Suppress("UNCHECKED_CAST")
@AvailableSince(KorneaErrors.VERSION_3_4_0)
public inline fun <R> KorneaResult<*>.filterToInstance(
    default: KorneaResult<R>,
    info: KorneaTypeChecker<R>
): KorneaResult<R> =
    when (this) {
        is KorneaResult.Success -> if (info.isInstance(get())) this as KorneaResult<R> else default
        is KorneaResult.Failure -> asType()
        else -> KorneaResult.badImplementation(this)
    }

@Suppress("UNCHECKED_CAST")
@AvailableSince(KorneaErrors.VERSION_3_4_0)
public inline fun <T, R : T> KorneaResult<T>.filterToInstance(
    info: KorneaTypeChecker<R>,
    transform: (T) -> KorneaResult<R>
): KorneaResult<R> = when (this) {
    is KorneaResult.Success -> {
        val result = get()
        if (info.isInstance(result)) this as KorneaResult<R>
        else transform(result)
    }
    is KorneaResult.Failure -> asType()
    else -> KorneaResult.badImplementation(this)
}