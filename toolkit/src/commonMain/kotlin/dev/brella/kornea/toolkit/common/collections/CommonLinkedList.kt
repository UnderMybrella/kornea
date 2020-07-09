package dev.brella.kornea.toolkit.common.collections

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.toolkit.common.KorneaToolkit
import dev.brella.kornea.toolkit.common.asNull

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public open class CommonLinkedList<T, C : MutableChainNode<T, C>>(
    initialHead: C?,
    initialTail: C? = initialHead?.tail(),
    protected open val newInstance: (T, C?) -> C
) : AbstractMutableList<T>() {
    public companion object {
        public operator fun <T> invoke(): CommonLinkedList<T, MutableChainNode.Base<T>> =
            CommonLinkedList(null, null, MutableChainNode.Companion::invoke)
    }

    protected open var head: C? = initialHead
    protected open var tail: C? = initialTail
        get() = field ?: head
    protected open var _size: Int = 0
    override val size: Int by ::_size

    override fun get(index: Int): T =
        if (index == 0) head?.node ?: throw IndexOutOfBoundsException("Index: 0, size: $size")
        else (head!! stepForwards index).node

    /**
     * Adds the specified element to the end of this list.
     *
     * @return `true` because the list is always modified as the result of this operation.
     */
    override fun add(element: T): Boolean {
        if (head === null) {
            head = newInstance(element, null)
        } else {
            tail = tail!!.append(newInstance(element, null))
        }
        _size++

        return true
    }

    /**
     * Inserts an element into the list at the specified [index].
     */
    override fun add(index: Int, element: T) {
        when {
            index < 0 || index > size -> throw IndexOutOfBoundsException("Index: $index, size: $size")
            index == 0 -> {
                head = newInstance(element, head)
                _size++
            }
            else -> {
                val n = head!! stepForwards index - 1

                if (n === tail) {
                    tail = newInstance(element, null)
                    n.next = (tail)
                } else {
                    n.append(newInstance(element, null))
                }
                _size++
            }
        }
    }

    /**
     * Removes an element at the specified [index] from the list.
     *
     * @return the element that has been removed.
     */
    override fun removeAt(index: Int): T {
        if (index < 0 || index >= size) throw IndexOutOfBoundsException("Index: $index, size: $size")

        if (index == 0) {
            val result = head!!.node
            head = head!!.next
            _size--
            return result
        } else {
            val n = head!! stepForwards index - 1

            val next = n.next
                ?: throw IllegalStateException("Node at index ${index - 1} has no next element (0 < ${index - 1} < $size)")

            if (next === tail) {
                tail = n
                n.next = null
            } else {
                n.next = next.next
            }
            _size--

            return next.node
        }
    }

    /**
     * Replaces the element at the specified position in this list with the specified element.
     *
     * @return the element previously at the specified position.
     */
    override fun set(index: Int, element: T): T {
        if (index < 0 || index >= size) throw IndexOutOfBoundsException("Index: $index, size: $size")

        if (index == 0) {
            val repl = head!!.node
            head = newInstance(element, head!!.next)
            return repl
        } else {
            val prior = head!! stepForwards index - 1

            val repl = prior.next
                ?: throw IllegalStateException("Node at index ${index - 1} has no next element (0 < ${index - 1} < $size)")

            prior.next = (newInstance(element, repl.next))

            return repl.node
        }
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        if (index < 0 || index > size) throw IndexOutOfBoundsException("Index: $index, size: $size")
        if (elements.isEmpty()) return false

        if (index == 0) {
            head = elements.reversed().fold(head) { next, element -> newInstance(element, next) }
            _size += elements.size
        } else {
            val n = head!! stepForwards index - 1

            if (n === tail) {
                tail = elements.reversed().fold(tail) { next, element -> newInstance(element, next) }
                n.next = (tail)
            } else {
                n.next = elements.reversed().fold(n.next) { next, element -> newInstance(element, next) }
            }
            _size += size
        }

        return true
    }

    override fun addAll(elements: Collection<T>): Boolean {
        if (elements.isEmpty()) return false
        if (head === null) head =
            elements.reversed().fold(asNull<C>()) { next, element -> newInstance(element, next) }!!
        else tail!!.append(
            elements.reversed().fold(asNull<C>()) { next, element -> newInstance(element, next) }!!
        )
        _size += elements.size

        return true
    }

    override fun clear() {
        _size = 0
        head = null
        tail = null
    }

    override operator fun contains(element: T): Boolean = indexOf(element) >= 0
    override fun containsAll(elements: Collection<T>): Boolean {
        val remaining = elements.toMutableList()

        var n = head
        while (n != null) {
            if (remaining.remove(n.node) && remaining.isEmpty()) return true
            n = n.next
        }

        return false
    }

    override fun indexOf(element: T): Int {
        var n = head
        var i = 0
        while (n != null) {
            if (n.node == element) return i
            n = n.next
            i++
        }

        return -1
    }

    override fun isEmpty(): Boolean = _size == 0 && head == null && tail == null

    override fun iterator(): MutableIterator<T> = MutableChainNodeIterator(head, this)

    override fun lastIndexOf(element: T): Int {
        var n = head
        var i = 0
        var index = -1
        while (n != null) {
            if (n.node == element) index = i
            n = n.next
            i++
        }

        return index
    }

//    override fun listIterator(): MutableListIterator<T> =
//    override fun listIterator(index: Int): MutableListIterator<T> {
//        return super.listIterator(index)
//    }

    override fun remove(element: T): Boolean {
//        if (index < 0) throw IndexOutOfBoundsException("$index < 0")
//        else if (index >= size) throw IndexOutOfBoundsException("Index: $index, size: $size")

        var prior = head ?: return false
        var next = prior.next
        while (next != null) {
            if (next.node == element) {
                if (next === tail) {
                    tail = prior
                    tail?.next = null
                } else {
                    prior.next = next.next
                }
                _size--

                return true
            }

            prior = next
            next = prior.next ?: return false
        }

        return false
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        val remaining = elements.toMutableList()

        var prior = head ?: return false
        var n = prior.next
        while (n != null) {
            if (remaining.remove(n.node)) {
                if (n === tail) {
                    tail = prior
                    tail?.next = null
                } else {
                    prior.next = n.next
                }
                _size--

                if (remaining.isEmpty()) break
            }

            prior = n
            n = prior.next ?: break
        }

        return remaining.size != elements.size
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        var prior = head ?: return false
        var n = prior.next
        val startingSize = size

        while (n != null) {
            if (n.node !in elements) {
                if (n === tail) {
                    tail = prior
                    tail?.next = null
                } else {
                    prior.next = n.next
                }
                _size--
            }

            prior = n
            n = prior.next ?: break
        }

        return startingSize != size
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        val subHead = head!! stepForwards fromIndex
        return CommonLinkedList(subHead, subHead stepForwards (toIndex - fromIndex), newInstance)
    }
}