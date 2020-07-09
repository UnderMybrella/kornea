package dev.brella.kornea.toolkit.common.collections

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.toolkit.common.KorneaToolkit

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public open class ChainIterator<C: ChainLink>(startingLink: C): Iterator<C> {
    public open class Between<C: ChainLink>(head: C, protected val max: C?): ChainIterator<C>(head) {
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
    override fun next(): C {
        try {
            return link ?: throw IndexOutOfBoundsException("Chain link is null")
        } finally {
            link = link?.next
        }
    }
}

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public open class MutableChainIterator<C: MutableChainLink<C>>(link: C, protected val underlyingCollection: MutableCollection<C>): ChainIterator<C>(link), MutableIterator<C> {
//    public open class Between<C: ChainLink>(head: C, protected val max: C?): ChainIterator<C>(head) {
//        public override fun hasNext(): Boolean = super.hasNext() && link != max
//    }

    protected var previous: C? = null

    override fun next(): C {
        previous = link
        return super.next()
    }

    override fun remove() {
        underlyingCollection.remove(previous ?: throw IndexOutOfBoundsException("No previous element"))
    }
}

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public open class DoubleChainIterator<C: DoubleChainLink>(startingLink: C, underlyingCollection: List<C>?) : ListIterator<C> {
    public open class Between<C: DoubleChainLink>(head: C, protected val min: C?, protected val max: C?, underlyingCollection: List<C>?): DoubleChainIterator<C>(head, underlyingCollection) {
        public override fun hasNext(): Boolean = super.hasNext() && link != max
        override fun hasPrevious(): Boolean = super.hasPrevious() && link?.previous != min
    }

    protected var link: C? = startingLink

    /**
     * NOTE: Indexes don't mean much for chains by themselves, but if we're in a list it's important we work from the right offset
     */
    protected var index: Int = underlyingCollection?.indexOf(startingLink) ?: 0

    /**
     * Returns `true` if the iteration has more elements.
     */
    override fun hasNext(): Boolean = link != null

    /**
     * Returns `true` if there are elements in the iteration before the current element.
     */
    override fun hasPrevious(): Boolean = link?.previous != null

    override fun next(): C {
        try {
            return link ?: throw IndexOutOfBoundsException("Chain link is null")
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
    override fun previous(): C {
        val result = link?.previous ?: throw IndexOutOfBoundsException()
        link = result
        index--
        return result
    }

    /**
     * Returns the index of the element that would be returned by a subsequent call to [previous].
     */
    override fun previousIndex(): Int = index - 1
}

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public open class MutableDoubleChainIterator<C: MutableDoubleChainLink<C>>(link: C, protected val underlyingCollection: MutableList<C>) : DoubleChainIterator<C>(link, underlyingCollection), MutableListIterator<C> {
    public open class Between<C: MutableDoubleChainLink<C>>(head: C, protected val min: C?, protected val max: C?, underlyingCollection: MutableList<C>): MutableDoubleChainIterator<C>(head, underlyingCollection) {
        public override fun hasNext(): Boolean = super.hasNext() && link != max
        override fun hasPrevious(): Boolean = super.hasPrevious() && link?.previous != min
    }

    override fun add(element: C) {
        underlyingCollection.add(index, element)
    }

    override fun remove() {
        underlyingCollection.removeAt(previousIndex())
    }

    override fun set(element: C) {
        underlyingCollection[index] = element
    }
}