package dev.brella.kornea.base.common

public actual class ListRingBuffer<T> public actual constructor(private val backing: MutableList<Any?>, public actual val maximumCapacity: Int): RingBuffer<T> {
    public actual companion object {
        public actual inline operator fun <reified T> invoke(maximumCapacity: Int): ListRingBuffer<T> =
            withCapacity(maximumCapacity)

        public actual inline fun <reified T> withCapacity(maximumCapacity: Int): ListRingBuffer<T> =
            ListRingBuffer(ArrayList(), maximumCapacity)
    }

    public actual var readIndex: Int = 0
    public actual var writeIndex: Int = 0

    override fun push(value: T) {
        if (writeIndex >= backing.size && backing.size < maximumCapacity) {
            backing.add(value)
            writeIndex = backing.size
        } else {
            backing[writeIndex++ % backing.size] = value
        }
    }

    override fun pop(): Optional<T> =
        if (backing.isEmpty())
            Optional.empty()
        else
            backing[readIndex++ % backing.size].let {
                if (it === IDLE) Optional.empty() else Optional(it)
            }
}