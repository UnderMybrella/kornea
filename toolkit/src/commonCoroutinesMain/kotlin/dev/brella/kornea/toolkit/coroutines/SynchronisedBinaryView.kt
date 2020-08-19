package dev.brella.kornea.toolkit.coroutines

import dev.brella.kornea.toolkit.common.BinaryView
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

public class SynchronisedBinaryView(private val backing: BinaryView, private val semaphore: Semaphore): BinaryView {
    override suspend fun size(): Int = semaphore.withPermit { backing.size() }
    override suspend fun get(index: Int): Int = semaphore.withPermit { backing.size() }
    override suspend fun copyInto(dest: ByteArray, destOffset: Int, start: Int, end: Int): Unit = semaphore.withPermit { backing.copyInto(dest, destOffset, start, end) }
}