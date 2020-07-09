package dev.brella.kornea.toolkit.common.collections

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.toolkit.common.KorneaToolkit
import dev.brella.kornea.toolkit.common.asNull

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public open class CommonDoublyLinkedList<T>(
    initialHead: MutableDoubleChainNode<T>? = null,
    initialTail: MutableDoubleChainNode<T>? = initialHead?.tail()
) : AbstractMutableList<T>() {
    protected open var head: MutableDoubleChainNode<T>? = initialHead
    protected open var tail: MutableDoubleChainNode<T>? = initialTail
        get() = field ?: head
    protected open var _size: Int = 0
    override val size: Int by ::_size

    private inline fun node(index: Int): MutableDoubleChainNode<T> =
        if (index > _size shr 1) tail!! stepBackwards size - index - 1
        else head!! stepForwards index

    override fun get(index: Int): T =
        if (index == 0) head?.node ?: throw IndexOutOfBoundsException("Index: 0, size: $size")
        else node(index).node

    /**
     * Adds the specified element to the end of this list.
     *
     * @return `true` because the list is always modified as the result of this operation.
     */
    override fun add(element: T): Boolean {
        if (head === null) {
            head = MutableDoubleChainNode(element, null, null)
        } else {
            tail = tail!!.append(MutableDoubleChainNode(element, null, null))
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
                head = MutableDoubleChainNode(element, null, head)
                _size++
            }
            index == size -> {
                tail = MutableDoubleChainNode(element, tail, null)
                _size++
            }
            else -> {
                val n = node(index)
                n.prepend(MutableDoubleChainNode(element, null, null))
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
        when {
            index < 0 || index >= size -> throw IndexOutOfBoundsException("Index: $index, size: $size")
            index == 0 -> {
                val result = head!!.node
                head = head!!.next()
                _size--
                return result
            }
            index == size - 1 -> {
                val result = tail!!.node
                head = tail!!.previous()
                _size--
                return result
            }
            else -> {
                val n = node(index)
                n.remove()
                _size--
                return n.node
            }
        }
    }

    /**
     * Replaces the element at the specified position in this list with the specified element.
     *
     * @return the element previously at the specified position.
     */
    override fun set(index: Int, element: T): T {
        when {
            index < 0 || index >= size -> throw IndexOutOfBoundsException("Index: $index, size: $size")
            index == 0 -> {
                val repl = head!!.node
                head = head!!.replaceWith(MutableDoubleChainNode(element, null, null))
                return repl
            }
            index == size - 1 -> {
                val repl = tail!!.node
                tail = tail!!.replaceWith(MutableDoubleChainNode(element, null, null))
                return repl
            }
            else -> {
                val n = node(index)
                n.replaceWith(MutableDoubleChainNode(element, null, null))
                return n.node
            }
        }
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        when {
            index < 0 || index > size -> throw IndexOutOfBoundsException("Index: $index, size: $size")
            elements.isEmpty() -> return false
            index == 0 -> {
                head = chain(elements.fold(asNull()) { previous, element ->
                    MutableDoubleChainNode(
                        element,
                        previous,
                        null
                    ).apply { previous?.next(this) }
                }, head)
                _size += elements.size
            }
            index == size -> {
                head = elements.fold(tail) { previous, element ->
                    MutableDoubleChainNode(
                        element,
                        previous,
                        null
                    ).apply { previous?.next(this) }
                }
                _size += elements.size
            }
            else -> {
                val n = node(index)

                chain(
                    n,
                    elements.fold(n.previous()) { previous, element ->
                        MutableDoubleChainNode(
                            element,
                            previous,
                            null
                        ).apply { previous?.next(this) }
                    })
                _size += size
            }
        }

        return true
    }

    override fun addAll(elements: Collection<T>): Boolean {
        if (elements.isEmpty()) return false
        if (head === null)
            head = elements.fold(asNull()) { previous, element ->
                MutableDoubleChainNode(element, previous, null)
                    .apply { previous?.next(this) }
            }
        else
            tail!!.append(elements.fold(asNull<MutableDoubleChainNode<T>>()) { previous, element ->
                MutableDoubleChainNode(element, previous, null)
                    .apply { previous?.next(this) }
            }!!)
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
            n = n.next()
        }

        return false
    }

    override fun indexOf(element: T): Int {
        var n = head
        var i = 0
        while (n != null) {
            if (n.node == element) return i
            n = n.next()
            i++
        }

        return -1
    }

    override fun isEmpty(): Boolean = _size == 0 && head == null && tail == null

    override fun iterator(): MutableListIterator<T> = MutableDoubleChainNodeIterator(head, this)
    override fun listIterator(): MutableListIterator<T> = iterator()
    override fun listIterator(index: Int): MutableListIterator<T> = MutableDoubleChainNodeIterator(node(index), this)

    override fun lastIndexOf(element: T): Int {
        var n = tail
        var i = _size - 1
        var index = -1
        while (n != null) {
            if (n.node == element) return i
            n = n.previous()
            i--
        }

        return index
    }

    override fun remove(element: T): Boolean {
//        if (index < 0) throw IndexOutOfBoundsException("$index < 0")
//        else if (index >= size) throw IndexOutOfBoundsException("Index: $index, size: $size")

        var prior = head ?: return false
        var next = prior.next()
        while (next != null) {
            if (next.node == element) {
                if (next === tail) {
                    tail = prior
                    tail?.next(null)
                } else {
                    prior.next(next.next())
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
        var n = prior.next()
        while (n != null) {
            if (remaining.remove(n.node)) {
                if (n === tail) {
                    tail = prior
                    tail?.next(null)
                } else {
                    prior.next(n.next())
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
        var n = prior.next()
        val startingSize = size

        while (n != null) {
            if (n.node !in elements) {
                if (n === tail) {
                    tail = prior
                    tail?.next(null)
                } else {
                    prior.next(n.next())
                }
                _size--
            }

            prior = n
            n = prior.next ?: break
        }

        return startingSize != size
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        val subHead = node(fromIndex)
        val distance = toIndex - fromIndex
        val subTail =
            if (distance < size - toIndex - 1) subHead stepForwards distance else tail!! stepBackwards size - toIndex - 1
        return CommonDoublyLinkedList(subHead, subTail)
    }
}