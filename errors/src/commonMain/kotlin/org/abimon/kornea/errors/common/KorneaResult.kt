package org.abimon.kornea.errors.common

import org.abimon.kornea.annotations.ExperimentalKorneaErrors

public interface KorneaResult<out T> {
    @Suppress("NOTHING_TO_INLINE")
    public companion object {
        /**
         * Should calls to [success] use an inline class, rather than the full stable class?
         * Warning: Inline classes are still experimental, and thus may be unstable
         */
        @ExperimentalKorneaErrors
        public var SHOULD_INLINE_CLASSES: Boolean = false

        /**
         * Creates an instance of [KorneaResult] that indicates a successful result
         * This method checks [SHOULD_INLINE_CLASSES] to determine if the resulting instance will be an inlined class or not
         */
        public inline fun <T> success(value: T): KorneaResult<T> = success(value, SHOULD_INLINE_CLASSES)

        /**
         * Creates an instance of [KorneaResult] that indicates a successful result
         * This method checks [useInlineClass] to determine if the resulting instance will be an inlined class or not
         */
        @ExperimentalKorneaErrors
        public inline fun <T> success(value: T, useInlineClass: Boolean): KorneaResult<T> =
            if (useInlineClass) successInline(value) else successStable(value)

        public inline fun <T> successStable(value: T): KorneaResult<T> =
            Success.of(value)

        public inline fun <T> empty(): KorneaResult<T> =
            Empty.of<T>()
        public inline fun <T> failedPredicate(): KorneaResult<T> =
            Empty.ofFailedPredicate<T>()
        public inline fun <T> typeCastEmpty(): KorneaResult<T> =
            Empty.ofTypeCast<T>()
        public inline fun <T> badImplementation(impl: KorneaResult<*>): KorneaResult<T> =
            Empty.ofBadImplementation<T>(impl)

        public inline fun <T, E : Throwable> thrown(exception: E, cause: KorneaResult<*>? = null): KorneaResult<T> =
            WithException.ofTyped(
                exception,
                cause as? Failure
            )

        public inline fun <T> errorAsIllegalArgument(
            errorCode: Int,
            errorMessage: String,
            cause: Failure? = null,
            generateStacktraceOnCreation: Boolean = WithErrorCode.DEFAULT_GENERATE_STACKTRACE_ON_CREATION,
            includeResultCodeInError: Boolean = WithErrorCode.DEFAULT_INCLUDE_RESULT_CODE_IN_ERROR
        ): KorneaResult<T> = WithErrorCode.asIllegalArgument(
            errorCode,
            errorMessage,
            cause,
            generateStacktraceOnCreation,
            includeResultCodeInError
        ).asType()

        public inline fun <T> errorAsIllegalState(
            errorCode: Int,
            errorMessage: String,
            cause: Failure? = null,
            generateStacktraceOnCreation: Boolean = WithErrorCode.DEFAULT_GENERATE_STACKTRACE_ON_CREATION,
            includeResultCodeInError: Boolean = WithErrorCode.DEFAULT_INCLUDE_RESULT_CODE_IN_ERROR
        ): KorneaResult<T> = WithErrorCode.asIllegalState(
            errorCode,
            errorMessage,
            cause,
            generateStacktraceOnCreation,
            includeResultCodeInError
        ).asType()

        public fun <T, E : Throwable> error(
            errorCode: Int,
            errorMessage: String,
            exception: E,
            cause: Failure? = null
        ): KorneaResult<T> = WithErrorCode.of(
            errorCode,
            errorMessage,
            exception,
            cause
        ).asType()

        public fun <T, E : Throwable> error(
            errorCode: Int,
            errorMessage: String,
            supplier: (WithErrorCode) -> E,
            cause: Failure? = null
        ): KorneaResult<T> = WithErrorCode.of(
            errorCode,
            errorMessage,
            supplier,
            cause
        ).asType()

        public fun dirtyImplementationString(impl: KorneaResult<*>): String =
            "Bad implementation of KorneaResult by `${impl::class}`; you need to implement either Success<T> or Failure! (Value was $impl)"
    }

    public interface Success<out T> : KorneaResult<T> {
        public companion object {
            public fun <T> of(value: T): Success<T> =
                Base(value)
        }

        private class Base<T>(private val value: T) : Success<T> {
            override fun get(): T = value

            override fun toString(): String =
                "Success(value=$value)"
        }

        override fun get(): T

        @Deprecated("Breaking a result down to components doesn't end up actually working", ReplaceWith("get()"))
        override operator fun component1(): T = get()
    }

    public interface Failure : KorneaResult<Nothing>

    public interface Empty : Failure {
        public companion object {
            public fun of(): Empty =
                Base
            public fun <T> of(): KorneaResult<T> = Base.asType()

            public fun ofFailedPredicate(): Empty =
                FailedPredicate
            public fun <T> ofFailedPredicate(): KorneaResult<T> = FailedPredicate.asType()

            public fun ofTypeCast(): Empty =
                TypeCastBase
            public fun <T> ofTypeCast(): KorneaResult<T> = TypeCastBase.asType()

            public fun ofBadImplementation(impl: KorneaResult<*>): Empty =
                BadImplementation(impl)
            public fun <T> ofBadImplementation(impl: KorneaResult<*>): KorneaResult<T> =
                BadImplementation(impl).asType()
        }

        private object Base : Empty {
            override fun get() = throw IllegalStateException("Result is empty")
            override fun component1() = throw IllegalStateException("Result is empty")

            override fun toString(): String =
                "Empty()"
        }

        private object FailedPredicate : Empty {
            override fun get() = throw IllegalStateException("Failed predicate")
            override fun component1() = throw IllegalStateException("Failed predicate")

            override fun toString(): String =
                "FailedPredicate()"
        }

        private object TypeCastBase : Empty {
            override fun get() = throw IllegalStateException("Result was miscast")
            override fun component1() = throw IllegalStateException("Result miscast")

            override fun toString(): String =
                "TypeCastEmpty()"
        }

        private class BadImplementation(val impl: KorneaResult<*>) :
            Empty {
            val complainingString: String by lazy {
                dirtyImplementationString(
                    impl
                )
            }

            override fun get() = throw IllegalStateException(complainingString)
            override fun component1() = throw IllegalStateException(complainingString)

            override fun toString(): String =
                "BadImplementation()"
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
        }

        public val exception: E

        override fun get(): Nothing = throw exception
        override fun component1(): Nothing = throw exception
    }

    public interface WithErrorCode : Failure {
        @Suppress("NOTHING_TO_INLINE")
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
                includeResultCodeInError: Boolean = DEFAULT_INCLUDE_RESULT_CODE_IN_ERROR
            ): IllegalArgumentException =
                IllegalArgumentException(
                    formatErrorMessage(
                        errorCode,
                        errorMessage,
                        includeResultCodeInError
                    )
                )

            public inline fun illegalArgument(error: WithErrorCode): IllegalArgumentException =
                illegalArgumentException(
                    error.errorCode,
                    error.errorMessage,
                    false
                )

            public inline fun illegalArgumentWithResultCode(error: WithErrorCode): IllegalArgumentException =
                illegalArgumentException(
                    error.errorCode,
                    error.errorMessage,
                    true
                )

            public inline fun illegalStateException(
                errorCode: Int,
                errorMessage: String,
                includeResultCodeInError: Boolean = DEFAULT_INCLUDE_RESULT_CODE_IN_ERROR
            ): IllegalStateException =
                IllegalStateException(
                    formatErrorMessage(
                        errorCode,
                        errorMessage,
                        includeResultCodeInError
                    )
                )

            public inline fun illegalState(error: WithErrorCode): IllegalStateException =
                illegalStateException(
                    error.errorCode,
                    error.errorMessage,
                    false
                )

            public inline fun illegalStateWithResultCode(error: WithErrorCode): IllegalStateException =
                illegalStateException(
                    error.errorCode,
                    error.errorMessage,
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
                supplier: (WithErrorCode) -> E,
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
            private val _exceptionSupplier: ((WithErrorCode) -> E)?
        ) : WithErrorCode,
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

            override fun get(): Nothing = throw exception
            override fun component1(): Nothing = throw exception

            override fun toString(): String =
                "WithErrorCode(errorCode=$errorCode, errorMessage='$errorMessage', cause=$cause, exception=$_exception, exceptionSupplier=$_exceptionSupplier)"

            init {
                require(_exception != null || _exceptionSupplier != null) { "Invalid Error combination WithErrorCode(errorCode=$errorCode, errorMessage=$errorMessage, exception=null, exceptionSupplier=null)" }
            }
        }

        public val errorCode: Int
        public val errorMessage: String
    }

    public fun get(): T

    @Deprecated("Breaking a result down to components doesn't end up actually working", ReplaceWith("getOrNull()", "org.abimon.knolus.errors.common.getOrNull"))
    public operator fun component1(): T? = getOrNull()
}

public inline fun KorneaResult<*>.hierarchy(): List<KorneaResult<*>> = ArrayList<KorneaResult<*>>().apply(this::hierarchyIn)
public inline fun KorneaResult<*>.hierarchyIn(list: MutableList<KorneaResult<*>>) {
    var self: KorneaResult<*>? = this
    while (self is KorneaResult.WithCause) {
        list.add(self)
        self = self.cause
    }

    self?.let(list::add)
}

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
public inline fun <T> KorneaResult.Failure.asType(): KorneaResult<T> = this

public inline fun <T, reified R> KorneaResult<T>.cast(): KorneaResult<R> =
    when (this) {
        is KorneaResult.Success<T> -> get().let { value -> if (value is R) KorneaResult.success(
            value
        ) else KorneaResult.typeCastEmpty()
        }
        is KorneaResult.Failure -> asType()
        else -> KorneaResult.badImplementation(this)
    }

public inline fun <T, reified R> KorneaResult<T>.map(transform: (T) -> R): KorneaResult<R> =
    when (this) {
        is KorneaResult.Success<T> -> KorneaResult.success(
            transform(get())
        )
        is KorneaResult.Failure -> asType()
        else -> KorneaResult.badImplementation(this)
    }

public inline fun <T, reified R> KorneaResult<T>.flatMap(transform: (T) -> KorneaResult<R>): KorneaResult<R> =
    when (this) {
        is KorneaResult.Success<T> -> transform(get())
        is KorneaResult.Failure -> asType()
        else -> KorneaResult.badImplementation(this)
    }

public inline fun <T> KorneaResult<T>.filter(predicate: (T) -> Boolean): KorneaResult<T> =
    when (this) {
        is KorneaResult.Success<T> -> if (predicate(get())) this else KorneaResult.failedPredicate()
        is KorneaResult.Failure -> asType()
        else -> KorneaResult.badImplementation(this)
    }

public inline fun <T> KorneaResult<T>.filterTo(transform: (T) -> KorneaResult<T>?): KorneaResult<T> =
    when (this) {
        is KorneaResult.Success<T> -> when (val result = transform(get())) {
            null -> KorneaResult.failedPredicate()
            else -> result
        }
        is KorneaResult.Failure -> asType()
        else -> KorneaResult.badImplementation(this)
    }

@Suppress("UNCHECKED_CAST")
public inline fun <reified R> KorneaResult<*>.filterToInstance(): KorneaResult<R> =
    when (this) {
        is KorneaResult.Success -> if (get() is R) this as KorneaResult<R> else KorneaResult.typeCastEmpty()
        is KorneaResult.Failure -> asType()
        else -> KorneaResult.badImplementation(this)
    }

@Suppress("UNCHECKED_CAST")
public inline fun <reified R> KorneaResult<*>.filterToInstance(onEmpty: () -> KorneaResult<R>): KorneaResult<R> =
    when (this) {
        is KorneaResult.Success -> if (get() is R) this as KorneaResult<R> else onEmpty()
        is KorneaResult.Failure -> asType()
        else -> KorneaResult.badImplementation(this)
    }

@Suppress("UNCHECKED_CAST")
public inline fun <reified R> KorneaResult<*>.filterToInstance(default: KorneaResult<R>): KorneaResult<R> =
    when (this) {
        is KorneaResult.Success -> if (get() is R) this as KorneaResult<R> else default
        is KorneaResult.Failure -> asType()
        else -> KorneaResult.badImplementation(this)
    }

@Suppress("UNCHECKED_CAST")
public inline fun <T, reified R : T> KorneaResult<T>.filterToInstance(transform: (T) -> KorneaResult<R>): KorneaResult<R> =
    when (this) {
        is KorneaResult.Success -> when (val result = get()) {
            is R -> this as KorneaResult<R>
            else -> transform(result)
        }
        is KorneaResult.Failure -> asType()
        else -> KorneaResult.badImplementation(this)
    }

public inline fun <T> KorneaResult<T>.getOrNull(): T? = if (this is KorneaResult.Success<T>) get() else null
public inline fun <T> KorneaResult<T>.getOrElse(default: T): T = if (this is KorneaResult.Success<T>) get() else default
public inline fun <T> KorneaResult<T>.getOrElseRun(block: () -> T): T =
    if (this is KorneaResult.Success<T>) get() else block()

public inline fun <T> KorneaResult<T>.getOrElseTransform(block: (KorneaResult.Failure) -> T): T =
    when (this) {
        is KorneaResult.Success<T> -> get()
        is KorneaResult.Failure -> block(this)
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }

public inline fun <T> KorneaResult<T>.orElse(default: KorneaResult<T>): KorneaResult<T> =
    if (this is KorneaResult.Success<T>) this else default

public inline fun <T> KorneaResult<T>.switchIfFailure(block: (KorneaResult.Failure) -> KorneaResult<T>): KorneaResult<T> =
    when (this) {
        is KorneaResult.Success<T> -> this
        is KorneaResult.Failure -> block(this)
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }

public inline fun <T> KorneaResult<T>.switchIfEmpty(block: () -> KorneaResult<T>): KorneaResult<T> =
    when (this) {
        is KorneaResult.Empty -> block()
        is KorneaResult.Success<T> -> this
        is KorneaResult.Failure -> this
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }

public inline fun <T> KorneaResult<T>.switchIfHasErrorCode(block: (KorneaResult.WithErrorCode) -> KorneaResult<T>): KorneaResult<T> =
    when (this) {
        is KorneaResult.WithErrorCode -> block(this)
        is KorneaResult.Success<T> -> this
        is KorneaResult.Failure -> this
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }

public inline fun <T> KorneaResult<T>.switchIfHasException(block: (KorneaResult.WithException<*>) -> KorneaResult<T>): KorneaResult<T> =
    when (this) {
        is KorneaResult.WithException<*> -> block(this)
        is KorneaResult.Success<T> -> this
        is KorneaResult.Failure -> this
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }

@Suppress("UNCHECKED_CAST")
public inline fun <T, reified E : Throwable> KorneaResult<T>.switchIfHasTypedException(block: (KorneaResult.WithException<E>) -> KorneaResult<T>): KorneaResult<T> =
    when (this) {
        is KorneaResult.WithException<*> -> if (exception is E) block(this as KorneaResult.WithException<E>) else this
        is KorneaResult.Success<T> -> this
        is KorneaResult.Failure -> this
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }

/** Run when this result is any failed state */
public inline fun <T> KorneaResult<T>.doOnFailure(block: (KorneaResult<T>) -> Nothing): T =
    when (this) {
        is KorneaResult.Success<T> -> get()
        is KorneaResult.Failure -> block(this)
        else -> throw IllegalStateException(
            KorneaResult.dirtyImplementationString(
                this
            )
        )
    }

/** Run when this result is specifically a known error */
public inline fun <T> KorneaResult<T>.doWithErrorCode(block: (KorneaResult.WithErrorCode) -> Unit): KorneaResult<T> =
    when (this) {
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

public inline fun <T> KorneaResult<T>.doOnEmpty(block: () -> Unit): KorneaResult<T> =
    when (this) {
        is KorneaResult.Empty -> {
            block()
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

public inline fun <T> KorneaResult<T>.doOnThrown(block: (KorneaResult.WithException<*>) -> Unit): KorneaResult<T> =
    when (this) {
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

@Suppress("UNCHECKED_CAST")
public inline fun <T, reified E : Throwable> KorneaResult<T>.doOnTypedThrown(block: (KorneaResult.WithException<E>) -> Unit): KorneaResult<T> =
    when (this) {
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

public inline fun <T> KorneaResult<T>.doOnSuccess(block: (T) -> Unit): KorneaResult<T> =
    when (this) {
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


//@ExperimentalContracts
//public inline fun <T : Any> requireSuccessful(value: KorneaResult<T>): T {
//    contract {
//        returns() implies (value is KorneaResult.Success<T>)
//    }
//
//    when (value) {
//        is KorneaResult.Success<T> -> return value.value
//        is KorneaResult.Failure<T, *> -> throw value.asIllegalArgumentException()
//        else -> throw IllegalArgumentException("(empty)")
//    }
//}
//
//@ExperimentalContracts
//public inline fun <T : Any> requireSuccessful(value: KorneaResult<T>, lazyMessage: () -> Any): T {
//    contract {
//        returns() implies (value is KorneaResult.Success<T>)
//    }
//
//    if (value !is KorneaResult.Success<T>) {
//        val message = lazyMessage()
//        throw IllegalArgumentException(message.toString())
//    } else {
//        return value.value
//    }
//}