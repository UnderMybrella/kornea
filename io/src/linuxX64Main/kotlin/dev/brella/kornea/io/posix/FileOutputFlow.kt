package dev.brella.kornea.io.posix

import dev.brella.kornea.annotations.ChangedSince
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.Uri
import kotlinx.cinterop.CPointer
import dev.brella.kornea.io.common.flow.CountingOutputFlow
import dev.brella.kornea.io.common.flow.IntFlowState
import dev.brella.kornea.io.common.flow.OutputFlowState
import platform.posix.FILE

@ExperimentalUnsignedTypes
@ChangedSince(KorneaIO.VERSION_5_0_0_ALPHA, "Implement IntFlowState")
public class FileOutputFlow(private val fp: FilePointer) : CountingOutputFlow, BaseDataCloseable(), OutputFlowState, IntFlowState by IntFlowState.base() {
    public constructor(fp: CPointer<FILE>) : this(FilePointer(fp))

    override val streamOffset: Long
        get() = fp.pos()

    private suspend inline fun <T> io(block: () -> T): T = block()

    override fun locationAsUri(): KorneaResult<Uri> = fp.locationAsUrl()

    override suspend fun write(byte: Int): Unit = io { fp.write(byte) }
    override suspend fun write(b: ByteArray, off: Int, len: Int) {
        if (len < 0 || off < 0 || len > b.size - off)
            throw IndexOutOfBoundsException()

        io { fp.write(b, off, len) }
    }

    override suspend fun flush() {
        io { fp.flush() }
    }

    override suspend fun whenClosed() {
        super.whenClosed()

        io { fp.close() }
    }
}