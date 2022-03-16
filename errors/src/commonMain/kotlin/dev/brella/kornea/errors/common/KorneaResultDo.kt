package dev.brella.kornea.errors.common

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.annotations.ChangedSince
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KClass


/** Run when this result is any failed state */
@OptIn(ExperimentalContracts::class)
@ChangedSince(
    KorneaErrors.VERSION_3_0_2_INDEV,
    "doOnFailure now returns the KorneaResult after processing, and block returns a Unit rather than Nothing. Previous functionality can be achieved with getOrBreak"
)
public inline fun <T> KorneaResult<T>.doOnFailure(block: (KorneaResult.Failure) -> Unit): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    failureOrNull()?.let(block)
    return this
}

/** Run when this result is specifically a known error */
@OptIn(ExperimentalContracts::class)
@ChangedSince(
    KorneaErrors.VERSION_3_1_0_INDEV,
    "The error code result has been broken up into three interfaces; you may want doWithErrorDetails"
)
public inline fun <T> KorneaResult<T>.doWithErrorCode(block: (KorneaResult.WithErrorCode) -> Unit): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    when (val failure = failureOrNull()) {
        is KorneaResult.WithErrorCode -> block(failure)
    }

    return this
}

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
public inline fun <T> KorneaResult<T>.doWithErrorMessage(block: (KorneaResult.WithErrorMessage) -> Unit): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    when (val failure = failureOrNull()) {
        is KorneaResult.WithErrorMessage -> block(failure)
    }

    return this
}

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
public inline fun <T> KorneaResult<T>.doWithErrorDetails(block: (KorneaResult.WithErrorDetails) -> Unit): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    when (val failure = failureOrNull()) {
        is KorneaResult.WithErrorDetails -> block(failure)
    }

    return this
}

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_2_0_INDEV)
public inline fun <T> KorneaResult<T>.doWithCause(block: (KorneaResult.WithCause) -> KorneaResult<T>): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    when (val failure = failureOrNull()) {
        is KorneaResult.WithCause -> block(failure)
    }

    return this
}

@OptIn(ExperimentalContracts::class)
@ChangedSince(KorneaErrors.VERSION_3_2_0_INDEV, "[block] now accepts the empty instance")
public inline fun <T> KorneaResult<T>.doOnEmpty(block: (KorneaResult.Empty) -> Unit): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    when (val failure = failureOrNull()) {
        is KorneaResult.Empty -> block(failure)
    }

    return this
}

@OptIn(ExperimentalContracts::class)
public inline fun <T> KorneaResult<T>.doOnThrown(block: (KorneaResult.WithException<*>) -> Unit): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    when (val failure = failureOrNull()) {
        is KorneaResult.WithException<*> -> block(failure)
    }

    return this
}

@OptIn(ExperimentalContracts::class)
@Suppress("UNCHECKED_CAST")
public inline fun <T, reified E : Throwable> KorneaResult<T>.doOnTypedThrown(block: (KorneaResult.WithException<E>) -> Unit): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    when (val failure = failureOrNull()) {
        is KorneaResult.WithException<*> -> if (failure.exception is E) block(failure as KorneaResult.WithException<E>)
    }

    return this
}

@OptIn(ExperimentalContracts::class)
@Suppress("UNCHECKED_CAST")
public inline fun <T, E : Throwable> KorneaResult<T>.doOnTypedThrown(
    klass: KClass<E>,
    block: (KorneaResult.WithException<E>) -> Unit
): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    when (val failure = failureOrNull()) {
        is KorneaResult.WithException<*> -> if (klass.isInstance(failure.exception)) block(failure as KorneaResult.WithException<E>)
    }

    return this
}

@OptIn(ExperimentalContracts::class)
public inline fun <T> KorneaResult<T>.doOnSuccess(block: (T) -> Unit): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    getOrNull()?.let(block)
    return this
}

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_1_1_INDEV)
public suspend inline fun <T> KorneaResult<T>.doOnSuccessAsync(@Suppress("REDUNDANT_INLINE_SUSPEND_FUNCTION_TYPE") block: suspend (T) -> Unit): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    val value = getOrNull()
    if (value != null) block(value)

    return this
}

/** This time, with Results */

/** Run when this result is any failed state */
@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_0_0_ALPHA)
public inline fun <T> KorneaResult<T>.doOnFailureResult(block: (KorneaResult<*>) -> Unit): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    if (isFailure) block(this)
    return this
}

/** Run when this result is specifically a known error */
@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_0_0_ALPHA)
public inline fun <T> KorneaResult<T>.doWithErrorCodeResult(block: (KorneaResult<*>) -> Unit): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    if (isWithErrorCode) block(this)
    return this
}

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_0_0_ALPHA)
public inline fun <T> KorneaResult<T>.doWithErrorMessageResult(block: (KorneaResult<*>) -> Unit): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    if (isWithErrorMessage) block(this)
    return this
}

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_0_0_ALPHA)
public inline fun <T> KorneaResult<T>.doWithErrorDetailsResult(block: (KorneaResult<*>) -> Unit): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    if (isWithErrorDetails) block(this)
    return this
}

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_0_0_ALPHA)
public inline fun <T> KorneaResult<T>.doWithCauseResult(block: (KorneaResult<*>) -> KorneaResult<T>): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    if (isFailureWithCause) block(this)
    return this
}

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_0_0_ALPHA)
public inline fun <T> KorneaResult<T>.doOnEmptyAsResult(block: (KorneaResult<*>) -> Unit): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    if (isEmpty) block(this)
    return this
}

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_0_0_ALPHA)
public inline fun <T> KorneaResult<T>.doOnThrownAsResult(block: (KorneaResult<*>) -> Unit): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    if (isFailureWithException) block(this)
    return this
}

@OptIn(ExperimentalContracts::class)
@Suppress("UNCHECKED_CAST")
@AvailableSince(KorneaErrors.VERSION_3_0_0_ALPHA)
public inline fun <T, reified E : Throwable> KorneaResult<T>.doOnTypedThrownWithResult(block: (KorneaResult<*>) -> Unit): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    when (val failure = failureOrNull()) {
        is KorneaResult.WithException<*> -> if (failure.exception is E) block(this)
    }

    return this
}

@OptIn(ExperimentalContracts::class)
@Suppress("UNCHECKED_CAST")
@AvailableSince(KorneaErrors.VERSION_3_0_0_ALPHA)
public inline fun <T, E : Throwable> KorneaResult<T>.doOnTypedThrownWithResult(
    klass: KClass<E>,
    block: (KorneaResult<*>) -> Unit
): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    when (val failure = failureOrNull()) {
        is KorneaResult.WithException<*> -> if (klass.isInstance(failure.exception)) block(this)
    }

    return this
}

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_0_0_ALPHA)
public inline fun <T> KorneaResult<T>.doOnSuccessWithResult(block: (KorneaResult<T>) -> Unit): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    if (isSuccess) block(this)
    return this
}

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_0_0_ALPHA)
public suspend inline fun <T> KorneaResult<T>.doOnSuccessAsyncWithResult(@Suppress("REDUNDANT_INLINE_SUSPEND_FUNCTION_TYPE") block: suspend (KorneaResult<T>) -> Unit): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    if (isSuccess) block(this)
    return this
}