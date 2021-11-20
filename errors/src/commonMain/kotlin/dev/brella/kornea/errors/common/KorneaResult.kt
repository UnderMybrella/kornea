@file:Suppress("unused", "NOTHING_TO_INLINE")

package dev.brella.kornea.errors.common

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.annotations.ChangedSince
import dev.brella.kornea.annotations.ExperimentalKorneaErrors
import dev.brella.kornea.base.common.*
import dev.brella.kornea.config.common.Configurable
import dev.brella.kornea.config.common.config
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.native.concurrent.ThreadLocal
import kotlin.reflect.KClass

public interface KorneaResult<out T> {
    @Suppress("NOTHING_TO_INLINE")
    public companion object : Configurable.Base<KorneaResultConfig>(KorneaResultConfig.DEFAULT) {
        /**
         * Creates an instance of [KorneaResult] that indicates a successful result
         *
         * This method attempts to retrieve and check [KorneaResultConfig.usePooledResult] from the current coroutineContext to determine if the resulting instance will be an inlined class or not
         */
        @AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
        public suspend inline fun <T> success(value: T): KorneaResult<T> = success(value, config())

        /**
         * Creates an instance of [KorneaResult] that indicates a successful result
         *
         * This method checks [config#shouldInlineClasses][KorneaResultConfig.usePooledResult] to determine if the resulting instance will be an inlined class or not
         */
        @OptIn(ExperimentalKorneaErrors::class)
        @ChangedSince(KorneaErrors.VERSION_2_0_0_ALPHA)
        public inline fun <T> success(value: T, config: KorneaResultConfig? = null): KorneaResult<T> =
            success(value, (config ?: defaultConfig).usePooledResult)

        /**
         * Creates an instance of [KorneaResult] that indicates a successful result
         *
         * This method checks [usePooledResult] to determine if the resulting instance will be an inlined class or not
         */
        @ExperimentalKorneaErrors
        public inline fun <T> success(value: T, usePooledResult: Boolean): KorneaResult<T> =
            if (usePooledResult) successPooled(value)
            else successStable(value)

        public inline fun <T> successStable(value: T): KorneaResult<T> =
            Success.of(value)


        /**
         * Creates an instance of [KorneaResult] that indicates a successful result, or [Empty] if [value] is null
         *
         * This method attempts to retrieve and check [KorneaResultConfig.usePooledResult] from the current coroutineContext to determine if the resulting instance will be an inlined class or not
         */
        @AvailableSince(KorneaErrors.VERSION_2_0_0_ALPHA)
        public suspend inline fun <T> successOrEmpty(value: T?): KorneaResult<T> =
            successOrEmpty(value, config())

        /**
         * Creates an instance of [KorneaResult] that indicates a successful result, or [Empty] if [value] is null
         *
         * This method checks [config#shouldInlineClasses][KorneaResultConfig.usePooledResult] to determine if the resulting instance will be an inlined class or not
         */
        @OptIn(ExperimentalKorneaErrors::class)
        @AvailableSince(KorneaErrors.VERSION_3_2_0_INDEV)
        @ChangedSince(KorneaErrors.VERSION_2_0_0_ALPHA)
        public inline fun <T> successOrEmpty(value: T?, config: KorneaResultConfig? = null): KorneaResult<T> =
            successOrEmpty(value, (config ?: defaultConfig).usePooledResult)

        /**
         * Creates an instance of [KorneaResult] that indicates a successful result, or [Empty] if [value] is null
         *
         * This method checks [usePooledResult] to determine if the resulting instance will be an inlined class or not
         */
        @ExperimentalKorneaErrors
        @AvailableSince(KorneaErrors.VERSION_3_2_0_INDEV)
        public inline fun <T> successOrEmpty(value: T?, usePooledResult: Boolean): KorneaResult<T> =
            when {
                value == null -> Empty.ofNull()
                usePooledResult -> successPooled(value)
                else -> successStable(value)
            }

        @AvailableSince(KorneaErrors.VERSION_3_2_0_INDEV)
        public inline fun <T> successOrEmptyStable(value: T?): KorneaResult<T> =
            if (value == null) Empty.ofNull()
            else Success.of(value)

        public suspend inline fun <T> successOrCatch(block: () -> T): KorneaResult<T> =
            try {
                success(block())
            } catch (th: Throwable) {
                thrown(th)
            }

        public inline fun <T> successOrCatch(config: KorneaResultConfig? = null, block: () -> T): KorneaResult<T> =
            try {
                success(block(), config)
            } catch (th: Throwable) {
                thrown(th)
            }

        @AvailableSince(KorneaErrors.VERSION_3_4_1_INDEV)
        public inline fun <T> failure(): KorneaResult<T> =
            Failure.of<T>()

        public inline fun <T> empty(): KorneaResult<T> =
            Empty.of<T>()

        public inline fun <T> failedPredicate(): KorneaResult<T> =
            Empty.ofFailedPredicate<T>()

        public inline fun <T> typeCastEmpty(): KorneaResult<T> =
            Empty.ofTypeCast<T>()

        public inline fun <T> badImplementation(impl: KorneaResult<*>): KorneaResult<T> =
            Empty.ofBadImplementation<T>(impl)

        public inline fun <E : Throwable> thrownAsFailure(exception: E, cause: KorneaResult<*>? = null): Failure =
            WithException.of(
                exception,
                cause as? Failure
            )

        public inline fun <T, E : Throwable> thrown(exception: E, cause: KorneaResult<*>? = null): KorneaResult<T> =
            WithException.ofTyped(
                exception,
                cause as? Failure
            )

        public inline fun <T> errorAsIllegalArgument(
            errorCode: Int,
            errorMessage: String,
            cause: KorneaResult<*>? = null,
            generateStacktraceOnCreation: Boolean = WithErrorDetails.DEFAULT_GENERATE_STACKTRACE_ON_CREATION,
            includeResultCodeInError: Boolean = WithErrorDetails.DEFAULT_INCLUDE_RESULT_CODE_IN_ERROR
        ): KorneaResult<T> = WithErrorDetails.asIllegalArgument(
            errorCode,
            errorMessage,
            cause as? Failure,
            generateStacktraceOnCreation,
            includeResultCodeInError
        ).asType()

        public inline fun <T> errorAsIllegalState(
            errorCode: Int,
            errorMessage: String,
            cause: KorneaResult<*>? = null,
            generateStacktraceOnCreation: Boolean = WithErrorDetails.DEFAULT_GENERATE_STACKTRACE_ON_CREATION,
            includeResultCodeInError: Boolean = WithErrorDetails.DEFAULT_INCLUDE_RESULT_CODE_IN_ERROR
        ): KorneaResult<T> = WithErrorDetails.asIllegalState(
            errorCode,
            errorMessage,
            cause as? Failure,
            generateStacktraceOnCreation,
            includeResultCodeInError
        ).asType()

        public fun <T, E : Throwable> error(
            errorCode: Int,
            errorMessage: String,
            exception: E,
            cause: KorneaResult<*>? = null
        ): KorneaResult<T> = WithErrorDetails.of(
            errorCode,
            errorMessage,
            exception,
            cause as Failure
        ).asType()

        public fun <T, E : Throwable> error(
            errorCode: Int,
            errorMessage: String,
            supplier: (WithErrorCode) -> E,
            cause: KorneaResult<*>? = null
        ): KorneaResult<T> = WithErrorDetails.of(
            errorCode,
            errorMessage,
            supplier,
            cause as Failure
        ).asType()

        public inline fun <T> foldingMutableListOf(config: KorneaResultConfig? = null): KorneaResult<MutableList<T>> =
            success(ArrayList(), config)

        public suspend inline fun <T> foldingMutableListOf(): KorneaResult<MutableList<T>> =
            success(ArrayList(), config())

        public suspend inline fun <T, R> fold(
            iterable: Iterable<T>,
            operation: (acc: KorneaResult<MutableList<R>>, T) -> KorneaResult<MutableList<R>>
        ): KorneaResult<List<R>> =
            foldTo(iterable, ArrayList(), operation)

        public suspend inline fun <T, R, L : MutableList<R>> foldTo(
            iterable: Iterable<T>,
            initial: L,
            operation: (acc: KorneaResult<L>, T) -> KorneaResult<L>
        ): KorneaResult<List<R>> =
            iterable.fold(success(initial, config()), operation)

        public inline fun <T, R> fold(
            iterable: Iterable<T>,
            config: KorneaResultConfig? = null,
            operation: (acc: KorneaResult<MutableList<R>>, T) -> KorneaResult<MutableList<R>>
        ): KorneaResult<List<R>> =
            foldTo(iterable, ArrayList(), config, operation)

        public inline fun <T, R, L : MutableList<R>> foldTo(
            iterable: Iterable<T>,
            initial: L,
            config: KorneaResultConfig? = null,
            operation: (acc: KorneaResult<L>, T) -> KorneaResult<L>
        ): KorneaResult<List<R>> =
            iterable.fold(success(initial, config), operation)

        public fun dirtyImplementationString(impl: KorneaResult<*>): String =
            "Bad implementation of KorneaResult by `${impl::class}`; you need to implement either Success<T> or Failure! (Value was $impl)"
    }

    public interface Success<out T> : KorneaResult<T> {
        public companion object {
            public fun <T> of(value: T): Success<T> =
                Base(value)
        }

        public interface FailedCastObserver {
            public fun <R> onFailedCast(): KorneaResult<R>
        }

        public interface FailedPredicateObserver {
            public fun <T> onFailedPredicate(): KorneaResult<T>
            public fun <T> onFilterWasNull(): KorneaResult<T>
            public fun <T> onFilterToFailed(): KorneaResult<T>
            public fun <R> onFilterToInstanceFailed(): KorneaResult<R>
        }

        private class Base<T>(private val value: T) : Success<T> {
            override fun get(): T = value
            override fun <R> mapValue(newValue: R): KorneaResult<R> =
                Base(newValue)

            override fun toString(): String =
                "Success(value=$value)"

            override fun dataHashCode(): Optional<Int> =
                Optional(value.hashCode())

            override fun isAvailable(dataHashCode: Int?): Boolean? =
                if (dataHashCode?.equals(value.hashCode()) != false) true else null

            override fun consume(dataHashCode: Int?) {}

            override fun copyOf(): KorneaResult<T> =
                Base(value)
        }

        override fun get(): T

        @AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
        @ChangedSince(KorneaErrors.VERSION_2_1_0_ALPHA)
        public infix fun <R> mapValue(newValue: R): KorneaResult<R>
    }

    public interface Failure : KorneaResult<Nothing> {
        public companion object {
            @AvailableSince(KorneaErrors.VERSION_3_4_1_INDEV)
            public fun of(): Failure =
                Base

            @AvailableSince(KorneaErrors.VERSION_3_4_1_INDEV)
            public fun <T> of(): KorneaResult<T> = Base.asType()
        }

        private object Base : Failure {
            override fun get(): Nothing = throw IllegalStateException("(Unknown failure)")
            override fun copyOf(): KorneaResult<Nothing> =
                this
        }

        override fun dataHashCode(): Optional<Int> = Optional.empty()
        override fun isAvailable(dataHashCode: Int?): Boolean? = null
        override fun consume(dataHashCode: Int?) {}
    }

    public interface Empty : Failure {
        public interface FailedPredicate : Empty

        @AvailableSince(KorneaErrors.VERSION_3_2_0_INDEV)
        public interface Null : Empty

        @AvailableSince(KorneaErrors.VERSION_3_2_0_INDEV)
        public interface Undefined : Empty
        public interface TypeCastEmpty : Empty
        public interface BadImplementation : Empty

        @AvailableSince(KorneaErrors.VERSION_2_1_0_ALPHA)
        public interface WasConsumed : Empty

        public companion object {
            public fun of(): Empty =
                Base

            public fun <T> of(): KorneaResult<T> = Base.asType()

            public fun ofFailedPredicate(): Empty =
                FailedPredicateBase

            public fun <T> ofFailedPredicate(): KorneaResult<T> = FailedPredicateBase.asType()

            public fun ofTypeCast(): Empty =
                TypeCastEmptyBase

            public fun <T> ofTypeCast(): KorneaResult<T> = TypeCastEmptyBase.asType()

            @AvailableSince(KorneaErrors.VERSION_3_2_0_INDEV)
            public fun ofNull(): Empty =
                NullBase

            @AvailableSince(KorneaErrors.VERSION_3_2_0_INDEV)
            public fun <T> ofNull(): KorneaResult<T> = NullBase.asType()

            @AvailableSince(KorneaErrors.VERSION_3_2_0_INDEV)
            public fun ofUndefined(): Empty =
                UndefinedBase

            @AvailableSince(KorneaErrors.VERSION_3_2_0_INDEV)
            public fun <T> ofUndefined(): KorneaResult<T> = UndefinedBase.asType()

            public fun ofBadImplementation(impl: KorneaResult<*>): Empty =
                BadImplementationBase(impl)

            public fun <T> ofBadImplementation(impl: KorneaResult<*>): KorneaResult<T> =
                BadImplementationBase(impl).asType()

            @AvailableSince(KorneaErrors.VERSION_2_1_0_ALPHA)
            public fun ofClosed(): Empty =
                WasConsumedBase

            @AvailableSince(KorneaErrors.VERSION_2_1_0_ALPHA)
            public fun <T> ofConsumed(): KorneaResult<T> = WasConsumedBase.asType()
        }

        private object Base : Empty {
            override fun get() = throw IllegalStateException("Result is empty")

            override fun toString(): String =
                "Empty()"

            override fun copyOf(): KorneaResult<Nothing> =
                this
        }

        private object FailedPredicateBase :
            FailedPredicate {
            override fun get() = throw IllegalStateException("Failed predicate")

            override fun toString(): String =
                "FailedPredicate()"

            override fun copyOf(): KorneaResult<Nothing> =
                this
        }

        private object NullBase : Null {
            override fun get() = throw IllegalArgumentException("Was null")

            override fun toString(): String =
                "Null()"

            override fun copyOf(): KorneaResult<Nothing> =
                this
        }

        private object UndefinedBase : Undefined {
            override fun get() = throw IllegalArgumentException("Was undefined")

            override fun toString(): String =
                "Undefined()"

            override fun copyOf(): KorneaResult<Nothing> =
                this
        }

        private object TypeCastEmptyBase :
            TypeCastEmpty {
            override fun get() = throw IllegalStateException("Result was miscast")

            override fun toString(): String =
                "TypeCastEmpty()"

            override fun copyOf(): KorneaResult<Nothing> =
                this
        }

        private object WasConsumedBase : WasConsumed {
            override fun get() = throw IllegalStateException("Result was consumed")

            override fun toString(): String =
                "WasConsumed()"

            override fun copyOf(): KorneaResult<Nothing> =
                this
        }

        private class BadImplementationBase(val impl: KorneaResult<*>) : BadImplementation,
            WithException<IllegalStateException> {
            override val exception: IllegalStateException by lazy {
                IllegalStateException(
                    dirtyImplementationString(
                        impl
                    )
                )
            }

            override fun toString(): String =
                "BadImplementation()"

            override fun <R : Throwable> withException(newException: R): WithException<R> =
                WithException.of(
                    newException,
                    this
                )

            override fun copyOf(): KorneaResult<Nothing> =
                BadImplementationBase(impl)
        }
    }

    public interface WithCause : Failure {
        public val cause: Failure?

        public infix fun withCause(newCause: Failure?): WithCause
    }

    public interface WithException<out E : Throwable> :
        Failure {
        public companion object {
            public fun <E : Throwable> of(exception: E, cause: Failure? = null): WithException<E> =
                Base(exception, cause)

            public fun <E : Throwable, T> ofTyped(exception: E, cause: Failure? = null): KorneaResult<T> =
                Base(exception, cause).asType()
        }

        private class Base<E : Throwable>(override val exception: E, override val cause: Failure?) : WithException<E>,
            WithCause {
            override fun toString(): String = "WithException(exception=$exception)"

            override fun withCause(newCause: Failure?): WithCause =
                Base(exception, newCause)

            override fun <R : Throwable> withException(newException: R): WithException<R> =
                Base(newException, cause)

            override fun copyOf(): KorneaResult<Nothing> =
                Base(exception, cause)
        }

        public val exception: E

        override fun get(): Nothing = throw exception

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
                append(errorCode.toString(16))
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

            override fun get(): Nothing = throw exception

            override fun copyOf(): KorneaResult<Nothing> =
                Base(errorCode, errorMessage, cause, _exception, _exceptionSupplier)

            override fun toString(): String =
                "WithErrorDetails(errorCode=$errorCode, errorMessage='$errorMessage', cause=$cause, exception=$_exception, exceptionSupplier=$_exceptionSupplier)"

            init {
                require(_exception != null || _exceptionSupplier != null) { "Invalid Error combination WithErrorDetails(errorCode=$errorCode, errorMessage=$errorMessage, exception=null, exceptionSupplier=null)" }
            }
        }
    }

    @AvailableSince(KorneaErrors.VERSION_3_2_0_INDEV)
    public interface WithPayload<T> : Failure {

    }

    public fun get(): T

    /**
     * Return a hash code for the data contained within.
     * Allows for a single result instance to be reused, while not being consumed from mistimed calls.
     *
     * @return a hashCode calculation for the data within, or [Optional.EMPTY] if no data is available
     */
    public fun dataHashCode(): Optional<Int>

    /**
     * Determine whether the data indicated by [dataHashCode] is available within this result instance.
     *
     *
     * @param dataHashCode the hashCode of the data to query, or null if we are only checking the most current data
     * @return true if available, false if unavailable, null if [dataHashCode] does not point to this set of data
     */
    public fun isAvailable(dataHashCode: Int?): Boolean?

    /**
     * Consume the data indicated by [dataHashCode] and mark this instance as completed.
     * Future calls to this result may throw an error if this instance is not reused.
     *
     * @param dataHashCode the data to consume, or null if we are only consuming the most current data
     */
    public fun consume(dataHashCode: Int?)

    /**
     * Creates a copy of this result, that may be used, consumed, and closed separately
     */
    public fun copyOf(): KorneaResult<T>
}

public inline fun KorneaResult<*>.hierarchy(): List<KorneaResult<*>> =
    ArrayList<KorneaResult<*>>().apply(this::hierarchyIn)

public inline fun KorneaResult<*>.hierarchyIn(list: MutableList<KorneaResult<*>>) {
    var self: KorneaResult<*>? = this
    while (self is KorneaResult.WithCause) {
        list.add(self)
        self = self.cause
    }

    self?.let(list::add)
}

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
public inline fun <T> KorneaResult<T>.mapFailureCause(transform: (KorneaResult.Failure?) -> KorneaResult<*>?): KorneaResult<T> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.WithCause -> this withCause (transform(cause) as? KorneaResult.Failure)
        is KorneaResult.Success<T> -> this
        is KorneaResult.Failure -> this
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }
}

@AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
public inline fun <T> KorneaResult<T>.mapFailureCause(cause: KorneaResult<*>?): KorneaResult<T> =
    when (this) {
        is KorneaResult.WithCause -> this withCause (cause as? KorneaResult.Failure)
        is KorneaResult.Success<T> -> this
        is KorneaResult.Failure -> this
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }

@Suppress("UNCHECKED_CAST")
@AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
public inline fun <T> KorneaResult<T>.mapFailureRootCause(rootCause: KorneaResult<*>?): KorneaResult<T> =
    when (this) {
        is KorneaResult.WithCause -> hierarchy()
            .asReversed()
            .fold(rootCause as? KorneaResult.Failure) { cause, result ->
                (result as? KorneaResult.WithCause)?.withCause(cause) ?: result as? KorneaResult.Failure
            }?.asType() ?: this

        is KorneaResult.Success<T> -> this
        is KorneaResult.Failure -> this
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
public inline fun <T, E : Throwable> KorneaResult<T>.mapFailureException(transform: (Throwable) -> E): KorneaResult<T> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.WithException<*> -> this withException transform(exception)
        is KorneaResult.Success<T> -> this
        is KorneaResult.Failure -> this
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }
}

@AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
public inline fun <T, E : Throwable> KorneaResult<T>.mapFailureException(exception: E): KorneaResult<T> =
    when (this) {
        is KorneaResult.WithException<*> -> this withException exception
        is KorneaResult.Success<T> -> this
        is KorneaResult.Failure -> this
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }

@AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
public inline fun <R, T : KorneaResult<R>> KorneaResult<T>.flatten(): KorneaResult<R> =
    when (this) {
        is KorneaResult.Success<T> -> get()
        is KorneaResult.Failure -> asType()
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
public inline fun <T> KorneaResult.Failure.asType(): KorneaResult<T> = this

@Suppress("UNCHECKED_CAST")
@ChangedSince(
    KorneaErrors.VERSION_3_1_0_INDEV,
    "Casting no longer creates a new instance, and when a cast fails instances have the chance to recover themselves"
)
public inline fun <T, reified R> KorneaResult<T>.cast(): KorneaResult<R> =
    when (this) {
        is KorneaResult.Success<T> -> when {
            get() is R -> this as KorneaResult<R>
            this is KorneaResult.Success.FailedCastObserver -> onFailedCast()
            else -> KorneaResult.typeCastEmpty()
        }

        is KorneaResult.Failure -> asType()
        else -> KorneaResult.badImplementation(this)
    }

@OptIn(ExperimentalContracts::class)
public inline fun <T, R> KorneaResult<T>.map(transform: (T) -> R): KorneaResult<R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.Success<T> -> this mapValue transform(get())
        is KorneaResult.Failure -> asType()
        else -> KorneaResult.badImplementation(this)
    }
}

@OptIn(ExperimentalContracts::class)
public inline fun <T, R> KorneaResult<T>.flatMap(transform: (T) -> KorneaResult<R>): KorneaResult<R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.Success<T> -> transform(get())
        is KorneaResult.Failure -> asType()
        else -> KorneaResult.badImplementation(this)
    }
}

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_3_0_INDEV)
public inline fun <T> KorneaResult<T>.flatMapOrSelf(transform: (T) -> KorneaResult<T>?): KorneaResult<T> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.Success<T> -> when (val result = transform(get())) {
            null -> this
            else -> result
        }

        is KorneaResult.Failure -> asType()
        else -> KorneaResult.badImplementation(this)
    }
}

@OptIn(ExperimentalContracts::class)
@ChangedSince(
    KorneaErrors.VERSION_3_1_0_INDEV,
    "When a filter fails, the result has a chance to control the output"
)
public inline fun <T> KorneaResult<T>.filter(predicate: (T) -> Boolean): KorneaResult<T> {
    contract {
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.Success<T> -> when {
            predicate(get()) -> this
            this is KorneaResult.Success.FailedPredicateObserver -> onFailedPredicate()
            else -> KorneaResult.failedPredicate()
        }

        is KorneaResult.Failure -> asType()
        else -> KorneaResult.badImplementation(this)
    }
}

@Suppress("UNCHECKED_CAST")
@AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
public inline fun <T> KorneaResult<T?>.filterNotNull(): KorneaResult<T> =
    when (this) {
        is KorneaResult.Success<T?> -> when {
            get() != null -> this as KorneaResult<T>
            this is KorneaResult.Success.FailedPredicateObserver -> onFilterWasNull()
            else -> KorneaResult.failedPredicate()
        }
        is KorneaResult.Failure -> asType()
        else -> KorneaResult.badImplementation(this)
    }

@OptIn(ExperimentalContracts::class)
@Suppress("UNCHECKED_CAST")
@AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
public inline fun <R> KorneaResult<*>.filterNotNull(onEmpty: () -> KorneaResult<R>): KorneaResult<R> {
    contract {
        callsInPlace(onEmpty, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.Success -> if (get() != null) this as KorneaResult<R> else onEmpty()
        is KorneaResult.Failure -> asType()
        else -> KorneaResult.badImplementation(this)
    }
}

@Suppress("UNCHECKED_CAST")
@AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
public inline fun <R> KorneaResult<*>.filterNotNull(default: KorneaResult<R>): KorneaResult<R> =
    when (this) {
        is KorneaResult.Success -> if (get() != null) this as KorneaResult<R> else default
        is KorneaResult.Failure -> asType()
        else -> KorneaResult.badImplementation(this)
    }

@OptIn(ExperimentalContracts::class)
@ChangedSince(
    KorneaErrors.VERSION_3_1_0_INDEV,
    "When a filter fails, the result has a chance to control the output"
)
public inline fun <T> KorneaResult<T>.filterTo(transform: (T) -> KorneaResult<T>?): KorneaResult<T> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.Success<T> -> when (val result = transform(get())) {
            null -> {
                if (this is KorneaResult.Success.FailedPredicateObserver) onFilterToFailed()
                else KorneaResult.failedPredicate()
            }
            else -> result
        }

        is KorneaResult.Failure -> asType()
        else -> KorneaResult.badImplementation(this)
    }
}

@Suppress("UNCHECKED_CAST")
@ChangedSince(
    KorneaErrors.VERSION_3_1_0_INDEV,
    "When a filter fails, the result has a chance to control the output"
)
public inline fun <reified R> KorneaResult<*>.filterToInstance(): KorneaResult<R> =
    when (this) {
        is KorneaResult.Success -> when {
            get() is R -> this as KorneaResult<R>
            this is KorneaResult.Success.FailedPredicateObserver -> onFilterToInstanceFailed()
            else -> KorneaResult.typeCastEmpty()
        }

        is KorneaResult.Failure -> asType()
        else -> KorneaResult.badImplementation(this)
    }

@Suppress("UNCHECKED_CAST")
@AvailableSince(KorneaErrors.VERSION_3_4_0_INDEV)
public inline fun <R : Any> KorneaResult<*>.filterToInstance(klass: KClass<R>): KorneaResult<R> =
    when (this) {
        is KorneaResult.Success -> when {
            klass.isInstance(get()) -> this as KorneaResult<R>
            this is KorneaResult.Success.FailedPredicateObserver -> onFilterToInstanceFailed()
            else -> KorneaResult.typeCastEmpty()
        }

        is KorneaResult.Failure -> asType()
        else -> KorneaResult.badImplementation(this)
    }

@OptIn(ExperimentalContracts::class)
@Suppress("UNCHECKED_CAST")
public inline fun <reified R> KorneaResult<*>.filterToInstance(onEmpty: () -> KorneaResult<R>): KorneaResult<R> {
    contract {
        callsInPlace(onEmpty, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.Success -> if (get() is R) this as KorneaResult<R> else onEmpty()
        is KorneaResult.Failure -> asType()
        else -> KorneaResult.badImplementation(this)
    }
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

    return when (this) {
        is KorneaResult.Success -> if (klass.isInstance(get())) this as KorneaResult<R> else onEmpty()
        is KorneaResult.Failure -> asType()
        else -> KorneaResult.badImplementation(this)
    }
}

@Suppress("UNCHECKED_CAST")
public inline fun <reified R> KorneaResult<*>.filterToInstance(default: KorneaResult<R>): KorneaResult<R> =
    when (this) {
        is KorneaResult.Success -> if (get() is R) this as KorneaResult<R> else default
        is KorneaResult.Failure -> asType()
        else -> KorneaResult.badImplementation(this)
    }

@Suppress("UNCHECKED_CAST")
@AvailableSince(KorneaErrors.VERSION_3_4_0_INDEV)
public inline fun <R : Any> KorneaResult<*>.filterToInstance(
    default: KorneaResult<R>,
    klass: KClass<R>
): KorneaResult<R> =
    when (this) {
        is KorneaResult.Success -> if (klass.isInstance(get())) this as KorneaResult<R> else default
        is KorneaResult.Failure -> asType()
        else -> KorneaResult.badImplementation(this)
    }

@OptIn(ExperimentalContracts::class)
@Suppress("UNCHECKED_CAST")
public inline fun <T, reified R : T> KorneaResult<T>.filterToInstance(transform: (T) -> KorneaResult<R>): KorneaResult<R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.Success -> when (val result = get()) {
            is R -> this as KorneaResult<R>
            else -> transform(result)
        }
        is KorneaResult.Failure -> asType()
        else -> KorneaResult.badImplementation(this)
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

    return when (this) {
        is KorneaResult.Success -> {
            val result = get()
            if (klass.isInstance(result)) this as KorneaResult<R>
            else transform(result)
        }
        is KorneaResult.Failure -> asType()
        else -> KorneaResult.badImplementation(this)
    }
}

public inline fun <T> KorneaResult<T>.getOrNull(): T? = if (this is KorneaResult.Success<T>) get() else null
public inline fun <T> KorneaResult<T>.getOrElse(default: T): T = if (this is KorneaResult.Success<T>) get() else default

public inline fun <T> KorneaResult<T>.consumeAndGet(dataHashCode: Int?): T = consume(dataHashCode) { get() }

public inline fun <T> KorneaResult<T>.consumeAndGetOrNull(dataHashCode: Int?): T? =
    consume(dataHashCode) { if (this is KorneaResult.Success<T>) get() else null }

public inline fun <T> KorneaResult<T>.consumeAndGetOrElse(dataHashCode: Int?, default: T): T =
    consume(dataHashCode) { if (this is KorneaResult.Success<T>) get() else default }

public inline fun <T> KorneaResult<T>.consumeAndGetOrNull(): T? =
    consume { if (this is KorneaResult.Success<T>) get() else null }

public inline fun <T> KorneaResult<T>.consumeAndGetOrElse(default: T): T =
    consume { if (this is KorneaResult.Success<T>) get() else default }

@AvailableSince(KorneaErrors.VERSION_2_1_0_ALPHA)
public inline fun <T> KorneaResult<T>.getAsOptional(): Optional<T> =
    if (this is KorneaResult.Success<T>) Optional.of(get()) else Optional.empty()

@AvailableSince(KorneaErrors.VERSION_3_4_0_INDEV)
public inline fun <T> KorneaResult<T>.getOrEmptyDefault(default: T): T =
    if (this is KorneaResult.Empty) default else get()

@OptIn(ExperimentalContracts::class)
public inline fun <T> KorneaResult<T>.getOrElseRun(block: () -> T): T {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return if (this is KorneaResult.Success<T>) get() else block()
}

@OptIn(ExperimentalContracts::class)
public inline fun <T> KorneaResult<T>.getOrElseTransform(block: (KorneaResult.Failure) -> T): T {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.Success<T> -> get()
        is KorneaResult.Failure -> block(this)
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }
}

public inline fun <T> KorneaResult<T>.orElse(default: KorneaResult<T>): KorneaResult<T> =
    if (this is KorneaResult.Success<T>) this else default

@OptIn(ExperimentalContracts::class)
public inline fun <T> KorneaResult<T>.switchIfFailure(block: (KorneaResult.Failure) -> KorneaResult<T>): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.Success<T> -> this
        is KorneaResult.Failure -> block(this)
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }
}

@OptIn(ExperimentalContracts::class)
@ChangedSince(KorneaErrors.VERSION_3_2_0_INDEV, "[block] now takes the empty instance")
public inline fun <T> KorneaResult<T>.switchIfEmpty(block: (empty: KorneaResult.Empty) -> KorneaResult<T>): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.Empty -> block(this)
        is KorneaResult.Success<T> -> this
        is KorneaResult.Failure -> this
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }
}

@OptIn(ExperimentalContracts::class)
@ChangedSince(
    KorneaErrors.VERSION_3_1_0_INDEV,
    "WithErrorCode has been broken up into three separate interfaces; you may want switchIfHasErrorDetails"
)
public inline fun <T> KorneaResult<T>.switchIfHasErrorCode(block: (KorneaResult.WithErrorCode) -> KorneaResult<T>): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.WithErrorCode -> block(this)
        is KorneaResult.Success<T> -> this
        is KorneaResult.Failure -> this
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }
}

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
public inline fun <T> KorneaResult<T>.switchIfHasErrorMessage(block: (KorneaResult.WithErrorMessage) -> KorneaResult<T>): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.WithErrorMessage -> block(this)
        is KorneaResult.Success<T> -> this
        is KorneaResult.Failure -> this
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }
}

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
public inline fun <T> KorneaResult<T>.switchIfHasErrorDetails(block: (KorneaResult.WithErrorDetails) -> KorneaResult<T>): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.WithErrorDetails -> block(this)
        is KorneaResult.Success<T> -> this
        is KorneaResult.Failure -> this
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }
}

@OptIn(ExperimentalContracts::class)
public inline fun <T> KorneaResult<T>.switchIfHasException(block: (KorneaResult.WithException<*>) -> KorneaResult<T>): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.WithException<*> -> block(this)
        is KorneaResult.Success<T> -> this
        is KorneaResult.Failure -> this
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }
}

@OptIn(ExperimentalContracts::class)
@Suppress("UNCHECKED_CAST")
public inline fun <T, reified E : Throwable> KorneaResult<T>.switchIfHasTypedException(block: (KorneaResult.WithException<E>) -> KorneaResult<T>): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.WithException<*> -> if (exception is E) block(this as KorneaResult.WithException<E>) else this
        is KorneaResult.Success<T> -> this
        is KorneaResult.Failure -> this
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }
}

@OptIn(ExperimentalContracts::class)
@Suppress("UNCHECKED_CAST")
public inline fun <T, E : Throwable> KorneaResult<T>.switchIfHasTypedException(
    klass: KClass<E>,
    block: (KorneaResult.WithException<E>) -> KorneaResult<T>
): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.WithException<*> -> if (klass.isInstance(exception)) block(this as KorneaResult.WithException<E>) else this
        is KorneaResult.Success<T> -> this
        is KorneaResult.Failure -> this
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }
}

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_2_0_INDEV)
public inline fun <T> KorneaResult<T>.switchIfHasCause(block: (KorneaResult.WithCause) -> KorneaResult<T>): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.WithCause -> block(this)
        is KorneaResult.Success<T> -> this
        is KorneaResult.Failure -> this
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }
}

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

    return when (this) {
        is KorneaResult.Failure -> {
            block(this)
            this
        }

        is KorneaResult.Success<T> -> this
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(this)
        )
    }
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

    return when (this) {
        is KorneaResult.WithErrorCode -> {
            block(this)
            this
        }

        is KorneaResult.Success<T> -> this
        is KorneaResult.Failure -> this
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }
}

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
public inline fun <T> KorneaResult<T>.doWithErrorMessage(block: (KorneaResult.WithErrorMessage) -> Unit): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.WithErrorMessage -> {
            block(this)
            this
        }

        is KorneaResult.Success<T> -> this
        is KorneaResult.Failure -> this
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }
}

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
public inline fun <T> KorneaResult<T>.doWithErrorDetails(block: (KorneaResult.WithErrorDetails) -> Unit): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.WithErrorDetails -> {
            block(this)
            this
        }

        is KorneaResult.Success<T> -> this
        is KorneaResult.Failure -> this
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(this)
        )
    }
}

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_2_0_INDEV)
public inline fun <T> KorneaResult<T>.doWithCause(block: (KorneaResult.WithCause) -> KorneaResult<T>): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.WithCause -> {
            block(this)
            this
        }

        is KorneaResult.Success<T> -> this
        is KorneaResult.Failure -> this
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(this)
        )
    }
}

@OptIn(ExperimentalContracts::class)
@ChangedSince(KorneaErrors.VERSION_3_2_0_INDEV, "[block] now accepts the empty instance")
public inline fun <T> KorneaResult<T>.doOnEmpty(block: (KorneaResult.Empty) -> Unit): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.Empty -> {
            block(this)
            this
        }

        is KorneaResult.Success<T> -> this
        is KorneaResult.Failure -> this
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }
}

@OptIn(ExperimentalContracts::class)
public inline fun <T> KorneaResult<T>.doOnThrown(block: (KorneaResult.WithException<*>) -> Unit): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.WithException<*> -> {
            block(this)
            this
        }

        is KorneaResult.Success<T> -> this
        is KorneaResult.Failure -> this
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }
}

@OptIn(ExperimentalContracts::class)
@Suppress("UNCHECKED_CAST")
public inline fun <T, reified E : Throwable> KorneaResult<T>.doOnTypedThrown(block: (KorneaResult.WithException<E>) -> Unit): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.WithException<*> -> {
            if (exception is E) block(this as KorneaResult.WithException<E>)
            this
        }

        is KorneaResult.Success<T> -> this
        is KorneaResult.Failure -> this
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }
}

@OptIn(ExperimentalContracts::class)
public inline fun <T> KorneaResult<T>.doOnSuccess(block: (T) -> Unit): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.Success<T> -> {
            block(get())

            this
        }
        is KorneaResult.Failure -> this
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }
}

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_1_1_INDEV)
public suspend inline fun <T> KorneaResult<T>.doOnSuccessAsync(@Suppress("REDUNDANT_INLINE_SUSPEND_FUNCTION_TYPE") block: suspend (T) -> Unit): KorneaResult<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.Success<T> -> {
            block(get())

            this
        }
        is KorneaResult.Failure -> this
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }
}

/**
 * Returns the value stored on a success, or runs [onFailure] when in a fail state.
 *
 * Fail states must not continue execution after they are called (ie: must return/shutdown/throw)
 */
@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_0_2_INDEV)
public inline fun <T> KorneaResult<T>.getOrBreak(onFailure: (KorneaResult.Failure) -> Nothing): T {
    contract {
        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.Success<T> -> get()
        is KorneaResult.Failure -> onFailure(this)
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }
}


/**
 * Returns the value stored on a success, or runs [onFailure] when in a fail state.
 *
 * Fail states must not continue execution after they are called (ie: must return/shutdown/throw)
 */
@OptIn(ExperimentalContracts::class)
public inline fun <T> KorneaResult<T>.consumeAndGetOrBreak(onFailure: (KorneaResult.Failure) -> Nothing): T {
    contract {
        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
    }

    return consume {
        when (this) {
            is KorneaResult.Success<T> -> get()
            is KorneaResult.Failure -> onFailure(this)
            else -> throw IllegalStateException(
                KorneaResult.dirtyImplementationString(
                    this
                )
            )
        }
    }
}


/**
 * Returns the value stored on a success, or runs [onFailure] when in a fail state.
 *
 * Fail states must not continue execution after they are called (ie: must return/shutdown/throw)
 */
@OptIn(ExperimentalContracts::class)
public inline fun <T> KorneaResult<T>.consumeOnSuccessGetOrBreak(dataHashCode: Int? = null, onFailure: (KorneaResult.Failure) -> Nothing): T {
    contract {
        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.Success<T> -> consumeAndGet(dataHashCode)
        is KorneaResult.Failure -> onFailure(this)
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }
}

@AvailableSince(KorneaErrors.VERSION_3_3_0_INDEV)
public inline fun <T, E> List<T>.foldResults(block: (element: T) -> KorneaResult<E>): KorneaResult<List<E>> =
    KorneaResult.fold(this, null) { acc, element ->
        acc.flatMapOrSelf { list -> block(element).map { list.add(it); list } }
    }

@OptIn(ExperimentalContracts::class)
public inline fun <T : Any> requireSuccessful(value: KorneaResult<T>): T {
    contract {
        returns() implies (value is KorneaResult.Success<T>)
    }

    return value.get()
}

@OptIn(ExperimentalContracts::class)
public inline fun <T : Any> requireSuccessful(value: KorneaResult<T>, lazyMessage: () -> Any): T {
    contract {
        returns() implies (value is KorneaResult.Success<T>)
    }

    if (value !is KorneaResult.Success<T>) {
        val message = lazyMessage()
        throw IllegalArgumentException(message.toString())
    } else {
        return value.get()
    }
}

@OptIn(ExperimentalContracts::class)
public inline fun KorneaResult<*>.isSuccessful(): Boolean {
    contract {
        returns(true) implies (this@isSuccessful is KorneaResult.Success)
    }

    return this is KorneaResult.Success
}

@OptIn(ExperimentalContracts::class)
public inline fun KorneaResult<*>.isFailure(): Boolean {
    contract {
        returns(true) implies (this@isFailure is KorneaResult.Failure)
    }

    return this is KorneaResult.Failure
}

@OptIn(ExperimentalContracts::class)
@ExperimentalUnsignedTypes
@AvailableSince(KorneaErrors.VERSION_2_1_0_ALPHA)
public suspend inline fun <T : DataCloseable, reified R> KorneaResult<T>.useAndMap(block: (T) -> R): KorneaResult<R> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.Success<T> -> mapValue(get().use(block))
        is KorneaResult.Failure -> asType()
        else -> throw IllegalStateException(KorneaResult.dirtyImplementationString(this))
    }
}

@OptIn(ExperimentalContracts::class)
@ExperimentalUnsignedTypes
@AvailableSince(KorneaErrors.VERSION_2_1_0_ALPHA)
public suspend inline fun <T : DataCloseable, reified R> KorneaResult<T>.useAndFlatMap(block: (T) -> KorneaResult<R>): KorneaResult<R> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is KorneaResult.Success<T> -> get().use(block)
        is KorneaResult.Failure -> asType()
        else -> throw IllegalStateException(KorneaResult.dirtyImplementationString(this))
    }
}


@AvailableSince(KorneaErrors.VERSION_2_1_0_ALPHA)
public inline fun <T> Optional<KorneaResult<T>>.flatten(): KorneaResult<T> =
    getOrElse(KorneaResult.empty())

@AvailableSince(KorneaErrors.VERSION_2_1_0_ALPHA)
public inline fun <T> KorneaResult<Optional<T>>.filter(): KorneaResult<T> =
    when (this) {
        is KorneaResult.Success ->
            if (get().isPresent) this mapValue get().value
            else KorneaResult.empty()
        is KorneaResult.Failure -> asType()
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(this)
        )
    }

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_2_1_0_ALPHA)
public inline fun <T : KorneaResult<*>, R> T.consume(block: (T) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    var exception: Throwable? = null
    val dataHashCode = dataHashCode()
    try {
        return block(this)
    } catch (e: Throwable) {
        exception = e
        throw e
    } finally {
        dataHashCode.doOnPresent { consumeFinally(it, exception) }
    }
}

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_2_1_0_ALPHA)
public inline fun <T : KorneaResult<*>, R> T.consume(dataHashCode: Int?, block: (T) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    var exception: Throwable? = null
    try {
        return block(this)
    } catch (e: Throwable) {
        exception = e
        throw e
    } finally {
        consumeFinally(dataHashCode, exception)
    }
}

@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_2_1_0_ALPHA)
public inline fun <T, R> KorneaResult<T>.consumeInner(block: (T) -> R): Optional<R> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    var exception: Throwable? = null
    val dataHashCode = dataHashCode()
    try {
        return getAsOptional().map(block)
    } catch (e: Throwable) {
        exception = e
        throw e
    } finally {
        dataHashCode.doOnPresent { consumeFinally(it, exception) }
    }
}

@PublishedApi
@AvailableSince(KorneaBase.VERSION_1_0_0_ALPHA)
internal fun KorneaResult<*>?.consumeFinally(dataHashCode: Int?, cause: Throwable?): Unit = when {
    this == null -> {
    }
    cause == null -> consume(dataHashCode)
    else ->
        try {
            consume(dataHashCode)
        } catch (closeException: Throwable) {
            cause.addSuppressed(closeException)
        }
}