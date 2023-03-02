package dev.brella.kornea.io.posix

import dev.brella.kornea.annotations.ChangedSince
import dev.brella.kornea.composite.common.Constituent
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.EnumSeekMode
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.Uri
import dev.brella.kornea.io.common.flow.*
import kotlinx.cinterop.CPointer
import platform.posix.FILE
import platform.posix.SEEK_CUR
import platform.posix.SEEK_END
import platform.posix.SEEK_SET

@ChangedSince(KorneaIO.VERSION_5_0_0_ALPHA, "Implement IntFlowState")
public class FileOutputFlow(private val fp: FilePointer, override val location: String? = null) : OutputFlow,
    BaseDataCloseable(), OutputFlowState,
    IntFlowState by IntFlowState.base(), SeekableFlow {

    public constructor(fp: CPointer<FILE>, location: String? = null) : this(FilePointer(fp), location)

    override val flow: KorneaFlow
        get() = this

    override suspend fun position(): ULong =
        fp.pos().toULong()

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

    override fun hasConstituent(key: Constituent.Key<*>): Boolean =
        when (key) {
            SeekableFlow.Key -> true
            else -> false
        }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Constituent> getConstituent(key: Constituent.Key<T>): KorneaResult<T> =
        when (key) {
            SeekableFlow.Key -> KorneaResult.success(this as T)
            else -> KorneaResult.empty()
        }
}