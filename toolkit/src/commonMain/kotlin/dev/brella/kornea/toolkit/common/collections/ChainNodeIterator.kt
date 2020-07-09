package dev.brella.kornea.toolkit.common.collections

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.toolkit.common.KorneaToolkit

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public open class ChainNodeIterator<T, C : ChainNode<T>>(startingLink: C?) : Iterator<T> {
    public open class Between<T, C : ChainNode<T>>(head: C, protected val max: C?) : ChainNodeIterator<T, C>(head) {
        public override fun hasNext(): Boolean = super.hasNext() && link != max
    }

    protected var link: C? = startingLink

    /**
     * Returns `true` if the iteration has more elements.
     */
    override fun hasNext(): Boolean = link != null

    /**
     * Returns the next element in the iteration.
     */
    override fun next(): T {
        try {
            return link?.node ?: throw IndexOutOfBoundsException("Chain link is null")
        } finally {
            link = link?.next
        }
    }
}

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public open class MutableChainNodeIterator<T, C : MutableChainNode<T>>(
    link: C?,
    protected val underlyingCollection: MutableCollection<T>
) : ChainNodeIterator<T, C>(link), MutableIterator<T> {
//    public open class Between<C: ChainLink>(head: C, protected val max: C?): ChainIterator<C>(head) {
//        public override fun hasNext(): Boolean = super.hasNext() && link != max
//    }

    protected var previous: C? = null

    override fun next(): T {
        previous = link
        return super.next()
    }

    override fun remove() {
        underlyingCollection.remove(previous?.node ?: throw IndexOutOfBoundsException("No previous element"))
    }
}

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public open class DoubleChainNodeIterator<T, C : DoubleChainNode<T>>(startingLink: C?, underlyingCollection: List<T>?) :
    ListIterator<T> {
    public open class Between<T, C : DoubleChainNode<T>>(
        head: C?,
        protected val min: C?,
        protected val max: C?,
        underlyingCollection: List<T>?
    ) : DoubleChainNodeIterator<T, C>(head, underlyingCollection) {
        public override fun hasNext(): Boolean = super.hasNext() && link != max
        override fun hasPrevious(): Boolean = super.hasPrevious() && link?.previous != min
    }

    protected var link: C? = startingLink

    /**
     * NOTE: Indexes don't mean much for chains by themselves, but if we're in a list it's important we work from the right offset
     */
    protected var index: Int = startingLink?.node?.let { node -> underlyingCollection?.indexOf(node) } ?: 0

    /**
     * Returns `true` if the iteration has more elements.
     */
    override fun hasNext(): Boolean = link != null

    /**
     * Returns `true` if there are elements in the iteration before the current element.
     */
    override fun hasPrevious(): Boolean = link?.previous != null

    override fun next(): T {
        try {
            return link?.node ?: throw IndexOutOfBoundsException("Chain link is null")
        } finally {
            index++
            link = link?.next
        }
    }

    /**
     * Returns the index of the element that would be returned by a subsequent call to [next].
     */
    override fun nextIndex(): Int = index

    /**
     * Returns the previous element in the iteration and moves the cursor position backwards.
     */
    override fun previous(): T {
        val result = link?.previous ?: throw IndexOutOfBoundsException()
        link = result
        index--
        return result.node
    }

    /**
     * Returns the index of the element that would be returned by a subsequent call to [previous].
     */
    override fun previousIndex(): Int = index - 1
}

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public open class MutableDoubleChainNodeIterator<T, C : MutableDoubleChainNode<T>>(
    link: C?,
    protected val underlyingCollection: MutableList<T>
) : DoubleChainNodeIterator<T, C>(link, underlyingCollection), MutableListIterator<T> {
    public open class Between<T, C : MutableDoubleChainNode<T>>(
        head: C?,
        protected val min: C?,
        protected val max: C?,
        underlyingCollection: MutableList<T>
    ) : MutableDoubleChainNodeIterator<T, C>(head, underlyingCollection) {
        public override fun hasNext(): Boolean = super.hasNext() && link != max
        override fun hasPrevious(): Boolean = super.hasPrevious() && link?.previous != min
    }

    override fun add(element: T) {
        underlyingCollection.add(index, element)
    }

    override fun remove() {
        underlyingCollection.removeAt(previousIndex())
    }

    override fun set(element: T) {
        underlyingCollection[index] = element
    }
}