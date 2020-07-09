package dev.brella.kornea.toolkit.common.collections

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.toolkit.common.KorneaToolkit
import kotlin.js.JsName

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public interface ChainLink<out C: ChainLink<C>>: Iterable<C> {
    public val next: C?

    override fun iterator(): Iterator<C> = ChainIterator(this as C)
}

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public interface MutableChainLink<C : MutableChainLink<C>> : ChainLink<C> {
    override var next: C?
}

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public interface ReverseChainLink<out C: ReverseChainLink<C>> {
    public val previous: C?

//    override fun iterator(): Iterator<ChainLink> = ChainIterator(this)
}

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public interface MutableReverseChainLink<C : MutableReverseChainLink<C>> : ReverseChainLink<C> {
    override var previous: C?
}

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public interface DoubleChainLink<out C: DoubleChainLink<C>> : ChainLink<C>, ReverseChainLink<C>

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public interface MutableDoubleChainLink<C : MutableDoubleChainLink<C>> : DoubleChainLink<C>, MutableChainLink<C>, MutableReverseChainLink<C> {
    override var previous: C?
    override var next: C?
}

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public interface ChainNode<out T, out C: ChainNode<T, C>> : ChainLink<C> {
    public data class Base<T>(override val node: T, override val next: ChainNode<T, *>?) : ChainNode<T, ChainNode<T, *>>

    public companion object {
        public inline operator fun <T> invoke(node: T, next: ChainNode<T, *>?): ChainNode<T, *> = Base(node, next)
    }

    public val node: T
}

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public interface MutableChainNode<T, C: MutableChainNode<T, C>> : MutableChainLink<C>, ChainNode<T, C> {
    public data class Base<T>(override val node: T, override var next: Base<T>?) : MutableChainNode<T, Base<T>>

    public companion object {
        public inline operator fun <T> invoke(node: T, next: Base<T>?): Base<T> =
            Base(node, next)
    }
}

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public interface DoubleChainNode<T, out C: DoubleChainNode<T, C>> : DoubleChainLink<C>, ChainNode<T, C> {
    public data class Base<T>(
        override val node: T,
        override val previous: DoubleChainNode<T, *>?,
        override val next: DoubleChainNode<T, *>?
    ) : DoubleChainNode<T, DoubleChainNode<T, *>>

    public companion object {
        public inline operator fun <T> invoke(
            node: T,
            previous: DoubleChainNode<T, *>?,
            next: DoubleChainNode<T, *>?
        ): DoubleChainNode<T, *> = Base(node, previous, next)
    }
}

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public interface MutableDoubleChainNode<T, C: MutableDoubleChainNode<T, C>> : MutableChainNode<T, C>, DoubleChainNode<T, C>, MutableDoubleChainLink<C> {
    public data class Base<T>(
        override val node: T,
        override var previous: Base<T>?,
        override var next: Base<T>?
    ) : MutableDoubleChainNode<T, Base<T>>

    public companion object {
        public inline operator fun <T> invoke(
            node: T,
            previous: Base<T>?,
            next: Base<T>?
        ): Base<T> = Base(node, previous, next)
    }
}

//public inline fun <C: ChainLink> C.next(self: C = this): C? = next() as C
//public inline fun <C: ReverseChainLink> C.previous(self: C = this): C? = previous() as C

//public operator fun <C: ChainLink> C.iterator(): Iterator<C> = ChainIterator(this)
//public operator fun <C: DoubleChainLink> C.iterator(): ListIterator<C> = DoubleChainIterator(this, null)
//public operator fun <C: MutableDoubleChainLink<C>> C.iterator(): MutableListIterator<C> = MutableDoubleChainIterator(this, null)

//public inline val <C : ChainLink> C.next: C?
//    get() = next() as? C
//
//public inline val <C : ReverseChainLink> C.previous: C?
//    get() = previous() as? C

//public inline var <C : MutableChainLink<C>> C.next: C?
//    get() = next()
//    set(value) {
//        next(value)
//    }
//
//public inline var <C : MutableReverseChainLink<C>> C.previous: C?
//    get() = previous()
//    set(value) {
//        previous(value)
//    }

/**
 * Return the head of this chain, by checking the previous link until there isn't one anymore
 */
public inline fun <C : DoubleChainLink<C>> C.head(): C {
    var head = this
    while (true) head = head.previous ?: return head
}

public inline fun <C : ChainLink<C>> C.tail(): C {
    var tail = this
    while (true) tail = tail.next ?: return tail
}

public inline infix fun <C : MutableDoubleChainLink<C>> C.replaceWith(other: C): C {
    chain(previous, other, next)
    return other
}

public inline fun <C : MutableDoubleChainLink<C>> C.remove(): C {
    chain(previous, next)
    return this
}

public inline fun <C : MutableChainLink<C>> chain(link: C?): C? = link
public inline fun <C : MutableDoubleChainLink<C>> chain(link: C?): C? = link

public inline fun <C : MutableChainLink<C>> chain(first: C?, second: C?): C? {
    first?.next = second

    return first
}

public inline fun <C : MutableDoubleChainLink<C>> chain(first: C?, second: C?): C? {
    first?.next = second
    second?.previous = first

    return first
}

public inline fun <C : MutableChainLink<C>> chain(vararg links: C?): C? =
    links.reversed().fold(links.lastOrNull()?.next) { next, link ->
        link?.next = next
        link
    }

public inline fun <C : MutableDoubleChainLink<C>> chain(vararg links: C?): C? {
    links.fold(links.firstOrNull()?.previous) { prev, link ->
        prev?.next = link
        link?.previous = prev
        link
    }

    return links.firstOrNull()
}

public inline infix fun <C : MutableChainLink<C>> C.append(other: C): C {
    chain(this, other)

    return other
}

public inline infix fun <C : MutableDoubleChainLink<C>> C.append(other: C): C {
    chain(this, other, next)

    return other
}

public inline infix fun <C : MutableDoubleChainLink<C>> C.prepend(other: C): C {
    chain(previous, other, this)

    return other
}

public inline infix fun <C : ChainLink<C>> C.stepForwards(nodes: Int): C {
    var prior = this
    repeat(nodes) {
        prior = prior.next ?: throw IndexOutOfBoundsException("Node at index $it has no next element")
    }

    return prior
}

public inline infix fun <C : ReverseChainLink<C>> C.stepBackwards(nodes: Int): C {
    var prior = this
    repeat(nodes) {
        prior = prior.previous ?: throw IndexOutOfBoundsException("Node at index $it has no next element")
    }

    return prior
}