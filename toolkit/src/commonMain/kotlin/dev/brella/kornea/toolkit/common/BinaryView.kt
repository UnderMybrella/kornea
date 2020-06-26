package dev.brella.kornea.toolkit.common

import dev.brella.kornea.annotations.AvailableSince
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

@AvailableSince(KorneaToolkit.VERSION_1_0_0_ALPHA)
public interface BinaryView {
    public suspend fun size(): Int
    public suspend fun get(index: Int): Int
    public suspend fun copyInto(dest: ByteArray, destOffset: Int, start: Int, end: Int)
}

public class SynchronisedBinaryView(private val backing: BinaryView, private val semaphore: Semaphore): BinaryView {
    override suspend fun size(): Int = semaphore.withPermit { backing.size() }
    override suspend fun get(index: Int): Int = semaphore.withPermit { backing.size() }
    override suspend fun copyInto(dest: ByteArray, destOffset: Int, start: Int, end: Int): Unit = semaphore.withPermit { backing.copyInto(dest, destOffset, start, end) }
}

public inline class BinaryArrayView(private val backing: ByteArray): BinaryView, Iterable<Int> {
    override suspend fun size(): Int = backing.size
    override suspend fun get(index: Int): Int = backing[index].toInt() and 0xFF
    override suspend fun copyInto(dest: ByteArray, destOffset: Int, start: Int, end: Int) {
        backing.copyInto(dest, destOffset, start, end)
    }

    override fun iterator(): Iterator<Int> = backing.iterator().map { it.toInt() and 0xFF }
}

public inline class BinaryListView(private val backing: List<Byte>): BinaryView, Iterable<Int> {
    override suspend fun size(): Int = backing.size
    override suspend fun get(index: Int): Int = backing[index].toInt() and 0xFF
    override suspend fun copyInto(dest: ByteArray, destOffset: Int, start: Int, end: Int) {
        backing.subList(start, end).forEachIndexed { index, byte -> dest[destOffset + index] = byte }
    }

    override fun iterator(): Iterator<Int> = backing.iterator().map { it.toInt() and 0xFF }
}