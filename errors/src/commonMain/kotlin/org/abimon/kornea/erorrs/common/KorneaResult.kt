package org.abimon.kornea.erorrs.common

sealed class KorneaResult<T> {
    class Success<T>(val value: T) : KorneaResult<T>() {
        override fun get(): T = value

        override operator fun component1(): T = value

        override fun toString(): String =
            "Success(value=$value)"
    }

    class Empty<T> : KorneaResult<T>() {
        override fun get(): T = throw IllegalStateException("Result is empty")
        override fun component1(): T? = null

        override fun toString(): String =
            "Empty()"
    }

    class Error<T, R>(val errorCode: Int, val errorMessage: String, val causedBy: KorneaResult<R>?) : KorneaResult<T>() {
        companion object {
            operator fun <T> invoke(resultCode: Int, message: String): Error<T, Unit> =
                Error(resultCode, message, null)
        }

        operator fun component2(): Int = errorCode
        operator fun component3(): String = errorMessage
        operator fun component4(): KorneaResult<R>? = causedBy

        override fun get(): T = throw IllegalStateException("Result is errored", asIllegalArgumentException())

        override fun toString(): String =
            "Error(errorCode=$errorCode, errorMessage='$errorMessage', causedBy=$causedBy)"

        fun asIllegalArgumentException(): IllegalArgumentException =
            IllegalArgumentException(errorMessage, (causedBy as? Error<R, *>)?.asIllegalArgumentException())

        override fun component1(): T? = null
    }

    class Thrown<T, E : Throwable, R>(val error: E, val causedBy: KorneaResult<R>?) : KorneaResult<T>() {
        companion object {
            operator fun <T, E : Throwable> invoke(error: E): Thrown<T, E, Unit> =
                Thrown(error, null)
        }

        operator fun component2(): E = error
        operator fun component3(): KorneaResult<R>? = causedBy

        override fun get(): T = throw error
        override fun component1(): T? = null
        override fun toString(): String =
            "Thrown(error=$error, causedBy=$causedBy)"
    }

    abstract fun get(): T

    abstract operator fun component1(): T?
}

inline fun <T, reified R> KorneaResult<T>.cast(): KorneaResult<R> =
    when (this) {
        is KorneaResult.Success<T> -> if (value is R) KorneaResult.Success<R>(value) else KorneaResult.Empty<R>()
        is KorneaResult.Empty -> KorneaResult.Empty()
        is KorneaResult.Error<T, *> -> KorneaResult.Error(errorCode, errorMessage, causedBy)
        is KorneaResult.Thrown<T, *, *> -> KorneaResult.Thrown(error, causedBy)
    }

inline fun <T, reified R> KorneaResult<T>.map(transform: (T) -> R): KorneaResult<R> =
    if (this is KorneaResult.Success) KorneaResult.Success(transform(value)) else cast()

inline fun <T, reified R> KorneaResult<T>.flatMap(transform: (T) -> KorneaResult<R>): KorneaResult<R> =
    if (this is KorneaResult.Success<T>) transform(value) else cast()

inline fun <T> KorneaResult<T>.filter(predicate: (T) -> Boolean): KorneaResult<T> =
    if (this is KorneaResult.Success<T> && !predicate(value)) KorneaResult.Empty() else this

inline fun <T> KorneaResult<T>.filterTo(transform: (T) -> KorneaResult<T>?): KorneaResult<T> =
    if (this is KorneaResult.Success<T>) transform(value) ?: this else this

inline fun <reified R> KorneaResult<*>.filterToInstance(): KorneaResult<R> =
    if (this is KorneaResult.Success && value !is R) KorneaResult.Empty() else this.cast()

inline fun <reified R> KorneaResult<*>.filterToInstance(onEmpty: () -> KorneaResult<R>): KorneaResult<R> =
    if (this is KorneaResult.Success && value !is R) onEmpty() else this.cast()

inline fun <reified R> KorneaResult<*>.filterToInstance(default: KorneaResult<R>): KorneaResult<R> =
    if (this is KorneaResult.Success && value !is R) default else this.cast()

inline fun <T, reified R : T> KorneaResult<T>.filterToInstance(transform: (T) -> KorneaResult<R>): KorneaResult<R> =
    if (this is KorneaResult.Success<T> && value !is R) transform(value) else this.cast()

inline fun <T> KorneaResult<T>.getOrNull(): T? = if (this is KorneaResult.Success<T>) value else null
inline fun <T> KorneaResult<T>.getOrElse(default: T): T = if (this is KorneaResult.Success<T>) value else default
inline fun <T> KorneaResult<T>.getOrElseRun(block: () -> T): T = if (this is KorneaResult.Success<T>) value else block()

inline fun <T> KorneaResult<T>.orElse(default: KorneaResult<T>): KorneaResult<T> =
    if (this is KorneaResult.Success<T>) this else default

inline fun <T> KorneaResult<T>.switchIfFailure(block: (KorneaResult<T>) -> KorneaResult<T>): KorneaResult<T> =
    if (this is KorneaResult.Success<T>) this else block(this)

inline fun <T> KorneaResult<T>.switchIfEmpty(block: () -> KorneaResult<T>): KorneaResult<T> =
    if (this is KorneaResult.Empty<T>) block() else this

inline fun <T> KorneaResult<T>.switchIfError(block: (KorneaResult.Error<T, *>) -> KorneaResult<T>): KorneaResult<T> =
    if (this is KorneaResult.Error<T, *>) block(this) else this

inline fun <T> KorneaResult<T>.switchIfThrown(block: (KorneaResult.Thrown<T, *, *>) -> KorneaResult<T>): KorneaResult<T> =
    if (this is KorneaResult.Thrown<T, *, *>) block(this) else this

/** Run when this result is any failed state */
public inline fun <T> KorneaResult<T>.doOnFailure(block: (KorneaResult<T>) -> Unit): T {
    if (this is KorneaResult.Success<T>) return value
    else {
        block(this)
        throw IllegalStateException()
    }
}

/** Run when this result is specifically a known error */
public inline fun <T> KorneaResult<T>.doOnError(block: (KorneaResult.Error<T, *>) -> Unit): KorneaResult<T> {
    if (this is KorneaResult.Error<T, *>) block(this)
    return this
}

public inline fun <T> KorneaResult<T>.doOnEmpty(block: () -> Unit): KorneaResult<T> {
    if (this is KorneaResult.Empty<T>) block()
    return this
}

public inline fun <T> KorneaResult<T>.doOnThrown(block: (KorneaResult.Thrown<T, *, *>) -> Unit): KorneaResult<T> {
    if (this is KorneaResult.Thrown<T, *, *>) block(this)
    return this
}

inline fun <T> KorneaResult<T>.doOnSuccess(block: (T) -> Unit): KorneaResult<T> {
    if (this is KorneaResult.Success<T>) block(value)
    return this
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