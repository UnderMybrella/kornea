package dev.brella.kornea.errors.common

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.annotations.ChangedSince
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KClass
import kotlin.reflect.cast


@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_4_0_0_ALPHA)
public inline fun <T> KorneaResult<T>.withPayloadForFailure(block: (KorneaResult.Failure) -> Any?): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (val failure = failureOrNull()) {
        null -> this
        else -> KorneaResult<T>(failure withPayload block(failure))
    }
}

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_4_0_0_ALPHA)
public inline fun <reified F : KorneaResult.Failure, T> KorneaResult<T>.withPayloadForTypedFailure(block: (F) -> Any?): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (val failure = failureOrNull()) {
        is F -> KorneaResult<T>(failure withPayload block(failure))
        else -> this
    }
}

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_4_0_0_ALPHA)
public inline fun <F : KorneaResult.Failure, T> KorneaResult<T>.withPayloadForTypedFailure(klass: KClass<F>, block: (F) -> Any?): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    val failure = failureOrNull()
    if (failure != null && klass.isInstance(failure)) return KorneaResult<T>(failure withPayload block(klass.cast(failure)))

    return this
}

@OptIn(ExperimentalContracts::class)
@ChangedSince(KorneaErrors.VERSION_4_0_0_ALPHA)
public inline fun <T> KorneaResult<T>.withPayloadForEmpty(block: (empty: KorneaResult.Empty) -> Any?): KorneaResult<T> =
    withPayloadForTypedFailure(block)

@OptIn(ExperimentalContracts::class)
@ChangedSince(KorneaErrors.VERSION_4_0_0_ALPHA)
public inline fun <T> KorneaResult<T>.withPayloadForErrorCode(block: (KorneaResult.WithErrorCode) -> Any?): KorneaResult<T> =
    withPayloadForTypedFailure(block)

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_4_0_0_ALPHA)
public inline fun <T> KorneaResult<T>.withPayloadForErrorMessage(block: (KorneaResult.WithErrorMessage) -> Any?): KorneaResult<T> =
    withPayloadForTypedFailure(block)

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_4_0_0_ALPHA)
public inline fun <T> KorneaResult<T>.withPayloadForErrorDetails(block: (KorneaResult.WithErrorDetails) -> Any?): KorneaResult<T> =
    withPayloadForTypedFailure(block)

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_4_0_0_ALPHA)
public inline fun <T> KorneaResult<T>.withPayloadForException(block: (KorneaResult.WithException<*>) -> Any?): KorneaResult<T> =
    withPayloadForTypedFailure(block)

@OptIn(ExperimentalContracts::class)
@Suppress("UNCHECKED_CAST")
@AvailableSince(KorneaErrors.VERSION_4_0_0_ALPHA)
public inline fun <T, reified E : Throwable> KorneaResult<T>.withPayloadForTypedException(block: (KorneaResult.WithException<E>) -> Any?): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (val failure = failureOrNull()) {
        is KorneaResult.WithException<*> ->
            if (failure.exception is E) KorneaResult<T>(block(failure as KorneaResult.WithException<E>))
            else this

        else -> this
    }
}

@OptIn(ExperimentalContracts::class)
@Suppress("UNCHECKED_CAST")
@AvailableSince(KorneaErrors.VERSION_4_0_0_ALPHA)
public inline fun <R, T : R, E : Throwable> KorneaResult<T>.withPayloadForTypedException(
    klass: KClass<E>,
    block: (KorneaResult.WithException<E>) -> Any?
): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (val failure = failureOrNull()) {
        is KorneaResult.WithException<*> ->
            if (klass.isInstance(failure.exception)) KorneaResult<T>(block(failure as KorneaResult.WithException<E>))
            else this

        else -> this
    }
}


@AvailableSince(KorneaErrors.VERSION_4_0_0_ALPHA)
public inline fun <T> KorneaResult<T>.withPayloadForPayload(block: (KorneaResult.WithPayload<*>) -> Any?): KorneaResult<T> =
    withPayloadForTypedFailure(block)

@OptIn(ExperimentalContracts::class)
@Suppress("UNCHECKED_CAST")
@AvailableSince(KorneaErrors.VERSION_4_0_0_ALPHA)
public inline fun <R, T : R, reified P> KorneaResult<T>.withPayloadForTypedPayload(block: (KorneaResult.WithPayload<P>) -> Any?): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (val failure = failureOrNull()) {
        is KorneaResult.WithPayload<*> ->
            if (failure.payload is P) KorneaResult<T>(block(failure as KorneaResult.WithPayload<P>))
            else this

        else -> this
    }
}

@OptIn(ExperimentalContracts::class)
@Suppress("UNCHECKED_CAST")
@AvailableSince(KorneaErrors.VERSION_4_0_0_ALPHA)
public inline fun <R, T : R, P: Any> KorneaResult<T>.withPayloadForTypedPayload(
    klass: KClass<P>,
    block: (KorneaResult.WithPayload<P>) -> Any?
): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (val failure = failureOrNull()) {
        is KorneaResult.WithPayload<*> ->
            if (klass.isInstance(failure.payload)) KorneaResult<T>(block(failure as KorneaResult.WithPayload<P>))
            else this

        else -> this
    }
}