package dev.brella.kornea.toolkit.common

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.errors.common.doOnSuccess
import dev.brella.kornea.errors.common.map
import kotlin.reflect.KMutableProperty0

@AvailableSince(KorneaToolkit.VERSION_1_1_0_ALPHA)
public interface Transactable<T> {
    public fun makeCopy(): T
    public fun acceptCopy(copy: T)
}

@AvailableSince(KorneaToolkit.VERSION_1_1_0_ALPHA)
public inline fun <T> transaction(recipient: ByteArray, action: (ByteArray) -> KorneaResult<T>): KorneaResult<T> =
    transaction(recipient, { o -> o.copyOf() }, { o, c -> c.copyInto(o) }, action)

@AvailableSince(KorneaToolkit.VERSION_1_1_0_ALPHA)
public inline fun <T> transaction(recipient: ShortArray, action: (ShortArray) -> KorneaResult<T>): KorneaResult<T> =
    transaction(recipient, { o -> o.copyOf() }, { o, c -> c.copyInto(o) }, action)

@AvailableSince(KorneaToolkit.VERSION_1_1_0_ALPHA)
public inline fun <T> transaction(recipient: IntArray, action: (IntArray) -> KorneaResult<T>): KorneaResult<T> =
    transaction(recipient, { o -> o.copyOf() }, { o, c -> c.copyInto(o) }, action)

@AvailableSince(KorneaToolkit.VERSION_1_1_0_ALPHA)
public inline fun <T> transaction(recipient: LongArray, action: (LongArray) -> KorneaResult<T>): KorneaResult<T> =
    transaction(recipient, { o -> o.copyOf() }, { o, c -> c.copyInto(o) }, action)

@AvailableSince(KorneaToolkit.VERSION_1_1_0_ALPHA)
public inline fun <T> transaction(recipient: DoubleArray, action: (DoubleArray) -> KorneaResult<T>): KorneaResult<T> =
    transaction(recipient, { o -> o.copyOf() }, { o, c -> c.copyInto(o) }, action)

@AvailableSince(KorneaToolkit.VERSION_1_1_0_ALPHA)
public inline fun <T> transaction(recipient: FloatArray, action: (FloatArray) -> KorneaResult<T>): KorneaResult<T> =
    transaction(recipient, { o -> o.copyOf() }, { o, c -> c.copyInto(o) }, action)

@AvailableSince(KorneaToolkit.VERSION_1_1_0_ALPHA)
public inline fun <T, A> transaction(recipient: Array<A>, action: (Array<A>) -> KorneaResult<T>): KorneaResult<T> =
    transaction(recipient, { o -> o.copyOf() }, { o, c -> c.copyInto(o) }, action)

@AvailableSince(KorneaToolkit.VERSION_1_1_0_ALPHA)
public inline fun <T, reified A> transaction(recipient: MutableList<A>, action: (MutableList<A>) -> KorneaResult<T>): KorneaResult<T> =
    transaction(recipient, { o -> o.toTypedArray().toMutableList() }, { o, c -> o.clear(); o.addAll(c) }, action)

@AvailableSince(KorneaToolkit.VERSION_1_1_0_ALPHA)
public inline fun <T> transaction(recipient: StringBuilder, action: (StringBuilder) -> KorneaResult<T>): KorneaResult<T> =
    transaction(recipient, { o -> StringBuilder(o) }, { o, c -> o.clear(); o.append(c) }, action)

@AvailableSince(KorneaToolkit.VERSION_1_1_0_ALPHA)
public inline fun <T> transaction(recipient: KMutableProperty0<T>, action: (T) -> KorneaResult<T>): KorneaResult<T> =
   action(recipient.get()).doOnSuccess { recipient.set(it) }

@AvailableSince(KorneaToolkit.VERSION_1_1_0_ALPHA)
public inline fun <T> transaction(recipient: Transactable<T>, action: (T) -> KorneaResult<T>): KorneaResult<T> =
    action(recipient.makeCopy()).doOnSuccess { recipient.acceptCopy(it) }

@AvailableSince(KorneaToolkit.VERSION_1_1_0_ALPHA)
public inline fun <T, R> transactionWithResult(recipient: KMutableProperty0<T>, action: (T) -> KorneaResult<Pair<T, R>>): KorneaResult<R> =
    action(recipient.get()).map { (copy, result) -> recipient.set(copy); result }

@AvailableSince(KorneaToolkit.VERSION_1_1_0_ALPHA)
public inline fun <T, R> transactionWithResult(recipient: Transactable<T>, action: (T) -> KorneaResult<Pair<T, R>>): KorneaResult<R> =
    action(recipient.makeCopy()).map { (copy, result) -> recipient.acceptCopy(copy); result }

@AvailableSince(KorneaToolkit.VERSION_1_1_0_ALPHA)
public inline fun <B, T> transaction(recipient: B, startTransaction: (original: B) -> B, finishTransaction: (original: B, copy: B) -> Unit, action: (B) -> KorneaResult<T>): KorneaResult<T> {
    val copy = startTransaction(recipient)

    return action(copy).doOnSuccess { finishTransaction(recipient, copy) }
}