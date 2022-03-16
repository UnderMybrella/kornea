@file:Suppress("unused", "NOTHING_TO_INLINE")

package dev.brella.kornea.errors.common

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.annotations.ChangedSince
import dev.brella.kornea.base.common.DataCloseable
import dev.brella.kornea.base.common.Optional
import dev.brella.kornea.base.common.getOrElseRun
import dev.brella.kornea.base.common.use
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline
import kotlin.math.absoluteValue
import kotlin.native.concurrent.ThreadLocal

@JvmInline
public value class KorneaResult<out T> @PublishedApi internal constructor(
    @PublishedApi
    internal val value: Any?
) {
    @Suppress("NOTHING_TO_INLINE")
    public companion object {
        /**
         * Creates an instance of [KorneaResult] that indicates a successful result
         */
        @AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
        public inline fun <T> success(value: T): KorneaResult<T> = KorneaResult(value)

        /**
         * Creates an instance of [KorneaResult] that indicates a successful result, or [Empty] if [value] is null
         */
        @AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
        public inline fun <T> successOrEmpty(value: T?): KorneaResult<T> =
            if (value == null) KorneaResult(Empty.of())
            else KorneaResult(value)

        public inline fun <T> successOrCatch(block: () -> T): KorneaResult<T> =
            try {
                success(block())
            } catch (th: Throwable) {
                thrown(th)
            }

        @AvailableSince(KorneaErrors.VERSION_3_4_1_INDEV)
        public inline fun <T> failure(): KorneaResult<T> =
            KorneaResult(Failure.of())

        public inline fun <T> failure(failure: Failure): KorneaResult<T> =
            KorneaResult(failure)

        public inline fun <T> empty(): KorneaResult<T> =
            KorneaResult(Empty.of())

        public inline fun <T> failedPredicate(): KorneaResult<T> =
            KorneaResult(Empty.ofFailedPredicate())

        public inline fun <T> typeCastEmpty(): KorneaResult<T> =
            KorneaResult(Empty.ofTypeCast())

        public inline fun <T, E : Throwable> thrown(
            exception: E,
            cause: Failure? = null
        ): KorneaResult<T> =
            KorneaResult(WithException.of(exception, cause))

        public inline fun <T> errorAsIllegalArgument(
            errorCode: Int,
            errorMessage: String,
            cause: Failure? = null,
            generateStacktraceOnCreation: Boolean = WithErrorDetails.DEFAULT_GENERATE_STACKTRACE_ON_CREATION,
            includeResultCodeInError: Boolean = WithErrorDetails.DEFAULT_INCLUDE_RESULT_CODE_IN_ERROR
        ): KorneaResult<T> = KorneaResult(
            WithErrorDetails.asIllegalArgument(
                errorCode,
                errorMessage,
                cause,
                generateStacktraceOnCreation,
                includeResultCodeInError
            )
        )

        public inline fun <T> errorAsIllegalState(
            errorCode: Int,
            errorMessage: String,
            cause: Failure? = null,
            generateStacktraceOnCreation: Boolean = WithErrorDetails.DEFAULT_GENERATE_STACKTRACE_ON_CREATION,
            includeResultCodeInError: Boolean = WithErrorDetails.DEFAULT_INCLUDE_RESULT_CODE_IN_ERROR
        ): KorneaResult<T> = KorneaResult(
            WithErrorDetails.asIllegalState(
                errorCode,
                errorMessage,
                cause,
                generateStacktraceOnCreation,
                includeResultCodeInError
            )
        )

        public fun <T, E : Throwable> error(
            errorCode: Int,
            errorMessage: String,
            exception: E,
            cause: Failure? = null
        ): KorneaResult<T> = KorneaResult(WithErrorDetails.of(errorCode, errorMessage, exception, cause))

        public fun <T, E : Throwable> error(
            errorCode: Int,
            errorMessage: String,
            supplier: (WithErrorCode) -> E,
            cause: Failure? = null
        ): KorneaResult<T> = KorneaResult(WithErrorDetails.of(errorCode, errorMessage, supplier, cause))

        public inline fun <T> foldingMutableListOf(): KorneaResult<MutableList<T>> =
            success(ArrayList())

        public inline fun <T, R> fold(
            iterable: Iterable<T>,
            operation: (acc: KorneaResult<MutableList<R>>, T) -> KorneaResult<MutableList<R>>
        ): KorneaResult<List<R>> =
            foldTo(iterable, ArrayList(), operation)

        public inline fun <T, R, L : MutableList<R>> foldTo(
            iterable: Iterable<T>,
            initial: L,
            operation: (acc: KorneaResult<L>, T) -> KorneaResult<L>
        ): KorneaResult<List<R>> =
            iterable.fold(success(initial), operation)

        public fun dirtyImplementationString(impl: KorneaResult<*>): String =
            "Bad implementation of KorneaResult by `${impl::class}`; you need to implement either Success<T> or Failure! (Value was $impl)"
    }

    public interface Failure {
        public companion object {
            @AvailableSince(KorneaErrors.VERSION_3_4_1_INDEV)
            public fun of(): Failure =
                Base

            @AvailableSince(KorneaErrors.VERSION_3_4_1_INDEV)
            public fun <T> of(): KorneaResult<T> = KorneaResult(Base)
        }

        private object Base : Failure {
            override fun asException(): Throwable =
                throw IllegalStateException("Result failed")

            override fun toString(): String =
                "Failure()"
        }

        public fun asException(): Throwable
    }

    public interface Empty : Failure {
        public interface FailedPredicate : Empty

        @AvailableSince(KorneaErrors.VERSION_3_2_0_INDEV)
        public interface Null : Empty

        @AvailableSince(KorneaErrors.VERSION_3_2_0_INDEV)
        public interface Undefined : Empty
        public interface TypeCastEmpty : Empty

        public companion object {
            public fun of(): Empty =
                Base

            public fun ofFailedPredicate(): Empty =
                FailedPredicateBase

            public fun ofTypeCast(): Empty =
                TypeCastEmptyBase

            @AvailableSince(KorneaErrors.VERSION_3_2_0_INDEV)
            public fun ofNull(): Empty =
                NullBase

            @AvailableSince(KorneaErrors.VERSION_3_2_0_INDEV)
            public fun ofUndefined(): Empty =
                UndefinedBase
        }

        private object Base : Empty {
            override fun asException(): Throwable =
                throw IllegalStateException("Empty Result")

            override fun toString(): String =
                "Empty()"
        }

        private object FailedPredicateBase : FailedPredicate {
            override fun asException(): Throwable =
                throw IllegalStateException("Failed predicate")

            override fun toString(): String =
                "FailedPredicate()"
        }

        private object TypeCastEmptyBase : TypeCastEmpty {
            override fun asException(): Throwable =
                throw IllegalStateException("Failed cast")

            override fun toString(): String =
                "TypeCast()"
        }

        private object NullBase : Null {
            override fun asException(): Throwable =
                throw IllegalArgumentException("Was null")

            override fun toString(): String =
                "Null()"
        }

        private object UndefinedBase : Undefined {
            override fun asException(): Throwable =
                throw IllegalArgumentException("Was undefined")

            override fun toString(): String =
                "Undefined()"
        }
    }

    public interface WithCause : Failure {
        public val cause: Failure?

        public infix fun withCause(newCause: Failure?): WithCause
    }

    public interface WithException<out E : Throwable> : Failure {
        public companion object {
            public fun <E : Throwable> of(exception: E, cause: Failure? = null): WithException<E> =
                Base(exception, cause)
        }

        private class Base<E : Throwable>(override val exception: E, override val cause: Failure?) : WithException<E>,
            WithCause {
            override fun toString(): String = "WithException(exception=$exception)"

            override fun withCause(newCause: Failure?): WithCause =
                Base(exception, newCause)

            override fun <R : Throwable> withException(newException: R): WithException<R> =
                Base(newException, cause)
        }

        public val exception: E

        override fun asException(): Throwable = exception

        @AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
        public infix fun <R : Throwable> withException(newException: R): WithException<R>
    }

    @ChangedSince(KorneaErrors.VERSION_3_1_0_INDEV)
    public interface WithErrorCode : Failure {
        public val errorCode: Int

        @AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
        public infix fun withErrorCode(newErrorCode: Int): WithErrorCode
    }

    @AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
    public interface WithErrorMessage : Failure {
        public val errorMessage: String

        @AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
        public infix fun withErrorMessage(newErrorMessage: String): WithErrorCode
    }

    @AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
    public interface WithErrorDetails : WithErrorCode,
        WithErrorMessage {
        @Suppress("NOTHING_TO_INLINE")
        @ThreadLocal
        public companion object {
            public var DEFAULT_GENERATE_STACKTRACE_ON_CREATION: Boolean = false
            public var DEFAULT_INCLUDE_RESULT_CODE_IN_ERROR: Boolean = false

            @Suppress("NOTHING_TO_INLINE")
            public inline fun formatErrorMessage(
                errorCode: Int,
                errorMessage: String,
                includeResultCodeInError: Boolean = DEFAULT_INCLUDE_RESULT_CODE_IN_ERROR
            ): String =
                if (includeResultCodeInError) buildErrorMessageWithCode(
                    errorCode,
                    errorMessage
                ) else errorMessage

            @Suppress("NOTHING_TO_INLINE")
            public inline fun buildErrorMessageWithCode(errorCode: Int, errorMessage: String): String = buildString {
                append(errorMessage)
                append(" (0x")
                append(errorCode.absoluteValue.toString(16))
                append(")")
            }

            public inline fun illegalArgumentException(
                errorCode: Int,
                errorMessage: String,
                cause: Throwable? = null,
                includeResultCodeInError: Boolean = DEFAULT_INCLUDE_RESULT_CODE_IN_ERROR
            ): IllegalArgumentException =
                IllegalArgumentException(
                    formatErrorMessage(
                        errorCode,
                        errorMessage,
                        includeResultCodeInError
                    ), cause
                )

            public inline fun illegalArgument(error: WithErrorDetails): IllegalArgumentException =
                illegalArgumentException(
                    error.errorCode,
                    error.errorMessage,
                    ((error as? WithCause)?.cause as? WithException<*>)?.exception,
                    false
                )

            public inline fun illegalArgumentWithResultCode(error: WithErrorDetails): IllegalArgumentException =
                illegalArgumentException(
                    error.errorCode,
                    error.errorMessage,
                    ((error as? WithCause)?.cause as? WithException<*>)?.exception,
                    true
                )

            public inline fun illegalStateException(
                errorCode: Int,
                errorMessage: String,
                cause: Throwable? = null,
                includeResultCodeInError: Boolean = DEFAULT_INCLUDE_RESULT_CODE_IN_ERROR
            ): IllegalStateException =
                IllegalStateException(
                    formatErrorMessage(
                        errorCode,
                        errorMessage,
                        includeResultCodeInError
                    ), cause
                )

            public inline fun illegalState(error: WithErrorDetails): IllegalStateException =
                illegalStateException(
                    error.errorCode,
                    error.errorMessage,
                    ((error as? WithCause)?.cause as? WithException<*>)?.exception,
                    false
                )

            public inline fun illegalStateWithResultCode(error: WithErrorDetails): IllegalStateException =
                illegalStateException(
                    error.errorCode,
                    error.errorMessage,
                    ((error as? WithCause)?.cause as? WithException<*>)?.exception,
                    true
                )

            public inline fun asIllegalArgument(
                errorCode: Int,
                errorMessage: String,
                cause: Failure? = null,
                generateStacktraceOnCreation: Boolean = DEFAULT_GENERATE_STACKTRACE_ON_CREATION,
                includeResultCodeInError: Boolean = DEFAULT_INCLUDE_RESULT_CODE_IN_ERROR
            ): Failure =
                if (generateStacktraceOnCreation) {
                    val e =
                        illegalArgumentException(
                            errorCode,
                            errorMessage,
                            (cause as? WithException<*>)?.exception,
                            includeResultCodeInError
                        )

                    of(
                        errorCode,
                        errorMessage,
                        e,
                        cause
                    )
                } else {
                    if (includeResultCodeInError) {
                        of(
                            errorCode,
                            errorMessage,
                            this::illegalArgument,
                            cause
                        )
                    } else {
                        of(
                            errorCode,
                            errorMessage,
                            this::illegalArgumentWithResultCode,
                            cause
                        )
                    }
                }

            public inline fun asIllegalState(
                errorCode: Int,
                errorMessage: String,
                cause: Failure? = null,
                generateStacktraceOnCreation: Boolean = DEFAULT_GENERATE_STACKTRACE_ON_CREATION,
                includeResultCodeInError: Boolean = DEFAULT_INCLUDE_RESULT_CODE_IN_ERROR
            ): Failure =
                if (generateStacktraceOnCreation) {
                    val e =
                        illegalStateException(
                            errorCode,
                            errorMessage,
                            (cause as? WithException<*>)?.exception,
                            includeResultCodeInError
                        )

                    of(
                        errorCode,
                        errorMessage,
                        e,
                        cause
                    )
                } else {
                    if (includeResultCodeInError) {
                        of(
                            errorCode,
                            errorMessage,
                            this::illegalState,
                            cause
                        )
                    } else {
                        of(
                            errorCode,
                            errorMessage,
                            this::illegalStateWithResultCode,
                            cause
                        )
                    }
                }

            public fun <E : Throwable> of(
                errorCode: Int,
                errorMessage: String,
                exception: E,
                cause: Failure? = null
            ): Failure =
                Base(
                    errorCode,
                    errorMessage,
                    cause,
                    exception,
                    null
                )

            public fun <E : Throwable> of(
                errorCode: Int,
                errorMessage: String,
                supplier: (WithErrorDetails) -> E,
                cause: Failure? = null
            ): Failure =
                Base(
                    errorCode,
                    errorMessage,
                    cause,
                    null,
                    supplier
                )
        }

        private class Base<out E : Throwable>(
            override val errorCode: Int,
            override val errorMessage: String,
            override val cause: Failure?,
            private val _exception: E?,
            private val _exceptionSupplier: ((WithErrorDetails) -> E)?
        ) : WithErrorDetails,
            WithException<E>,
            WithCause {
            @Suppress("OVERRIDE_BY_INLINE")
            override val exception: E
                inline get() = _exception ?: _exceptionSupplier!!.invoke(this)

            override fun withCause(newCause: Failure?): WithCause =
                Base(
                    errorCode,
                    errorMessage,
                    newCause,
                    _exception,
                    _exceptionSupplier
                )

            override fun withErrorCode(newErrorCode: Int): WithErrorCode =
                Base(
                    newErrorCode,
                    errorMessage,
                    cause,
                    _exception,
                    _exceptionSupplier
                )

            override fun withErrorMessage(newErrorMessage: String): WithErrorCode =
                Base(
                    errorCode,
                    newErrorMessage,
                    cause,
                    _exception,
                    _exceptionSupplier
                )

            override fun <R : Throwable> withException(newException: R): WithException<R> =
                Base(
                    errorCode,
                    errorMessage,
                    cause,
                    newException,
                    null
                )

            override fun toString(): String =
                "WithErrorDetails(errorCode=$errorCode, errorMessage='$errorMessage', cause=$cause, exception=$_exception, exceptionSupplier=$_exceptionSupplier)"

            init {
                require(_exception != null || _exceptionSupplier != null) { "Invalid Error combination WithErrorDetails(errorCode=$errorCode, errorMessage=$errorMessage, exception=null, exceptionSupplier=null)" }
            }
        }
    }

    @AvailableSince(KorneaErrors.VERSION_3_2_0_INDEV)
    public sealed interface WithPayload<T> : Failure {

    }

    // discovery

    public val isSuccess: Boolean get() = value !is Failure
    public val isFailure: Boolean get() = value is Failure
    public val isEmpty: Boolean get() = value is Empty
    public val isFailureWithCause: Boolean get() = value is WithCause
    public val isFailureWithException: Boolean get() = value is WithException<*>
    public val isWithErrorCode: Boolean get() = value is WithErrorCode
    public val isWithErrorMessage: Boolean get() = value is WithErrorMessage
    public val isWithErrorDetails: Boolean get() = value is WithErrorDetails
    public val isWithPayload: Boolean get() = value is WithPayload<*>

    // value & exception retrieval

    @Suppress("UNCHECKED_CAST")
    public inline fun getOrNull(): T? =
        when {
            isFailure -> null
            else -> value as T
        }

    public inline fun failureOrNull(): Failure? =
        when (value) {
            is Failure -> value
            else -> null
        }
}

@PublishedApi
internal fun KorneaResult<*>.throwOnFailure() {
    if (value is KorneaResult.Failure) throw value.asException()
}

public inline fun KorneaResult.Failure.hierarchy(): List<KorneaResult.Failure> =
    ArrayList<KorneaResult.Failure>().apply(this::hierarchyIn)

public inline fun KorneaResult.Failure.hierarchyIn(list: MutableList<KorneaResult.Failure>) {
    var self: KorneaResult.Failure? = this
    while (self is KorneaResult.WithCause) {
        list.add(self)
        self = self.cause
    }

    self?.let(list::add)
}

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
public inline fun <T> KorneaResult<T>.mapFailureCause(transform: (KorneaResult.Failure?) -> KorneaResult.Failure?): KorneaResult<T> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when (val failure = failureOrNull()) {
        is KorneaResult.WithCause -> KorneaResult.failure(failure withCause transform(failure.cause))
        else -> this
    }
}

@AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
public inline fun <T> KorneaResult<T>.mapFailureCause(cause: KorneaResult.Failure?): KorneaResult<T> =
    when (val failure = failureOrNull()) {
        is KorneaResult.WithCause -> KorneaResult.failure(failure withCause cause)
        else -> this
    }

@Suppress("UNCHECKED_CAST")
@AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
public inline fun <T> KorneaResult<T>.mapFailureRootCause(rootCause: KorneaResult.Failure?): KorneaResult<T> =
    when (val failure = failureOrNull()) {
        is KorneaResult.WithCause -> KorneaResult.failure(failure.hierarchy()
            .asReversed()
            .fold(rootCause) { cause, result ->
                (result as? KorneaResult.WithCause)?.withCause(cause) ?: result
            } ?: failure)

        else -> this
    }

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
public inline fun <T, E : Throwable> KorneaResult<T>.mapFailureException(transform: (Throwable) -> E): KorneaResult<T> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when (val failure = failureOrNull()) {
        is KorneaResult.WithException<*> -> KorneaResult.failure(failure withException transform(failure.exception))
        else -> this
    }
}

@AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
public inline fun <T, E : Throwable> KorneaResult<T>.mapFailureException(exception: E): KorneaResult<T> =
    when (val failure = failureOrNull()) {
        is KorneaResult.WithException<*> -> KorneaResult.failure(failure withException exception)
        else -> this
    }

@Suppress("UNCHECKED_CAST")
@AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
public inline fun <R, T : KorneaResult<R>> KorneaResult<T>.flatten(): KorneaResult<R> =
    if (isSuccess) value as T
    else asType()

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
public inline fun <T> KorneaResult<*>.asType(): KorneaResult<T> = this as KorneaResult<T>

@Suppress("UNCHECKED_CAST")
@ChangedSince(
    KorneaErrors.VERSION_3_1_0_INDEV,
    "Casting no longer creates a new instance, and when a cast fails instances have the chance to recover themselves"
)
public inline fun <T, reified R> KorneaResult<T>.cast(): KorneaResult<R> =
    if (isSuccess)
        if (value is R) this as KorneaResult<R>
        else KorneaResult.typeCastEmpty()
    else
        asType()

@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalContracts::class)
public inline fun <T, R> KorneaResult<T>.map(transform: (T) -> R): KorneaResult<R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }


    return if (isSuccess)
        KorneaResult(transform(value as T))
    else
        asType()
}

@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalContracts::class)
public inline fun <T, R> KorneaResult<T>.flatMap(transform: (T) -> KorneaResult<R>): KorneaResult<R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return if (isSuccess)
        transform(value as T)
    else
        asType()
}

@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_3_0_INDEV)
public inline fun <T> KorneaResult<T>.flatMapOrSelf(transform: (T) -> KorneaResult<T>?): KorneaResult<T> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return if (isSuccess)
        when (val result = transform(value as T)) {
            null -> this
            else -> result
        }
    else
        asType()
}

@AvailableSince(KorneaErrors.VERSION_3_3_0_INDEV)
public inline fun <T, E> List<T>.foldResults(block: (element: T) -> KorneaResult<E>): KorneaResult<List<E>> =
    KorneaResult.fold(this) { acc, element ->
        acc.flatMapOrSelf { list -> block(element).map { list.add(it); list } }
    }

@AvailableSince(KorneaErrors.VERSION_2_1_0_ALPHA)
public suspend inline fun <T : DataCloseable, reified R> KorneaResult<T>.useAndMap(block: (T) -> R): KorneaResult<R> =
    map { t -> t.use(block) }

@AvailableSince(KorneaErrors.VERSION_2_1_0_ALPHA)
public suspend inline fun <T : DataCloseable, reified R> KorneaResult<T>.useAndFlatMap(block: (T) -> KorneaResult<R>): KorneaResult<R> =
    flatMap { t -> t.use(block) }


@AvailableSince(KorneaErrors.VERSION_2_1_0_ALPHA)
public inline fun <T> Optional<KorneaResult<T>>.flatten(): KorneaResult<T> =
    getOrElseRun { KorneaResult.empty() }

@Suppress("UNCHECKED_CAST")
@AvailableSince(KorneaErrors.VERSION_2_1_0_ALPHA)
public inline fun <T> KorneaResult<Optional<T>>.filter(): KorneaResult<T> =
    if (isSuccess) {
        val opt = value as Optional<T>
        if (opt.isPresent) KorneaResult.success(opt.value)
        else KorneaResult.empty()
    } else {
        asType()
    }