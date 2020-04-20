package org.abimon.kornea.erorrs.common

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

sealed class KorneaResult<T> {
    class Success<T>(val value: T) : KorneaResult<T>() {
        override fun <R> cast(): KorneaResult<R> =
            Empty()

        override fun get(): T = value

        override operator fun component1(): T = value

        override fun toString(): String =
            "Success(value=$value)"
    }

    class Empty<T> : KorneaResult<T>() {
        override fun <R> cast(): KorneaResult<R> = Empty()

        override fun get(): T = throw IllegalStateException("Result is empty")
        override fun component1(): T? = null

        override fun toString(): String =
            "Empty()"
    }

    class Failure<T, R>(val resultCode: Int, val message: String, val causedBy: KorneaResult<R>?) : KorneaResult<T>() {
        companion object {
            operator fun <T> invoke(resultCode: Int, message: String): Failure<T, Unit> =
                Failure(resultCode, message, null)
        }

        override fun <R> cast(): KorneaResult<R> = Failure(resultCode, message, causedBy)

        operator fun component2(): Int = resultCode
        operator fun component3(): String = message
        operator fun component4(): KorneaResult<R>? = causedBy

        override fun get(): T = throw IllegalStateException("Result is in a fail state", asIllegalArgumentException())

        override fun toString(): String =
            "Failure(resultCode=$resultCode, message='$message', causedBy=$causedBy)"

        fun asIllegalArgumentException(): IllegalArgumentException =
            IllegalArgumentException(message, (causedBy as? Failure<R, *>)?.asIllegalArgumentException())

        override fun component1(): T? = null
    }

    abstract fun <R> cast(): KorneaResult<R>

    abstract fun get(): T

    abstract operator fun component1(): T?
}

inline fun <T, R> KorneaResult<T>.map(transform: (T) -> R): KorneaResult<R> =
    if (this is KorneaResult.Success<T>) KorneaResult.Success(transform(value)) else cast()
inline fun <T, R> KorneaResult<T>.flatMap(transform: (T) -> KorneaResult<R>): KorneaResult<R> =
    if (this is KorneaResult.Success<T>) transform(value) else cast()
inline fun <T> KorneaResult<T>.filter(predicate: (T) -> Boolean): KorneaResult<T> =
    if (this is KorneaResult.Success<T> && !predicate(value)) KorneaResult.Empty() else this

inline fun <T> KorneaResult<T>.getOrNull(): T? = if (this is KorneaResult.Success<T>) value else null
inline fun <T> KorneaResult<T>.getOrElse(default: T): T = if (this is KorneaResult.Success<T>) value else default
inline fun <T> KorneaResult<T>.getOrElseRun(block: () -> T): T = if (this is KorneaResult.Success<T>) value else block()



public inline fun <T> KorneaResult<T>.doOnFailure(op: (KorneaResult<T>) -> Unit): T {
    if (this is KorneaResult.Success<T>) return value
    else {
        op(this)
        throw IllegalStateException()
    }
}

@ExperimentalContracts
public inline fun <T : Any> requireSuccessful(value: KorneaResult<T>): T {
    contract {
        returns() implies (value is KorneaResult.Success<T>)
    }

    when (value) {
        is KorneaResult.Success<T> -> return value.value
        is KorneaResult.Failure<T, *> -> throw value.asIllegalArgumentException()
        else -> throw IllegalArgumentException("(empty)")
    }
}

@ExperimentalContracts
public inline fun <T : Any> requireSuccessful(value: KorneaResult<T>, lazyMessage: () -> Any): T {
    contract {
        returns() implies (value is KorneaResult.Success<T>)
    }

    if (value !is KorneaResult.Success<T>) {
        val message = lazyMessage()
        throw IllegalArgumentException(message.toString())
    } else {
        return value.value
    }
}