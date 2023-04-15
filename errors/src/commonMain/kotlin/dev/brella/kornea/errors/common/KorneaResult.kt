@file:Suppress("unused")

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

    @AvailableSince(KorneaErrors.VERSION_3_2_0_INDEV)
    @ChangedSince(KorneaErrors.VERSION_4_0_0_ALPHA)
    public interface WithPayload<out T> : Failure {
        public val payload: T

        override fun withCause(newCause: Failure?): WithPayload<T>
    }

    @ChangedSince(KorneaErrors.VERSION_4_0_0_ALPHA)
    public interface Failure {
        public companion object : Failure {
            override val cause: Failure? = null

            override fun asException(): Throwable =
                IllegalStateException("Result failed")

            override fun withCause(newCause: Failure?): Failure =
                Base(newCause)

            override fun <R> withPayload(newPayload: R): WithPayload<R> =
                BasePayload(newPayload)

            override fun <R> with(newPayload: R, newCause: Failure?): WithPayload<R> =
                BasePayload(newPayload, newCause)

            override fun toString(): String =
                "Failure()"

            @AvailableSince(KorneaErrors.VERSION_3_4_1_INDEV)
            public fun of(): Failure =
                this

            @AvailableSince(KorneaErrors.VERSION_3_4_1_INDEV)
            public fun <T> of(): KorneaResult<T> =
                KorneaResult(this)

            @AvailableSince(KorneaErrors.VERSION_4_0_0_ALPHA)
            public fun <T> of(payload: T, cause: Failure? = null): WithPayload<T> =
                BasePayload(payload, cause)

            @AvailableSince(KorneaErrors.VERSION_4_0_0_ALPHA)
            public fun of(cause: Failure): Failure =
                Base(cause)

            @AvailableSince(KorneaErrors.VERSION_4_0_0_ALPHA)
            public fun <T> ofWrapped(
                payload: Any?, //type erasure
                cause: Failure? = null
            ): KorneaResult<T> = KorneaResult(BasePayload(payload, cause))

            @AvailableSince(KorneaErrors.VERSION_4_0_0_ALPHA)
            public fun <T> ofWrapped(cause: Failure): KorneaResult<T> =
                KorneaResult(Base(cause))
        }

        private data class Base(override val cause: Failure?) : Failure {
            override fun asException(): Throwable =
                IllegalArgumentException("Failure()", cause?.asException())

            override fun <R> withPayload(newPayload: R): WithPayload<R> =
                BasePayload(newPayload, cause)

            override fun withCause(newCause: Failure?): Failure =
                Base(newCause)

            override fun <R> with(newPayload: R, newCause: Failure?): WithPayload<R> =
                BasePayload(newPayload, newCause)
        }

        private data class BasePayload<T>(override val payload: T, override val cause: Failure? = null) : Failure,
            WithPayload<T> {
            override fun asException(): Throwable =
                IllegalArgumentException("Result has payload: $payload")

            override fun <R> withPayload(newPayload: R): WithPayload<R> =
                BasePayload(newPayload, cause)

            override fun withCause(newCause: Failure?): WithPayload<T> =
                BasePayload(payload, cause)

            override fun <R> with(newPayload: R, newCause: Failure?): WithPayload<R> =
                BasePayload(newPayload, newCause)
        }

        public val cause: Failure?

        public fun asException(): Throwable
        public infix fun <R> withPayload(newPayload: R): WithPayload<R>
        public infix fun withCause(newCause: Failure?): Failure
        public fun <R> with(newPayload: R, newCause: Failure?): WithPayload<R>
    }

    public interface Empty : Failure {
        public interface FailedPredicate : Empty {
            public companion object : FailedPredicate {
                override val cause: Failure? = null

                override fun withCause(newCause: Failure?): Failure =
                    Base(null, newCause)

                override fun <R> withPayload(newPayload: R): WithPayload<R> =
                    BasePayload(null, newPayload, cause)

                override fun <R> with(newPayload: R, newCause: Failure?): WithPayload<R> =
                    BasePayload(null, newPayload, newCause)

                override fun toString(): String =
                    "Failed Predicate"


                public fun of(): FailedPredicate =
                    this

                public fun of(message: String? = null, cause: Failure? = null): FailedPredicate =
                    Base(message, cause)

                public fun <T> of(payload: T, message: String? = null, cause: Failure? = null): WithPayload<T> =
                    BasePayload(message, payload, cause)

                public fun <T> of(): KorneaResult<T> =
                    KorneaResult(this)

                public fun <T> of(message: String? = null, cause: Failure? = null): KorneaResult<T> =
                    KorneaResult(Base(message, cause))

                public fun <T> ofWrapped(
                    payload: Any?,
                    message: String? = null,
                    cause: Failure? = null
                ): KorneaResult<T> =
                    KorneaResult(BasePayload(message, payload, cause))
            }

            private class Base(val message: String?, override val cause: Failure?) : FailedPredicate {
                override fun withCause(newCause: Failure?): Failure =
                    Base(message, newCause)

                override fun <R> withPayload(newPayload: R): WithPayload<R> =
                    BasePayload(message, newPayload, cause)

                override fun <R> with(newPayload: R, newCause: Failure?): WithPayload<R> =
                    BasePayload(message, newPayload, newCause)

                override fun toString(): String =
                    if (message != null) "Failed Predicate - $message"
                    else "Failed Predicate"
            }

            private class BasePayload<T>(val message: String?, override val payload: T, override val cause: Failure?) :
                FailedPredicate, WithPayload<T> {
                override fun withCause(newCause: Failure?): WithPayload<T> =
                    BasePayload(message, payload, newCause)

                override fun <R> withPayload(newPayload: R): WithPayload<R> =
                    BasePayload(message, newPayload, cause)

                override fun <R> with(newPayload: R, newCause: Failure?): WithPayload<R> =
                    BasePayload(message, newPayload, newCause)

                override fun toString(): String =
                    if (message != null) "FailedPredicate - $message"
                    else "FailedPredicate"
            }

            override fun asException(): Throwable =
                IllegalStateException(toString())
        }

        @AvailableSince(KorneaErrors.VERSION_3_2_0_INDEV)
        public interface Null : Empty {
            public companion object : Null {
                override val cause: Failure? = null

                override fun withCause(newCause: Failure?): Failure =
                    Base(null, newCause)

                override fun <R> withPayload(newPayload: R): WithPayload<R> =
                    BasePayload(null, newPayload, cause)

                override fun <R> with(newPayload: R, newCause: Failure?): WithPayload<R> =
                    BasePayload(null, newPayload, newCause)

                override fun toString(): String =
                    "Was Null"


                public fun of(): Null =
                    this

                public fun of(message: String? = null, cause: Failure? = null): Null =
                    Base(message, cause)

                public fun <T> of(payload: T, message: String? = null, cause: Failure? = null): WithPayload<T> =
                    BasePayload(message, payload, cause)

                public fun <T> of(): KorneaResult<T> =
                    KorneaResult(this)

                public fun <T> of(message: String? = null, cause: Failure? = null): KorneaResult<T> =
                    KorneaResult(Base(message, cause))

                public fun <T> ofWrapped(
                    payload: Any?,
                    message: String? = null,
                    cause: Failure? = null
                ): KorneaResult<T> =
                    KorneaResult(BasePayload(message, payload, cause))
            }

            private class Base(val message: String?, override val cause: Failure?) : Null {
                override fun withCause(newCause: Failure?): Failure =
                    Base(message, newCause)

                override fun <R> withPayload(newPayload: R): WithPayload<R> =
                    BasePayload(message, newPayload, cause)

                override fun <R> with(newPayload: R, newCause: Failure?): WithPayload<R> =
                    BasePayload(message, newPayload, newCause)

                override fun toString(): String =
                    if (message != null) "Was Null - $message"
                    else "Was Null"
            }

            private class BasePayload<T>(val message: String?, override val payload: T, override val cause: Failure?) :
                Null, WithPayload<T> {
                override fun withCause(newCause: Failure?): WithPayload<T> =
                    BasePayload(message, payload, newCause)

                override fun <R> withPayload(newPayload: R): WithPayload<R> =
                    BasePayload(message, newPayload, cause)

                override fun <R> with(newPayload: R, newCause: Failure?): WithPayload<R> =
                    BasePayload(message, newPayload, newCause)

                override fun toString(): String =
                    if (message != null) "Was Null - $message"
                    else "Was Null"
            }

            override fun asException(): Throwable =
                IllegalArgumentException(toString())
        }

        @AvailableSince(KorneaErrors.VERSION_3_2_0_INDEV)
        public interface Undefined : Empty {
            public companion object : Undefined {
                override val cause: Failure? = null

                override fun withCause(newCause: Failure?): Failure =
                    Base(null, newCause)

                override fun <R> withPayload(newPayload: R): WithPayload<R> =
                    BasePayload(null, newPayload, cause)

                override fun <R> with(newPayload: R, newCause: Failure?): WithPayload<R> =
                    BasePayload(null, newPayload, newCause)

                override fun toString(): String =
                    "Was Undefined"


                public fun of(): Undefined =
                    this

                public fun of(message: String? = null, cause: Failure? = null): Undefined =
                    Base(message, cause)

                public fun <T> of(payload: T, message: String? = null, cause: Failure? = null): WithPayload<T> =
                    BasePayload(message, payload, cause)

                public fun <T> of(): KorneaResult<T> =
                    KorneaResult(this)

                public fun <T> of(message: String? = null, cause: Failure? = null): KorneaResult<T> =
                    KorneaResult(Base(message, cause))

                public fun <T> ofWrapped(
                    payload: Any?,
                    message: String? = null,
                    cause: Failure? = null
                ): KorneaResult<T> =
                    KorneaResult(BasePayload(message, payload, cause))
            }

            private class Base(val message: String?, override val cause: Failure?) : Undefined {
                override fun withCause(newCause: Failure?): Failure =
                    Base(message, newCause)

                override fun <R> withPayload(newPayload: R): WithPayload<R> =
                    BasePayload(message, newPayload, cause)

                override fun <R> with(newPayload: R, newCause: Failure?): WithPayload<R> =
                    BasePayload(message, newPayload, newCause)

                override fun toString(): String =
                    if (message != null) "Was Undefined - $message"
                    else "Was Undefined"
            }

            private class BasePayload<T>(val message: String?, override val payload: T, override val cause: Failure?) :
                Undefined, WithPayload<T> {
                override fun withCause(newCause: Failure?): WithPayload<T> =
                    BasePayload(message, payload, newCause)

                override fun <R> withPayload(newPayload: R): WithPayload<R> =
                    BasePayload(message, newPayload, cause)

                override fun <R> with(newPayload: R, newCause: Failure?): WithPayload<R> =
                    BasePayload(message, newPayload, newCause)

                override fun toString(): String =
                    if (message != null) "Was Undefined - $message"
                    else "Was Undefined"
            }

            override fun asException(): Throwable =
                IllegalArgumentException(toString())
        }

        public interface TypeCastEmpty : Empty {
            public companion object : TypeCastEmpty {
                override val cause: Failure? = null

                override fun withCause(newCause: Failure?): Failure =
                    Base(null, newCause)

                override fun <R> withPayload(newPayload: R): WithPayload<R> =
                    BasePayload(null, newPayload, cause)

                override fun <R> with(newPayload: R, newCause: Failure?): WithPayload<R> =
                    BasePayload(null, newPayload, newCause)

                override fun toString(): String =
                    "Type Cast Failed"


                public fun of(): TypeCastEmpty =
                    this

                public fun of(message: String? = null, cause: Failure? = null): TypeCastEmpty =
                    Base(message, cause)

                public fun <T> of(payload: T, message: String? = null, cause: Failure? = null): WithPayload<T> =
                    BasePayload(message, payload, cause)

                public fun <T> of(): KorneaResult<T> =
                    KorneaResult(this)

                public fun <T> of(message: String? = null, cause: Failure? = null): KorneaResult<T> =
                    KorneaResult(Base(message, cause))

                public fun <T> ofWrapped(
                    payload: Any?,
                    message: String? = null,
                    cause: Failure? = null
                ): KorneaResult<T> =
                    KorneaResult(BasePayload(message, payload, cause))
            }

            private class Base(val message: String?, override val cause: Failure?) : TypeCastEmpty {
                override fun withCause(newCause: Failure?): Failure =
                    Base(message, newCause)

                override fun <R> withPayload(newPayload: R): WithPayload<R> =
                    BasePayload(message, newPayload, cause)

                override fun <R> with(newPayload: R, newCause: Failure?): WithPayload<R> =
                    BasePayload(message, newPayload, newCause)

                override fun toString(): String =
                    if (message != null) "Type Cast Failed - $message"
                    else "Type Cast Failed"
            }

            private class BasePayload<T>(val message: String?, override val payload: T, override val cause: Failure?) :
                TypeCastEmpty, WithPayload<T> {
                override fun withCause(newCause: Failure?): WithPayload<T> =
                    BasePayload(message, payload, newCause)

                override fun <R> withPayload(newPayload: R): WithPayload<R> =
                    BasePayload(message, newPayload, cause)

                override fun <R> with(newPayload: R, newCause: Failure?): WithPayload<R> =
                    BasePayload(message, newPayload, newCause)

                override fun toString(): String =
                    if (message != null) "Type Cast Failed - $message"
                    else "Type Cast Failed"
            }

            override fun asException(): Throwable =
                IllegalStateException(toString())
        }

        public companion object : Empty {
            override val cause: Failure? = null

            override fun withCause(newCause: Failure?): Failure =
                Base(null, newCause)

            override fun <R> withPayload(newPayload: R): WithPayload<R> =
                BasePayload(null, newPayload, cause)

            override fun <R> with(newPayload: R, newCause: Failure?): WithPayload<R> =
                BasePayload(null, newPayload, newCause)

            override fun toString(): String =
                "Empty"

            public fun of(): Empty =
                this

            public fun of(message: String? = null, cause: Failure? = null): Empty =
                Base(message, cause)

            public fun <T> of(payload: T, message: String? = null, cause: Failure? = null): WithPayload<T> =
                BasePayload(message, payload, cause)

            public fun <T> of(): KorneaResult<T> =
                KorneaResult(this)

            public fun <T> of(message: String? = null, cause: Failure? = null): KorneaResult<T> =
                KorneaResult(Base(message, cause))

            public fun <T> ofWrapped(payload: Any?, message: String? = null, cause: Failure? = null): KorneaResult<T> =
                KorneaResult(BasePayload(message, payload, cause))

            public fun ofFailedPredicate(): FailedPredicate =
                FailedPredicate

            public fun ofFailedPredicate(message: String? = null, cause: Failure? = null): FailedPredicate =
                FailedPredicate.of(message, cause)

            public fun <T> ofFailedPredicate(
                payload: T,
                message: String? = null,
                cause: Failure? = null
            ): WithPayload<T> =
                FailedPredicate.of(payload, message, cause)

            public fun <T> ofFailedPredicate(): KorneaResult<T> =
                KorneaResult(FailedPredicate)

            public fun <T> ofFailedPredicate(message: String? = null, cause: Failure? = null): KorneaResult<T> =
                FailedPredicate.of<T>(message, cause)

            public fun <T> ofFailedPredicateWrapped(
                payload: Any?,
                message: String? = null,
                cause: Failure? = null
            ): KorneaResult<T> =
                FailedPredicate.ofWrapped(payload, message, cause)

            public fun ofTypeCast(): TypeCastEmpty =
                TypeCastEmpty

            public fun ofTypeCast(message: String? = null, cause: Failure? = null): TypeCastEmpty =
                TypeCastEmpty.of(message, cause)

            public fun <T> ofTypeCast(payload: T, message: String? = null, cause: Failure? = null): WithPayload<T> =
                TypeCastEmpty.of(payload, message, cause)

            public fun <T> ofTypeCast(): KorneaResult<T> =
                KorneaResult(TypeCastEmpty)

            public fun <T> ofTypeCast(message: String? = null, cause: Failure? = null): KorneaResult<T> =
                TypeCastEmpty.of<T>(message, cause)

            public fun <T> ofTypeCastWrapped(
                payload: Any?,
                message: String? = null,
                cause: Failure? = null
            ): KorneaResult<T> =
                TypeCastEmpty.ofWrapped(payload, message, cause)

            @AvailableSince(KorneaErrors.VERSION_3_2_0_INDEV)
            public fun ofNull(): Null =
                Null

            public fun ofNull(message: String? = null, cause: Failure? = null): Null =
                Null.of(message, cause)

            public fun <T> ofNull(payload: T, message: String? = null, cause: Failure? = null): WithPayload<T> =
                Null.of(payload, message, cause)

            public fun <T> ofNull(): KorneaResult<T> =
                KorneaResult(Null)

            public fun <T> ofNull(message: String? = null, cause: Failure? = null): KorneaResult<T> =
                Null.of<T>(message, cause)

            public fun <T> ofNullWrapped(
                payload: Any?,
                message: String? = null,
                cause: Failure? = null
            ): KorneaResult<T> =
                Null.ofWrapped(payload, message, cause)

            @AvailableSince(KorneaErrors.VERSION_3_2_0_INDEV)
            public fun ofUndefined(): Empty =
                Undefined

            public fun ofUndefined(message: String? = null, cause: Failure? = null): Undefined =
                Undefined.of(message, cause)

            public fun <T> ofUndefined(payload: T, message: String? = null, cause: Failure? = null): WithPayload<T> =
                Undefined.of(payload, message, cause)

            public fun <T> ofUndefined(): KorneaResult<T> =
                KorneaResult(Undefined)

            public fun <T> ofUndefined(message: String? = null, cause: Failure? = null): KorneaResult<T> =
                Undefined.of<T>(message, cause)

            public fun <T> ofUndefinedWrapped(
                payload: Any?,
                message: String? = null,
                cause: Failure? = null
            ): KorneaResult<T> =
                Undefined.ofWrapped(payload, message, cause)
        }

        private class Base(val message: String?, override val cause: Failure?) : Empty {
            override fun withCause(newCause: Failure?): Failure =
                Base(message, newCause)

            override fun <R> withPayload(newPayload: R): WithPayload<R> =
                BasePayload(message, newPayload, cause)

            override fun <R> with(newPayload: R, newCause: Failure?): WithPayload<R> =
                BasePayload(message, newPayload, newCause)

            override fun toString(): String =
                if (message != null) "Empty - $message"
                else "Empty"
        }

        private class BasePayload<T>(val message: String?, override val payload: T, override val cause: Failure?) :
            Empty, WithPayload<T> {
            override fun withCause(newCause: Failure?): WithPayload<T> =
                BasePayload(message, payload, newCause)

            override fun <R> withPayload(newPayload: R): WithPayload<R> =
                BasePayload(message, newPayload, cause)

            override fun <R> with(newPayload: R, newCause: Failure?): WithPayload<R> =
                BasePayload(message, newPayload, newCause)

            override fun toString(): String =
                if (message != null) "Empty - $message"
                else "Empty"
        }

        override fun asException(): Throwable =
            IllegalStateException(toString())
    }

    public interface WithException<out E : Throwable> : Failure {
        public companion object {
            public fun <E : Throwable> of(exception: E, cause: Failure? = null): WithException<E> =
                Base(exception, cause)
        }

        private class Base<E : Throwable>(override val exception: E, override val cause: Failure?) : WithException<E> {
            override fun toString(): String = "WithException(exception=$exception)"

            override fun withCause(newCause: Failure?): WithException<E> =
                Base(exception, newCause)

            override fun <R> withPayload(newPayload: R): WithPayload<R> =
                BasePayload(exception, newPayload, cause)

            override fun <R> with(newPayload: R, newCause: Failure?): WithPayload<R> =
                BasePayload(exception, newPayload, newCause)

            override fun <R : Throwable> withException(newException: R): WithException<R> =
                Base(newException, cause)
        }

        private class BasePayload<E : Throwable, T>(
            override val exception: E,
            override val payload: T,
            override val cause: Failure?
        ) : WithException<E>, WithPayload<T> {
            override fun toString(): String = "WithException(exception=$exception)"

            override fun withCause(newCause: Failure?): WithPayload<T> =
                BasePayload(exception, payload, newCause)

            override fun <R> withPayload(newPayload: R): WithPayload<R> =
                BasePayload(exception, newPayload, cause)

            override fun <R> with(newPayload: R, newCause: Failure?): WithPayload<R> =
                BasePayload(exception, newPayload, newCause)

            override fun <R : Throwable> withException(newException: R): WithException<R> =
                BasePayload(newException, payload, cause)
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
        @ThreadLocal
        public companion object {
            public var DEFAULT_GENERATE_STACKTRACE_ON_CREATION: Boolean = false
            public var DEFAULT_INCLUDE_RESULT_CODE_IN_ERROR: Boolean = false

            public inline fun formatErrorMessage(
                errorCode: Int,
                errorMessage: String,
                includeResultCodeInError: Boolean = DEFAULT_INCLUDE_RESULT_CODE_IN_ERROR
            ): String =
                if (includeResultCodeInError) buildErrorMessageWithCode(
                    errorCode,
                    errorMessage
                ) else errorMessage

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
                    (error.cause as? WithException<*>)?.exception,
                    false
                )

            public inline fun illegalArgumentWithResultCode(error: WithErrorDetails): IllegalArgumentException =
                illegalArgumentException(
                    error.errorCode,
                    error.errorMessage,
                    (error.cause as? WithException<*>)?.exception,
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
                    (error.cause as? WithException<*>)?.exception,
                    false
                )

            public inline fun illegalStateWithResultCode(error: WithErrorDetails): IllegalStateException =
                illegalStateException(
                    error.errorCode,
                    error.errorMessage,
                    (error.cause as? WithException<*>)?.exception,
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

            public inline fun <T> asIllegalArgument(
                payload: T,
                errorCode: Int,
                errorMessage: String,
                cause: Failure? = null,
                generateStacktraceOnCreation: Boolean = DEFAULT_GENERATE_STACKTRACE_ON_CREATION,
                includeResultCodeInError: Boolean = DEFAULT_INCLUDE_RESULT_CODE_IN_ERROR
            ): WithPayload<T> =
                if (generateStacktraceOnCreation) {
                    val e =
                        illegalArgumentException(
                            errorCode,
                            errorMessage,
                            (cause as? WithException<*>)?.exception,
                            includeResultCodeInError
                        )

                    of(
                        payload,
                        errorCode,
                        errorMessage,
                        e,
                        cause
                    )
                } else {
                    if (includeResultCodeInError) {
                        of(
                            payload,
                            errorCode,
                            errorMessage,
                            this::illegalArgument,
                            cause
                        )
                    } else {
                        of(
                            payload,
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

            public inline fun <T> asIllegalState(
                payload: T,
                errorCode: Int,
                errorMessage: String,
                cause: Failure? = null,
                generateStacktraceOnCreation: Boolean = DEFAULT_GENERATE_STACKTRACE_ON_CREATION,
                includeResultCodeInError: Boolean = DEFAULT_INCLUDE_RESULT_CODE_IN_ERROR
            ): WithPayload<T> =
                if (generateStacktraceOnCreation) {
                    val e =
                        illegalStateException(
                            errorCode,
                            errorMessage,
                            (cause as? WithException<*>)?.exception,
                            includeResultCodeInError
                        )

                    of(
                        payload,
                        errorCode,
                        errorMessage,
                        e,
                        cause
                    )
                } else {
                    if (includeResultCodeInError) {
                        of(
                            payload,
                            errorCode,
                            errorMessage,
                            this::illegalState,
                            cause
                        )
                    } else {
                        of(
                            payload,
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

            public fun <E : Throwable, T> of(
                payload: T,
                errorCode: Int,
                errorMessage: String,
                exception: E,
                cause: Failure? = null
            ): WithPayload<T> =
                BasePayload(
                    payload,
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

            public fun <E : Throwable, T> of(
                payload: T,
                errorCode: Int,
                errorMessage: String,
                supplier: (WithErrorDetails) -> E,
                cause: Failure? = null
            ): WithPayload<T> =
                BasePayload(
                    payload,
                    errorCode,
                    errorMessage,
                    cause,
                    null,
                    supplier
                )
        }

        @Suppress("PropertyName")
        private open class Base<out E : Throwable>(
            override val errorCode: Int,
            override val errorMessage: String,
            override val cause: Failure?,
            protected val _exception: E?,
            protected val _exceptionSupplier: ((WithErrorDetails) -> E)?
        ) : WithErrorDetails,
            WithException<E> {
            override val exception: E
                get() = _exception ?: _exceptionSupplier!!.invoke(this)

            override fun withCause(newCause: Failure?): Failure =
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

            override fun <R> withPayload(newPayload: R): WithPayload<R> =
                BasePayload(
                    newPayload,
                    errorCode,
                    errorMessage,
                    cause,
                    _exception,
                    _exceptionSupplier
                )

            override fun <R> with(newPayload: R, newCause: Failure?): WithPayload<R> =
                BasePayload(
                    newPayload,
                    errorCode,
                    errorMessage,
                    newCause,
                    _exception,
                    _exceptionSupplier
                )

            override fun toString(): String =
                "WithErrorDetails(errorCode=$errorCode, errorMessage='$errorMessage', cause=$cause, exception=$_exception, exceptionSupplier=$_exceptionSupplier)"

            init {
                require(_exception != null || _exceptionSupplier != null) { "Invalid Error combination WithErrorDetails(errorCode=$errorCode, errorMessage=$errorMessage, exception=null, exceptionSupplier=null)" }
            }
        }

        private class BasePayload<out E : Throwable, T>(
            override val payload: T,
            errorCode: Int,
            errorMessage: String,
            cause: Failure?,
            exception: E?,
            exceptionSupplier: ((WithErrorDetails) -> E)?
        ) : Base<E>(errorCode, errorMessage, cause, exception, exceptionSupplier), WithPayload<T> {
            override fun withCause(newCause: Failure?): WithPayload<T> =
                BasePayload(
                    payload,
                    errorCode,
                    errorMessage,
                    newCause,
                    _exception,
                    _exceptionSupplier
                )
        }
    }

    // discovery

    public val isSuccess: Boolean get() = value !is Failure
    public val isFailure: Boolean get() = value is Failure
    public val isEmpty: Boolean get() = value is Empty
    public val isFailureWithCause: Boolean get() = value is Failure && value.cause != null
    public val isFailureWithException: Boolean get() = value is WithException<*>
    public val isWithErrorCode: Boolean get() = value is WithErrorCode
    public val isWithErrorMessage: Boolean get() = value is WithErrorMessage
    public val isWithErrorDetails: Boolean get() = value is WithErrorDetails
    public val hasPayload: Boolean get() = value is WithPayload<*>

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

public inline fun <T> runResult(block: () -> T): KorneaResult<T> =
    KorneaResult.successOrCatch(block)

/**
 * Runs [block] and returns [T], or null if an exception was thrown.
 */
public inline fun <T> runOrNull(block: () -> T): T? =
    try {
        block()
    } catch (_: Throwable) {
        null
    }

/**
 * Runs [block] and returns [T], or null if an exception was thrown.
 * Prints the exception's stack trace
 */
public inline fun <T> runOrNullStackTrace(block: () -> T): T? =
    try {
        block()
    } catch (th: Throwable) {
        th.printStackTrace()
        null
    }

@PublishedApi
internal fun KorneaResult<*>.throwOnFailure() {
    if (value is KorneaResult.Failure) throw value.asException()
}

public inline fun KorneaResult.Failure.hierarchy(): List<KorneaResult.Failure> =
    ArrayList<KorneaResult.Failure>().apply(this::hierarchyIn)

public inline fun KorneaResult.Failure.hierarchyIn(list: MutableList<KorneaResult.Failure>) {
    var self: KorneaResult.Failure? = this
    while (self != null) {
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
        null -> this
        else -> KorneaResult.failure(failure withCause transform(failure.cause))
    }
}

@AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
public inline fun <T> KorneaResult<T>.mapFailureCause(cause: KorneaResult.Failure?): KorneaResult<T> =
    when (val failure = failureOrNull()) {
        null -> this
        else -> KorneaResult.failure(failure withCause cause)
    }

@AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
public inline fun <T> KorneaResult<T>.mapFailureRootCause(rootCause: KorneaResult.Failure?): KorneaResult<T> =
    when (val failure = failureOrNull()) {
        null -> this
        else -> KorneaResult.failure(failure.hierarchy()
            .asReversed()
            .fold(rootCause) { cause, result -> result withCause cause } ?: failure)
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

@AvailableSince(KorneaErrors.VERSION_3_1_0_INDEV)
public inline fun <R, T : KorneaResult<R>> KorneaResult<T>.flatten(): KorneaResult<R> =
    if (isSuccess) getUnsafe()
    else asType()


@Suppress("UNCHECKED_CAST")
public inline fun <T> KorneaResult<*>.asType(): KorneaResult<T> = this as KorneaResult<T>


@ChangedSince(
    KorneaErrors.VERSION_3_1_0_INDEV,
    "Casting no longer creates a new instance, and when a cast fails instances have the chance to recover themselves"
)
public inline fun <T, reified R> KorneaResult<T>.cast(): KorneaResult<R> =
    if (isSuccess && value !is R)
        KorneaResult.typeCastEmpty()
    else
        asType()

@OptIn(ExperimentalContracts::class)
public inline fun <T, R> KorneaResult<T>.map(transform: (T) -> R): KorneaResult<R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }


    return if (isSuccess)
        KorneaResult(transform(getUnsafe()))
    else
        asType()
}


@OptIn(ExperimentalContracts::class)
public inline fun <T, R> KorneaResult<T>.flatMap(transform: (T) -> KorneaResult<R>): KorneaResult<R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return if (isSuccess)
        transform(getUnsafe())
    else
        asType()
}


@OptIn(ExperimentalContracts::class)
@AvailableSince(KorneaErrors.VERSION_3_3_0_INDEV)
public inline fun <T> KorneaResult<T>.flatMapOrSelf(transform: (T) -> KorneaResult<T>?): KorneaResult<T> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }

    return if (isSuccess)
        when (val result = transform(getUnsafe())) {
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


@AvailableSince(KorneaErrors.VERSION_2_1_0_ALPHA)
public inline fun <T> KorneaResult<Optional<T>>.filter(): KorneaResult<T> =
    if (isSuccess) {
        val opt = getUnsafe()
        if (opt.isPresent) KorneaResult.success(opt.value)
        else KorneaResult.empty()
    } else {
        asType()
    }