package dev.brella.kornea.base.common

public actual class ArrayRingBuffer<T> public actual constructor(private val backing: Array<Any?>) : RingBuffer<T> {
    public actual companion object {
        public actual inline operator fun <reified T> invoke(capacity: Int): ArrayRingBuffer<T> =
            withCapacity(capacity)

        public actual inline fun <reified T> withCapacity(capacity: Int): ArrayRingBuffer<T> =
            ArrayRingBuffer(Array(capacity) { IDLE })
    }

    public actual var readIndex: Int = 0
    public actual var writeIndex: Int = 0

    override fun push(value: T) {
        backing[writeIndex++ % backing.size] = value
    }

    override fun pop(): Optional<T> =
        if (backing.isEmpty())
            Optional.empty()
        else
            backing[readIndex++ % backing.size].let {
                if (it === IDLE) Optional.empty() else Optional(it)
            }
}