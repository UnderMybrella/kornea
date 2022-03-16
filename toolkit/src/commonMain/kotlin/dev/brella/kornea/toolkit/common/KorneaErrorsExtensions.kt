package dev.brella.kornea.toolkit.common

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.errors.common.KorneaErrors
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.errors.common.flatMap

@Suppress("UNCHECKED_CAST")
public inline fun <R> KorneaResult<*>.filterToInstance(info: KorneaTypeChecker<R>): KorneaResult<R> =
    flatMap { t -> if (info.isInstance(t)) KorneaResult.success(t) as KorneaResult<R> else KorneaResult.typeCastEmpty() }

@Suppress("UNCHECKED_CAST")
public inline fun <R> KorneaResult<*>.filterToInstance(
    info: KorneaTypeChecker<R>,
    onEmpty: () -> KorneaResult<R>
): KorneaResult<R> =
    flatMap { t -> if (info.isInstance(t)) KorneaResult.success(t) as KorneaResult<R> else onEmpty() }

@Suppress("UNCHECKED_CAST")
@AvailableSince(KorneaErrors.VERSION_3_4_0_INDEV)
public inline fun <R> KorneaResult<*>.filterToInstance(
    default: KorneaResult<R>,
    info: KorneaTypeChecker<R>
): KorneaResult<R> =
    flatMap { t -> if (info.isInstance(t)) KorneaResult.success(t) as KorneaResult<R> else default }

@Suppress("UNCHECKED_CAST")
@AvailableSince(KorneaErrors.VERSION_3_4_0_INDEV)
public inline fun <T, R : T> KorneaResult<T>.filterToInstance(
    info: KorneaTypeChecker<R>,
    crossinline transform: (T) -> KorneaResult<R>
): KorneaResult<R> =
    flatMap { t -> if (info.isInstance(t)) KorneaResult.success(t) as KorneaResult<R> else transform(t) }