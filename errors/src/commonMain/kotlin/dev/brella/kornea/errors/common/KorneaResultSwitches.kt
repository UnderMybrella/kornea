package dev.brella.kornea.errors.common

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.annotations.ChangedSince
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KClass


@OptIn(ExperimentalContracts::class)
public inline fun <R, T : R> KorneaResult<T>.switchIfFailure(block: (KorneaResult.Failure) -> KorneaResult<R>): KorneaResult<R> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (val failure = failureOrNull()) {
        null -> this
        else -> block(failure)
    }
}

@OptIn(ExperimentalContracts::class)
@ChangedSince(KorneaErrors.VERSION_3_2_0_INDEV, "[block] now takes the empty instance")
public inline fun <R, T : R> KorneaResult<T>.switchIfEmpty(block: (empty: KorneaResult.Empty) -> KorneaResult<R>): KorneaResult<R> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (val failure = failureOrNull()) {
        is KorneaResult.Empty -> block(failure)
        else -> this
    }
}

@OptIn(ExperimentalContracts::class)
@ChangedSince(
    KorneaErrors.VERSION_3_1_0_INDEV,
    "WithErrorCode has been broken up into three separate interfaces; you may want switchIfHasErrorDetails"
)
public inline fun <R, T : R> KorneaResult<T>.switchIfHasErrorCode(block: (KorneaResult.WithErrorCode) -> KorneaResult<R>): KorneaResult<R> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (val failure = failureOrNull()) {
        is KorneaResult.WithErrorCode -> block(failure)
        else -> this
    }
}

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
public inline fun <R, T : R> KorneaResult<T>.switchIfHasErrorMessage(block: (KorneaResult.WithErrorMessage) -> KorneaResult<R>): KorneaResult<R> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (val failure = failureOrNull()) {
        is KorneaResult.WithErrorMessage -> block(failure)
        else -> this
    }
}

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
public inline fun <R, T : R> KorneaResult<T>.switchIfHasErrorDetails(block: (KorneaResult.WithErrorDetails) -> KorneaResult<R>): KorneaResult<R> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (val failure = failureOrNull()) {
        is KorneaResult.WithErrorDetails -> block(failure)
        else -> this
    }
}

@OptIn(ExperimentalContracts::class)
public inline fun <R, T : R> KorneaResult<T>.switchIfHasException(block: (KorneaResult.WithException<*>) -> KorneaResult<R>): KorneaResult<R> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (val failure = failureOrNull()) {
        is KorneaResult.WithException<*> -> block(failure)
        else -> this
    }
}

@OptIn(ExperimentalContracts::class)
@Suppress("UNCHECKED_CAST")
public inline fun <R, T : R, reified E : Throwable> KorneaResult<T>.switchIfHasTypedException(block: (KorneaResult.WithException<E>) -> KorneaResult<R>): KorneaResult<R> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (val failure = failureOrNull()) {
        is KorneaResult.WithException<*> ->
            if (failure.exception is E) block(failure as KorneaResult.WithException<E>)
            else this

        else -> this
    }
}

@OptIn(ExperimentalContracts::class)
@Suppress("UNCHECKED_CAST")
public inline fun <R, T : R, E : Throwable> KorneaResult<T>.switchIfHasTypedException(
    klass: KClass<E>,
    block: (KorneaResult.WithException<E>) -> KorneaResult<R>
): KorneaResult<R> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (val failure = failureOrNull()) {
        is KorneaResult.WithException<*> ->
            if (klass.isInstance(failure.exception)) block(failure as KorneaResult.WithException<E>)
            else this

        else -> this
    }
}

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_2_0_INDEV)
public inline fun <R, T : R> KorneaResult<T>.switchIfHasCause(block: (KorneaResult.WithCause) -> KorneaResult<R>): KorneaResult<R> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (val failure = failureOrNull()) {
        is KorneaResult.WithCause -> block(failure)
        else -> this
    }
}