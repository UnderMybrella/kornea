package dev.brella.kornea.errors.common

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.base.common.Optional
import dev.brella.kornea.base.common.map

@Deprecated(
    "Consumption is no longer supported, as instances of results are now always inline classes",
    ReplaceWith("getOrThrow()")
)
public inline fun <T> KorneaResult<T>.consumeAndGet(dataHashCode: Int?): T = getOrThrow()

@Deprecated(
    "Consumption is no longer supported, as instances of results are now always inline classes",
    ReplaceWith("getOrNull()")
)
public inline fun <T> KorneaResult<T>.consumeAndGetOrNull(dataHashCode: Int?): T? = getOrNull()

@Deprecated(
    "Consumption is no longer supported, as instances of results are now always inline classes",
    ReplaceWith("getOrDefault(default)")
)
public inline fun <T> KorneaResult<T>.consumeAndGetOrElse(dataHashCode: Int?, default: T): T = getOrDefault(default)

@Deprecated(
    "Consumption is no longer supported, as instances of results are now always inline classes",
    ReplaceWith("getOrNull()")
)
public inline fun <T> KorneaResult<T>.consumeAndGetOrNull(): T? = getOrNull()

@Deprecated(
    "Consumption is no longer supported, as instances of results are now always inline classes",
    ReplaceWith("getOrDefault(default)")
)
public inline fun <T> KorneaResult<T>.consumeAndGetOrElse(default: T): T = getOrDefault(default)

@Deprecated("Replace with getOrElse", ReplaceWith("getOrElse(block)"))
public inline fun <T> KorneaResult<T>.getOrElseTransform(block: (KorneaResult.Failure) -> T): T =
    getOrElse(block)

/**
 * Returns the value stored on a success, or runs [onFailure] when in a fail state.
 *
 * Fail states must not continue execution after they are called (ie: must return/shutdown/throw)
 */
@Deprecated(
    "Consumption is no longer supported, as instances of results are now always inline classes",
    ReplaceWith("getOrBreak(onFailure)")
)
public inline fun <T> KorneaResult<T>.consumeAndGetOrBreak(onFailure: (KorneaResult<*>) -> Nothing): T =
    getOrBreak(onFailure)


/**
 * Returns the value stored on a success, or runs [onFailure] when in a fail state.
 *
 * Fail states must not continue execution after they are called (ie: must return/shutdown/throw)
 */
@Deprecated(
    "Consumption is no longer supported, as instances of results are now always inline classes",
    ReplaceWith("getOrBreak(onFailure)")
)
public inline fun <T> KorneaResult<T>.consumeOnSuccessGetOrBreak(
    dataHashCode: Int? = null,
    onFailure: (KorneaResult<*>) -> Nothing
): T = getOrBreak(onFailure)

@Deprecated("Structure changed, removing usefulness of contracts", ReplaceWith("result.getOrThrow()"))
public inline fun <T : Any> requireSuccessful(result: KorneaResult<T>): T =
    result.getOrThrow()

@Deprecated("Structure changed, removing usefulness of contracts", ReplaceWith("result.getOrThrow()"))
public inline fun <T : Any> requireSuccessful(result: KorneaResult<T>, lazyMessage: () -> Any): T =
    result.getOrThrow()

@Deprecated("Structure changed, removing usefulness of contracts", ReplaceWith("isSuccess"))
public inline fun KorneaResult<*>.isSuccessful(): Boolean =
    isSuccess

@Deprecated("Structure changed, removing usefulness of contracts", ReplaceWith("isFailure"))
public inline fun KorneaResult<*>.isFailure(): Boolean =
    isFailure

@AvailableSince(KorneaErrors.VERSION_2_1_0_ALPHA)
@Deprecated(
    "Consumption is no longer supported, as instances of results are now always inline classes",
    ReplaceWith("block(this)")
)
public inline fun <T : KorneaResult<*>, R> T.consume(block: (T) -> R): R = block(this)

@AvailableSince(KorneaErrors.VERSION_2_1_0_ALPHA)
@Deprecated(
    "Consumption is no longer supported, as instances of results are now always inline classes",
    ReplaceWith("block(this)")
)
public inline fun <T : KorneaResult<*>, R> T.consume(dataHashCode: Int?, block: (T) -> R): R = block(this)


@AvailableSince(KorneaErrors.VERSION_2_1_0_ALPHA)
@Deprecated(
    "Consumption is no longer supported, as instances of results are now always inline classes",
    ReplaceWith("getAsOptional().map(block)", "dev.brella.kornea.base.common.map")
)
public inline fun <T, R> KorneaResult<T>.consumeInner(block: (T) -> R): Optional<R> =
    getAsOptional().map(block)