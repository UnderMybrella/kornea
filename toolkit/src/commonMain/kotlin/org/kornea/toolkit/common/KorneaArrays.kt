package org.kornea.toolkit.common

public fun byteArrayOfHex(vararg elements: Int): ByteArray = ByteArray(elements.size) { i -> elements[i].toByte() }

public inline fun <reified T> Array<out T>.recast(): Array<T> = Array(size, this::get)

public inline fun <T, reified R> Array<T>.mapToArray(block: (T) -> R): Array<R> = mapToArray(indices, block)
public inline fun <T, reified R> Array<T>.mapToArray(start: Int, block: (T) -> R): Array<R> = mapToArray(start .. lastIndex, block)
public inline fun <T, reified R> Array<T>.mapToArray(range: IntRange, block: (T) -> R): Array<R> =
    Array(range.last - range.first) { i -> block(get(i + range.first)) }

public inline fun <T, R> Array<out T>.mapToArrayWith(transform: (T) -> R): Array<Pair<T, R>> =
    mapToArrayWith(::Pair, transform)

public inline fun <T, R, reified P> Array<out T>.mapToArrayWith(zip: (T, R) -> P, transform: (T) -> R): Array<P> =
    Array(size) { i ->
        val element = get(i)
        zip(element, transform(element))
    }

public fun <T> Array<out T>.toListWith(other: T): List<T> =
    when (size) {
        0 -> listOf(other)
        1 -> listOf(this[0], other)
        else -> this.toMutableListWith(other)
    }

public fun <T> Array<out T>.toMutableListWith(other: T): MutableList<T> {
    val list: MutableList<T> = ArrayList(size + 1)
    list.addAll(this)
    list.add(other)
    return list
}

public inline fun <T, R, reified P> Array<out T>.mapWith(transform: (T) -> R): List<Pair<T, R>> =
    mapToWith(ArrayList(size), ::Pair, transform)

public inline fun <T, R, reified P> Array<out T>.mapWith(zip: (T, R) -> P, transform: (T) -> R): List<P> =
    mapToWith(ArrayList(size), zip, transform)

public inline fun <T, R, C : MutableCollection<Pair<T, R>>, reified P> Array<out T>.mapToWith(
    collection: C,
    transform: (T) -> R
): C = mapToWith(collection, ::Pair, transform)

public inline fun <T, R, C : MutableCollection<P>, reified P> Array<out T>.mapToWith(
    collection: C,
    zip: (T, R) -> P,
    transform: (T) -> R
): C {
    forEach { element -> collection.add(zip(element, transform(element))) }

    return collection
}