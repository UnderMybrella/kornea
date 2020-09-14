package dev.brella.kornea.io.common.flow

import dev.brella.kornea.io.common.*
import dev.brella.kornea.toolkit.common.SuspendInit0
import dev.brella.kornea.toolkit.common.init

@ExperimentalUnsignedTypes
public open class SinkOffsetInputFlow private constructor(
    protected open val backing: InputFlow,
    override val baseOffset: ULong,
    override val location: String? =
        "${backing.location}+${baseOffset.toString(16)}h"
) : BaseDataCloseable(), OffsetInputFlow, SuspendInit0 {
    public companion object {
        public suspend operator fun invoke(
            backing: SeekableInputFlow,
            offset: ULong,
            location: String? = "${backing.location}+${offset.toString(16)}h"
        ): Seekable  = Seekable(backing, offset, location)

        public suspend operator fun invoke(
            backing: InputFlow,
            offset: ULong,
            location: String? = "${backing.location}+${offset.toString(16)}h"
        ): SinkOffsetInputFlow {
            if (backing is SeekableInputFlow) return Seekable(backing, offset, location)

            return init(SinkOffsetInputFlow(backing, offset, location))
        }
    }

    public class Seekable private constructor(
        override val backing: SeekableInputFlow,
        override val baseOffset: ULong,
        override val location: String? = "${backing.location}+${baseOffset.toString(16)}h"
    ) : SinkOffsetInputFlow(backing, baseOffset, location), SeekableInputFlow {
        public companion object {
            public suspend operator fun invoke(
                backing: SeekableInputFlow,
                offset: ULong,
                location: String? = "${backing.location}+${offset.toString(16)}h"
            ): Seekable = init(Seekable(backing, offset, location))
        }

        override suspend fun seek(pos: Long, mode: EnumSeekMode): ULong {
            when (mode) {
                EnumSeekMode.FROM_BEGINNING -> {
                    this.sinkOffset = pos.toULong()
                    backing.seek(baseOffset.toLong() + pos, mode)
                }
                EnumSeekMode.FROM_POSITION -> {
                    val n = this.sinkOffset.toLong() + pos
                    this.sinkOffset = n.toULong()
                    backing.seek(baseOffset.toLong() + n, EnumSeekMode.FROM_BEGINNING)
                }
                EnumSeekMode.FROM_END -> {
                    val size = size()
                    if (size == null) {
                        val result = backing.seek(pos, mode)
                        if (result < baseOffset) {
                            backing.skip(baseOffset - result)
                            this.sinkOffset = 0u
                        }
                    } else {
                        val n = (size.toLong() - pos)
                        this.sinkOffset = n.toULong()
                        backing.seek(baseOffset.toLong() + n, EnumSeekMode.FROM_BEGINNING)
                    }
                }
            }

            return position()
        }
    }

    protected var sinkOffset: ULong = 0uL

    override suspend fun read(): Int? {
        sinkOffset++
        return backing.read()
    }

    override suspend fun read(b: ByteArray, off: Int, len: Int): Int? {
        if (len < 0 || off < 0 || len > b.size - off)
            throw IndexOutOfBoundsException()

        val read = backing.read(b, off, len) ?: return null
        sinkOffset += read.toULong()
        return read
    }

    override suspend fun skip(n: ULong): ULong? {
        val skipped = backing.skip(n) ?: return null
        sinkOffset += skipped
        return skipped
    }

    override suspend fun available(): ULong? = backing.available()
    override suspend fun remaining(): ULong? = backing.remaining()
    override suspend fun size(): ULong? = backing.size()?.minus(baseOffset)
    override suspend fun position(): ULong = sinkOffset

    override suspend fun init() {
        backing.skip(baseOffset)
    }

    override suspend fun whenClosed() {
        super.whenClosed()

        backing.close()
    }

    override suspend fun globalOffset(): ULong = baseOffset + backing.globalOffset()
}