@file:Suppress("NOTHING_TO_INLINE")

package dev.brella.kornea.toolkit.common

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public inline fun <T> T?.switchIfNull(other: T): T = this ?: other
public inline fun <T> T?.switchIfNull(other: () -> T): T = this ?: other()

//public inline fun <A, B, R> A.let(b: B, operation: B.(A) -> R): R = b.operation(this)
//public inline fun <A, B> A.let(b: B?, operation: B.(A) -> A): A = b?.operation(this) ?: this
public inline fun <A, B> A?.withOrElse(operation: A.(B) -> B, param: B): B = this?.operation(param) ?: param

public inline fun <T> T.takeIf(predicate: Boolean): T? {
    return if (predicate) this else null
}

public inline fun <T> asNull(): T? = null

@ExperimentalContracts
public inline fun <T, R> freeze(receiver: T, block: (T) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return block(receiver)
}

public inline fun <T> T?.isNull(): Boolean = this == null
public inline fun <T> T?.isNotNull(): Boolean = this != null