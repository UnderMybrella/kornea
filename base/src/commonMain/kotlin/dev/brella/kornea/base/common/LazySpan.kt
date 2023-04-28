package dev.brella.kornea.base.common

public interface LazySpan<out T> : Iterable<T> {
    public val size: Int

    public operator fun get(index: Int): T

    public override operator fun iterator(): Iterator<T> = LazySpanIterator(this)
}

internal class LazySpanIterator<T>(val span: LazySpan<T>) : Iterator<T> {
    var i = 0
    override fun hasNext(): Boolean = i < span.size
    override fun next(): T = span[i++]
}

public fun <T> lazySpanOf(vararg values: T): LazySpan<T> = InitializedLazySpanImpl(values)

public expect fun <T> lazySpan(size: Int, initializer: (Int) -> T): LazySpan<T>
public expect fun <T> lazySpan(size: Int, mode: LazyThreadSafetyMode, initializer: (Int) -> T): LazySpan<T>

internal object UninitialisedElement

internal class UnsafeLazySpanImpl<out T>(override val size: Int, initializer: (Int) -> T) : LazySpan<T> {
    private var initializer: ((Int) -> T)? = initializer
    private var initialisationRemaining = size
    private val _array = Array<Any?>(size) { UninitialisedElement }
    override fun get(index: Int): T {
        var value = _array[index]
        if (value === UninitialisedElement) {
            value = initializer!!(index)
            _array[index] = value

            if (--initialisationRemaining >= size && _array.none { it === UninitialisedElement }) {
                initializer = null
            }
        }

        @Suppress("UNCHECKED_CAST")
        return value as T
    }
}

internal class InitializedLazySpanImpl<out T>(private val array: Array<T>) : LazySpan<T> {
    override val size: Int
        get() = array.size

    override fun get(index: Int): T =
        array[index]

    override fun iterator(): Iterator<T> =
        array.iterator()
}