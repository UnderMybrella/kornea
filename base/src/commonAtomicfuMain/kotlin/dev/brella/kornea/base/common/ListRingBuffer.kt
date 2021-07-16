package dev.brella.kornea.base.common

import kotlinx.atomicfu.atomic

public actual class ListRingBuffer<T> public actual constructor(
    private val backing: MutableList<Any?>,
    public actual val maximumCapacity: Int
) : RingBuffer<T> {
    public actual companion object {
        public actual inline operator fun <reified T> invoke(maximumCapacity: Int): ListRingBuffer<T> =
            withCapacity(maximumCapacity)

        public actual inline fun <reified T> withCapacity(maximumCapacity: Int): ListRingBuffer<T> =
            ListRingBuffer(ArrayList(), maximumCapacity)
    }

    private val _readIndex = atomic(0)
    private val _writeIndex = atomic(0)

    public actual var readIndex: Int by _readIndex
    public actual var writeIndex: Int by _writeIndex

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
        else {
            val index = readIndex++ % backing.size
            val optional = backing[index].let { if (it === IDLE) Optional.empty<T>() else Optional<T>(it) }
            backing[index] = IDLE
            optional
        }
}