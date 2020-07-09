package dev.brella.kornea.toolkit.common.collections

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.toolkit.common.KorneaToolkit

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public open class ChainIterator<C: ChainLink<C>>(startingLink: C): Iterator<C> {
    public open class Between<C: ChainLink<C>>(head: C, protected val max: C?): ChainIterator<C>(head) {
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
            return link ?: throw NoSuchElementException()
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
        underlyingCollection.remove(previous ?: throw NoSuchElementException())
    }
}

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public open class ReverseChainIterator<C: ReverseChainLink<C>>(startingLink: C): Iterator<C> {
    public open class Between<C: ChainLink<C>>(head: C, protected val max: C?): ChainIterator<C>(head) {
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
            return link ?: throw NoSuchElementException()
        } finally {
            link = link?.previous
        }
    }
}

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public open class MutableReverseChainIterator<C: MutableReverseChainLink<C>>(link: C, protected val underlyingCollection: MutableCollection<C>): ReverseChainIterator<C>(link), MutableIterator<C> {
//    public open class Between<C: ChainLink>(head: C, protected val max: C?): ChainIterator<C>(head) {
//        public override fun hasNext(): Boolean = super.hasNext() && link != max
//    }

    protected var previous: C? = null

    override fun next(): C {
        previous = link
        return super.next()
    }

    override fun remove() {
        underlyingCollection.remove(previous ?: throw NoSuchElementException())
    }
}


@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public open class DoubleChainIterator<C: DoubleChainLink<C>>(startingLink: C, underlyingCollection: List<C>?) : ListIterator<C> {
    public open class Between<C: DoubleChainLink<C>>(head: C, protected val min: C?, protected val max: C?, underlyingCollection: List<C>?): DoubleChainIterator<C>(head, underlyingCollection) {
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
            return link ?: throw NoSuchElementException()
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
public open class MutableDoubleChainIterator<C: MutableDoubleChainLink<C>>(link: C, protected val underlyingCollection: MutableList<C>?) : DoubleChainIterator<C>(link, underlyingCollection), MutableListIterator<C> {
    public open class Between<C: MutableDoubleChainLink<C>>(head: C, protected val min: C?, protected val max: C?, underlyingCollection: MutableList<C>): MutableDoubleChainIterator<C>(head, underlyingCollection) {
        public override fun hasNext(): Boolean = super.hasNext() && link != max
        override fun hasPrevious(): Boolean = super.hasPrevious() && link?.previous != min
    }

    override fun add(element: C) {
        underlyingCollection?.add(index, element) ?: (link?.prepend(element) ?: throw NoSuchElementException())
    }

    override fun remove() {
        underlyingCollection?.removeAt(previousIndex()) ?: (link?.previous?.remove() ?: throw NoSuchElementException())
    }

    override fun set(element: C) {
        underlyingCollection?.set(index, element) ?: (link?.replaceWith(element) ?: throw NoSuchElementException())
    }
}