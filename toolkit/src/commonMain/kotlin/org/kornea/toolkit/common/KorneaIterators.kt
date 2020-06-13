package org.kornea.toolkit.common

public fun <T, V> Array<T>.iterator(map: (T) -> V): Iterator<V> = MappingIterator(this.iterator(), map)
public fun <T, V> Iterable<T>.iterator(map: (T) -> V): Iterator<V> = MappingIterator(this.iterator(), map)
public fun <T, V> Iterator<T>.map(map: (T) -> V): Iterator<V> = MappingIterator(this, map)
//public fun <T, V> Enumeration<T>.iterator(map: (T) -> V): Iterator<V> = MappingIterator(this.iterator(), map)

public inline fun <T, R> Iterable<T>.mapFirst(map: (T) -> R?): R {
    for (element in this) return map(element) ?: continue

    throw NoSuchElementException("Collection contains no element matching the predicate.")
}

public inline fun <T, R> Iterable<T>.mapFirst(predicate: (T) -> Boolean, map: (T) -> R): R {
    for (element in this) {
        if (predicate(element))
            return map(element)
    }

    throw NoSuchElementException("Collection contains no element matching the predicate.")
}

/**
 * Returns the sum of all values produced by [selector] function applied to each element in the collection.
 */
public inline fun <T> Iterable<T>.sumByLong(selector: (T) -> Number): Long {
    var sum: Long = 0
    for (element in this) {
        sum += selector(element).toLong()
    }
    return sum
}