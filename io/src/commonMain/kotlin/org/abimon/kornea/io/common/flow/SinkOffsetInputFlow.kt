package org.abimon.kornea.io.common.flow

import org.abimon.kornea.io.common.*

@ExperimentalUnsignedTypes
open class SinkOffsetInputFlow private constructor(
    open val backing: InputFlow, override val baseOffset: ULong,
    override val location: String? =
        "${backing.location}+${baseOffset.toString(16)}h"
) : OffsetInputFlow {
    companion object {
        suspend operator fun invoke(backing: SeekableInputFlow, offset: ULong, location: String? = "${backing.location}+${offset.toString(16)}h"): Seekable {
            val flow = Seekable(backing, offset, location)
            flow.initialSkip()
            return flow
        }
        suspend operator fun invoke(backing: InputFlow, offset: ULong, location: String? = "${backing.location}+${offset.toString(16)}h"): SinkOffsetInputFlow {
            val flow = SinkOffsetInputFlow(backing, offset, location)
            flow.initialSkip()
            return flow
        }
    }

    class Seekable(override val backing: SeekableInputFlow, override val baseOffset: ULong, override val location: String? = "${backing.location}+${baseOffset.toString(16)}h"): SinkOffsetInputFlow(backing, baseOffset, location), SeekableInputFlow {
        companion object {
            suspend operator fun invoke(backing: SeekableInputFlow, offset: ULong, location: String? = "${backing.location}+${offset.toString(16)}h"): Seekable {
                val flow = Seekable(backing, offset, location)
                flow.initialSkip()
                return flow
            }
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

    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()
    protected var sinkOffset: ULong = 0uL
    protected var closed: Boolean = false
    override val isClosed: Boolean
        get() = closed

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

    suspend fun initialSkip() {
        backing.skip(baseOffset)
    }

    override suspend fun close() {
        super.close()

        if (!closed) {
            backing.close()
            closed = true
        }
    }
}