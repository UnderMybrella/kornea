package dev.brella.kornea.toolkit.common

import dev.brella.kornea.annotations.AvailableSince
import kotlin.jvm.JvmInline

@AvailableSince(KorneaToolkit.VERSION_1_0_0_ALPHA)
public interface BinaryView {
    public suspend fun size(): Int
    public suspend fun get(index: Int): Int
    public suspend fun copyInto(dest: ByteArray, destOffset: Int, start: Int, end: Int)
}

@JvmInline
public value class BinaryArrayView(private val backing: ByteArray): BinaryView, Iterable<Int> {
    override suspend fun size(): Int = backing.size
    override suspend fun get(index: Int): Int = backing[index].toInt() and 0xFF
    override suspend fun copyInto(dest: ByteArray, destOffset: Int, start: Int, end: Int) {
        backing.copyInto(dest, destOffset, start, end)
    }

    override fun iterator(): Iterator<Int> = backing.iterator().map { it.toInt() and 0xFF }
}

@JvmInline
public value class BinaryListView(private val backing: List<Byte>): BinaryView, Iterable<Int> {
    override suspend fun size(): Int = backing.size
    override suspend fun get(index: Int): Int = backing[index].toInt() and 0xFF
    override suspend fun copyInto(dest: ByteArray, destOffset: Int, start: Int, end: Int) {
        backing.subList(start, end).forEachIndexed { index, byte -> dest[destOffset + index] = byte }
    }

    override fun iterator(): Iterator<Int> = backing.iterator().map { it.toInt() and 0xFF }
}