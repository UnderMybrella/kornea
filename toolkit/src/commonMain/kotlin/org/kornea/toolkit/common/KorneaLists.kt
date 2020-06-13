@file:Suppress("NOTHING_TO_INLINE")

package org.kornea.toolkit.common

public inline fun <T, reified R> List<T>.mapToArray(block: (T) -> R): Array<R> = mapToArray(indices, block)
public inline fun <T, reified R> List<T>.mapToArray(start: Int, block: (T) -> R): Array<R> =
    mapToArray(start..lastIndex, block)

public inline fun <T, reified R> List<T>.mapToArray(range: IntRange, block: (T) -> R): Array<R> =
    Array(range.last - range.first) { i -> block(get(i + range.first)) }


public inline fun <T, R> List<T>.mapToArrayWith(transform: (T) -> R): Array<Pair<T, R>> =
    mapToArrayWith(indices, ::Pair, transform)

public inline fun <T, R> List<T>.mapToArrayWith(start: Int, transform: (T) -> R): Array<Pair<T, R>> =
    mapToArrayWith(start..lastIndex, ::Pair, transform)

public inline fun <T, R> List<T>.mapToArrayWith(range: IntRange, transform: (T) -> R): Array<Pair<T, R>> =
    mapToArrayWith(range, ::Pair, transform)

public inline fun <T, R, reified P> List<T>.mapToArrayWith(
    zip: (T, R) -> P,
    transform: (T) -> R
): Array<P> = mapToArrayWith(indices, zip, transform)

public inline fun <T, R, reified P> List<T>.mapToArrayWith(
    start: Int,
    zip: (T, R) -> P,
    transform: (T) -> R
): Array<P> = mapToArrayWith(start..lastIndex, zip, transform)

public inline fun <T, R, reified P> List<T>.mapToArrayWith(
    range: IntRange,
    zip: (T, R) -> P,
    transform: (T) -> R
): Array<P> =
    Array(range.last - range.first) { i ->
        val element = get(i)
        zip(element, transform(element))
    }

public inline fun <T, C : MutableCollection<T>> C.withElement(t: T): C {
    add(t)
    return this
}

public inline fun <T, C : MutableCollection<T>> C.withElements(t: List<T>): C {
    addAll(t)
    return this
}

public inline fun <T, C : MutableCollection<T>> C.withElements(t: Array<out T>): C {
    addAll(t)
    return this
}

public inline fun <T, C : MutableCollection<T>> T.addTo(c: C): T {
    c.add(this)
    return this
}