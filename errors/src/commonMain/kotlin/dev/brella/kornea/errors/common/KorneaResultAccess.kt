package dev.brella.kornea.errors.common

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.base.common.Optional
import dev.brella.kornea.base.common.empty
import dev.brella.kornea.base.common.of
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Suppress("UNCHECKED_CAST")
@AvailableSince(KorneaErrors.VERSION_2_1_0_ALPHA)
public inline fun <T> KorneaResult<T>.getAsOptional(): Optional<T> =
    if (isSuccess) Optional.of(value as T) else Optional.empty()

@Suppress("UNCHECKED_CAST")
public inline fun <T> KorneaResult<T>.getOrThrow(): T {
    throwOnFailure()
    return value as T
}

@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalContracts::class)
public inline fun <R, T : R> KorneaResult<T>.getOrElse(onFailure: (failure: KorneaResult.Failure) -> R): R {
    contract {
        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
    }

    return when (val failure = failureOrNull()) {
        null -> value as T
        else -> onFailure(failure)
    }
}

@Suppress("UNCHECKED_CAST")
public inline fun <R, T : R> KorneaResult<T>.getOrDefault(default: R): R = if (isSuccess) value as T else default

@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalContracts::class)
public inline fun <R, T : R> KorneaResult<T>.getOrElseRun(block: () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return if (isSuccess) value as T else block()
}

@OptIn(ExperimentalContracts::class)
public inline fun <R, T : R> KorneaResult<T>.orElse(onFailure: (failure: KorneaResult.Failure) -> KorneaResult<R>): KorneaResult<R> {
    contract {
        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
    }

    return when (val failure = failureOrNull()) {
        null -> this
        else -> onFailure(failure)
    }
}

public inline fun <R, T : R> KorneaResult<T>.orDefault(default: KorneaResult<R>): KorneaResult<R> =
    if (isSuccess) this else default

/**
 * Returns the value stored on a success, or runs [onFailure] when in a fail state.
 *
 * Fail states must not continue execution after they are called (ie: must return/shutdown/throw)
 */
@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_0_2_INDEV)
public inline fun <T> KorneaResult<T>.getOrBreak(onFailure: (KorneaResult<*>) -> Nothing): T {
    contract {
        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
    }

    return if (isSuccess)
        this as T
    else
        onFailure(this)
}

/**
 * Returns the value stored on a success, or runs [onFailure] when in a fail state.
 *
 * Fail states must not continue execution after they are called (ie: must return/shutdown/throw)
 */
@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_0_2_INDEV)
public inline fun <T> KorneaResult<T>.getOrBreakWithFailure(onFailure: (KorneaResult.Failure) -> Nothing): T {
    contract {
        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
    }

    return when (val failure = failureOrNull()) {
        null -> this as T
        else -> onFailure(failure)
    }
}