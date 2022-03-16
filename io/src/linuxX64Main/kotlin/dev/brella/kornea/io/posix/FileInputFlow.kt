package dev.brella.kornea.io.posix

import dev.brella.kornea.annotations.ChangedSince
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.EnumSeekMode
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.Uri
import dev.brella.kornea.io.common.flow.InputFlowState
import dev.brella.kornea.io.common.flow.IntFlowState
import dev.brella.kornea.io.common.flow.SeekableInputFlow
import kotlinx.cinterop.CPointer
import platform.posix.FILE
import platform.posix.SEEK_CUR
import platform.posix.SEEK_END
import platform.posix.SEEK_SET

@ChangedSince(KorneaIO.VERSION_5_0_0_ALPHA, "Implement IntFlowState")
public class FileInputFlow(private val fp: FilePointer, override val location: String? = null) : SeekableInputFlow,
    BaseDataCloseable(), InputFlowState, IntFlowState by IntFlowState.base() {
    public constructor(fp: CPointer<FILE>, location: String? = null) : this(
        FilePointer(fp), location
    )

    private val size = fp.size()

    private inline fun <T> io(block: () -> T): T = block()

    override fun locationAsUri(): KorneaResult<Uri> = fp.locationAsUrl()

    override suspend fun read(): Int? = io { fp.read() }
    override suspend fun read(b: ByteArray, off: Int, len: Int): Int? {
        if (len < 0 || off < 0 || len > b.size - off)
            throw IndexOutOfBoundsException()

        return io { fp.read(b, off, len).toInt() }
    }

    override suspend fun skip(n: ULong): ULong? = io {
        val pos = fp.pos()
        if (fp.seek(n.toLong(), SEEK_CUR) != 0) return@io null
        return@io (fp.pos() - pos).toULong()
    }

    override suspend fun available(): ULong = remaining()
    override suspend fun remaining(): ULong = size() - position()
    override suspend fun size(): ULong = size.toULong()
    override suspend fun position(): ULong = io { fp.pos().toULong() }

    override suspend fun seek(pos: Long, mode: EnumSeekMode): ULong {
        when (mode) {
            EnumSeekMode.FROM_BEGINNING -> io { fp.seek(pos, SEEK_SET) }
            EnumSeekMode.FROM_POSITION -> io { fp.seek(pos, SEEK_CUR) }
            EnumSeekMode.FROM_END -> io { fp.seek(pos, SEEK_END) }
        }

        return position()
    }

    override suspend fun whenClosed() {
        super.whenClosed()

        io { fp.close() }
    }
}