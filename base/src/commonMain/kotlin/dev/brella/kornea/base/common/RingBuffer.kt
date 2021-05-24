package dev.brella.kornea.base.common

public interface RingBuffer<T> {
    public fun push(value: T)

    public fun pop(): Optional<T>
}

public inline fun <T> RingBuffer<T>.popOrNull(): T? =
    pop().getOrNull()

@PublishedApi
internal object IDLE

public expect class ArrayRingBuffer<T>(backing: Array<Any?>): RingBuffer<T> {
    public companion object {
        public inline operator fun <reified T> invoke(capacity: Int): ArrayRingBuffer<T>
//            withCapacity(capacity)
        public inline fun <reified T> withCapacity(capacity: Int): ArrayRingBuffer<T>
//            ArrayRingBuffer(arrayOfNulls(capacity))
    }

    public var readIndex: Int
        private set
    public var writeIndex: Int
        private set
}

public expect class ListRingBuffer<T>(backing: MutableList<Any?>, maximumCapacity: Int): RingBuffer<T> {
    public companion object {
        public inline operator fun <reified T> invoke(maximumCapacity: Int): ListRingBuffer<T>
        public inline fun <reified T> withCapacity(maximumCapacity: Int): ListRingBuffer<T>
    }

    public val maximumCapacity: Int

    public var readIndex: Int
        private set
    public var writeIndex: Int
        private set
}