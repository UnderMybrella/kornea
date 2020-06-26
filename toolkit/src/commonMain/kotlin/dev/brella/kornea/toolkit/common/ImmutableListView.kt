package dev.brella.kornea.toolkit.common

/**
 * An immutable view of [backing] using an inline class
 */
public inline class ImmutableListView<T>(private val backing: List<T>): List<T> {
    override val size: Int
        get() = backing.size

    override operator fun contains(element: T): Boolean = backing.contains(element)

    override fun containsAll(elements: Collection<T>): Boolean = backing.containsAll(elements)

    /**
     * Returns the element at the specified index in the list.
     */
    override operator fun get(index: Int): T = backing[index]

    /**
     * Returns the index of the first occurrence of the specified element in the list, or -1 if the specified
     * element is not contained in the list.
     */
    override fun indexOf(element: T): Int = backing.indexOf(element)

    override fun isEmpty(): Boolean = backing.isEmpty()

    override fun iterator(): Iterator<T> = backing.iterator()

    /**
     * Returns the index of the last occurrence of the specified element in the list, or -1 if the specified
     * element is not contained in the list.
     */
    override fun lastIndexOf(element: T): Int = backing.lastIndexOf(element)

    /**
     * Returns a list iterator over the elements in this list (in proper sequence).
     */
    override fun listIterator(): ListIterator<T> = backing.listIterator()

    /**
     * Returns a list iterator over the elements in this list (in proper sequence), starting at the specified [index].
     */
    override fun listIterator(index: Int): ListIterator<T> = backing.listIterator(index)

    /**
     * Returns a view of the portion of this list between the specified [fromIndex] (inclusive) and [toIndex] (exclusive).
     * The returned list is backed by this list, so non-structural changes in the returned list are reflected in this list, and vice-versa.
     *
     * Structural changes in the base list make the behavior of the view undefined.
     */
    override fun subList(fromIndex: Int, toIndex: Int): List<T> =
        ImmutableListView(backing.subList(fromIndex, toIndex))
}

public fun <T> List<T>.asImmutableView(): List<T> = ImmutableListView(this)