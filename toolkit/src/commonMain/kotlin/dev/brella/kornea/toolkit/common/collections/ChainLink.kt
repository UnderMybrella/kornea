package dev.brella.kornea.toolkit.common.collections

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.toolkit.common.KorneaToolkit
import kotlin.js.JsName

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public interface ChainLink : Iterable<ChainLink> {
    public interface ViaProperty : ChainLink {
        @JsName("nextAttr")
        public val next: ChainLink?

        override fun next(): ChainLink? = next
    }

    public fun next(): ChainLink?

    override fun iterator(): Iterator<ChainLink> = ChainIterator(this)
}

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public interface MutableChainLink<C : MutableChainLink<C>> : ChainLink {
    public interface ViaProperty<C : MutableChainLink<C>> : MutableChainLink<C> {
        @JsName("nextAttr")
        public var next: C?
        override fun next(): C? = next
        override fun next(link: C?) {
            next = link
        }
    }

    override fun next(): C?
    public fun next(link: C?)
}

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public interface ReverseChainLink {
    public interface ViaProperty : ReverseChainLink {
        @JsName("previousAttr")
        public val previous: ReverseChainLink?

        override fun previous(): ReverseChainLink? = previous
    }

    public fun previous(): ReverseChainLink?

//    override fun iterator(): Iterator<ChainLink> = ChainIterator(this)
}

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public interface MutableReverseChainLink<C : MutableReverseChainLink<C>> : ReverseChainLink {
    public interface ViaProperty<C : MutableReverseChainLink<C>> : MutableReverseChainLink<C> {
        @JsName("previousAttr")
        public var previous: C?
        override fun previous(): C? = previous
        override fun previous(link: C?) {
            previous = link
        }
    }

    override fun previous(): C?
    public fun previous(link: C?)
}

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public interface DoubleChainLink : ChainLink, ReverseChainLink {
    public interface ViaProperty : DoubleChainLink {
        @JsName("previousAttr")
        public val previous: DoubleChainLink?

        @JsName("nextAttr")
        public val next: DoubleChainLink?

        override fun previous(): DoubleChainLink? = previous
        override fun next(): DoubleChainLink? = next
    }

    override fun previous(): DoubleChainLink?
    override fun next(): DoubleChainLink?

    override fun iterator(): ListIterator<ChainLink> = DoubleChainIterator(this, null)
}

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public interface MutableDoubleChainLink<C : MutableDoubleChainLink<C>> : DoubleChainLink, MutableChainLink<C>,
    MutableReverseChainLink<C> {
    public interface ViaProperty<C : MutableDoubleChainLink<C>> : MutableDoubleChainLink<C>,
        MutableChainLink.ViaProperty<C>, MutableReverseChainLink.ViaProperty<C> {
        override var next: C?
        override var previous: C?

        override fun previous(): C? = previous
        override fun previous(link: C?) {
            previous = link
        }

        override fun next(): C? = next
        override fun next(link: C?) {
            next = link
        }
    }
}

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public interface ChainNode<T> : ChainLink {
    public data class Base<T>(override val node: T, override val next: ChainNode<T>?) : ChainNode<T>,
        ChainLink.ViaProperty

    public companion object {
        public inline operator fun <T> invoke(node: T, next: ChainNode<T>?): ChainNode<T> = Base(node, next)
    }

    public val node: T
}

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public interface MutableChainNode<T> : MutableChainLink<MutableChainNode<T>>, ChainNode<T> {
    public data class Base<T>(override val node: T, override var next: MutableChainNode<T>?) : MutableChainNode<T>,
        MutableChainLink.ViaProperty<MutableChainNode<T>>

    public companion object {
        public inline operator fun <T> invoke(node: T, next: MutableChainNode<T>?): MutableChainNode<T> =
            Base(node, next)
    }
}

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public interface DoubleChainNode<T> : DoubleChainLink, ChainNode<T> {
    public data class Base<T>(
        override val node: T,
        override val previous: DoubleChainNode<T>?,
        override val next: DoubleChainNode<T>?
    ) : DoubleChainNode<T>, DoubleChainLink.ViaProperty

    public companion object {
        public inline operator fun <T> invoke(
            node: T,
            previous: DoubleChainNode<T>?,
            next: DoubleChainNode<T>?
        ): DoubleChainNode<T> = Base(node, previous, next)
    }
}

@AvailableSince(KorneaToolkit.VERSION_2_1_0_ALPHA)
public interface MutableDoubleChainNode<T> : DoubleChainNode<T>, MutableDoubleChainLink<MutableDoubleChainNode<T>> {
    public data class Base<T>(
        override val node: T,
        override var previous: MutableDoubleChainNode<T>?,
        override var next: MutableDoubleChainNode<T>?
    ) : MutableDoubleChainNode<T>, MutableDoubleChainLink.ViaProperty<MutableDoubleChainNode<T>>

    public companion object {
        public inline operator fun <T> invoke(
            node: T,
            previous: MutableDoubleChainNode<T>?,
            next: MutableDoubleChainNode<T>?
        ): MutableDoubleChainNode<T> = Base(node, previous, next)
    }
}

//public inline fun <C: ChainLink> C.next(self: C = this): C? = next() as C
//public inline fun <C: ReverseChainLink> C.previous(self: C = this): C? = previous() as C

public inline val <C : ChainLink> C.next: C?
    get() = next() as? C

public inline val <C : ReverseChainLink> C.previous: C?
    get() = previous() as? C

public inline var <C : MutableChainLink<C>> C.next: C?
    get() = next()
    set(value) {
        next(value)
    }

public inline var <C : MutableReverseChainLink<C>> C.previous: C?
    get() = previous()
    set(value) {
        previous(value)
    }

/**
 * Return the head of this chain, by checking the previous link until there isn't one anymore
 */
public inline fun <C : DoubleChainLink> C.head(): C {
    var head = this
    while (true) head = head.previous ?: return head
}

public inline fun <C : ChainLink> C.tail(): C {
    var tail = this
    while (true) tail = tail.next ?: return tail
}

public inline infix fun <C : MutableDoubleChainLink<C>> C.replaceWith(other: C): C {
    chain(previous(), other, next())
    return other
}

public inline fun <C : MutableDoubleChainLink<C>> C.remove(): C {
    chain(previous(), next())
    return this
}

public inline fun <C : MutableChainLink<C>> chain(link: C?): C? = link
public inline fun <C : MutableDoubleChainLink<C>> chain(link: C?): C? = link

public inline fun <C : MutableChainLink<C>> chain(first: C?, second: C?): C? {
    first?.next(second)

    return first
}

public inline fun <C : MutableDoubleChainLink<C>> chain(first: C?, second: C?): C? {
    first?.next(second)
    second?.previous(first)

    return first
}

public inline fun <C : MutableChainLink<C>> chain(vararg links: C?): C? =
    links.reversed().fold(links.lastOrNull()?.next()) { next, link ->
        link?.next(next)
        link
    }

public inline fun <C : MutableDoubleChainLink<C>> chain(vararg links: C?): C? {
    links.fold(links.firstOrNull()?.previous()) { prev, link ->
        prev?.next(link)
        link?.previous(prev)
        link
    }

    return links.firstOrNull()
}

public inline infix fun <C : MutableChainLink<C>> C.append(other: C): C {
    chain(this, other)

    return other
}

public inline infix fun <C : MutableDoubleChainLink<C>> C.append(other: C): C {
    chain(this, other, next())

    return other
}

public inline infix fun <C : MutableDoubleChainLink<C>> C.prepend(other: C): C {
    chain(previous(), other, this)

    return other
}

public inline infix fun <C : ChainLink> C.stepForwards(nodes: Int): C {
    var prior = this
    repeat(nodes) {
        prior = prior.next ?: throw IndexOutOfBoundsException("Node at index $it has no next element")
    }

    return prior
}

public inline infix fun <C : ReverseChainLink> C.stepBackwards(nodes: Int): C {
    var prior = this
    repeat(nodes) {
        prior = prior.previous ?: throw IndexOutOfBoundsException("Node at index $it has no next element")
    }

    return prior
}