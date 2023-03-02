package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.ChangedSince
import dev.brella.kornea.composite.common.Constituent
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.errors.common.map
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.EnumSeekMode
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.Uri
import dev.brella.kornea.toolkit.common.SuspendInit0
import dev.brella.kornea.toolkit.common.init

@ChangedSince(KorneaIO.VERSION_5_0_0_ALPHA, "Implement IntFlowState")
public open class SinkOffsetInputFlow private constructor(
    protected open val backing: InputFlow,
    override val baseOffset: ULong,
    override val location: String? =
        "${backing.location}+${baseOffset.toString(16)}h"
) : BaseDataCloseable(),
    OffsetInputFlow, SuspendInit0, InputFlowState, IntFlowState by IntFlowState.base() {
    public companion object {
        @Deprecated(
            "Base SinkOffsetInputFlow handles seeking too",
            replaceWith = ReplaceWith("invoke(backing, offset, location)")
        )
        public suspend fun <T> seekable(
            backing: T,
            offset: ULong,
            location: String? = "${backing.location}+${offset.toString(16)}h"
        ): SinkOffsetInputFlow where T : InputFlow, T : SeekableFlow =
            invoke(backing, offset, location)

        public suspend operator fun invoke(
            backing: InputFlow,
            offset: ULong,
            location: String? = "${backing.location}+${offset.toString(16)}h"
        ): SinkOffsetInputFlow {
            return init(SinkOffsetInputFlow(backing, offset, location))
        }
    }

    public inner class SeekableConstituent(public val constituent: SeekableFlow) : SeekableFlow {
        override val flow: KorneaFlow
            get() = this@SinkOffsetInputFlow

        override suspend fun seek(pos: Long, mode: EnumSeekMode): ULong {
            when (mode) {
                EnumSeekMode.FROM_BEGINNING -> {
                    sinkOffset = pos.toULong()
                    constituent.seek(baseOffset.toLong() + pos, mode)
                }

                EnumSeekMode.FROM_POSITION -> {
                    val n = sinkOffset.toLong() + pos
                    sinkOffset = n.toULong()
                    constituent.seek(baseOffset.toLong() + n, EnumSeekMode.FROM_BEGINNING)
                }

                EnumSeekMode.FROM_END -> {
                    val size = size()
                    if (size == null) {
                        val result = constituent.seek(pos, mode)
                        if (result < baseOffset) {
                            backing.skip(baseOffset - result)
                            sinkOffset = 0u
                        }
                    } else {
                        val n = (size.toLong() - pos)
                        sinkOffset = n.toULong()
                        constituent.seek(baseOffset.toLong() + n, EnumSeekMode.FROM_BEGINNING)
                    }
                }
            }

            return position()
        }
    }

    public inner class PeekableConstituent(public val constituent: PeekableInputFlow) : PeekableInputFlow {
        override val flow: InputFlow
            get() = this@SinkOffsetInputFlow

        override suspend fun peek(forward: Int): Int? = constituent.peek(forward)
        override suspend fun peek(forward: Int, b: ByteArray, off: Int, len: Int): Int? =
            constituent.peek(forward, b, off, len)
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
    override suspend fun absPosition(): ULong = (backing as? KorneaFlowWithBacking)?.absPosition() ?: backing.position()

    override fun locationAsUri(): KorneaResult<Uri> = backing.locationAsUri()

    override fun hasConstituent(key: Constituent.Key<*>): Boolean =
        when (key) {
            SeekableFlow.Key,
            PeekableInputFlow.Key -> backing.hasConstituent(key)

            else -> false
        }

    override fun <T : Constituent> getConstituent(key: Constituent.Key<T>): KorneaResult<T> =
        when (key) {
            SeekableFlow.Key -> backing.getConstituent(SeekableFlow.Key)
                .map { SeekableConstituent(it) as T }

            PeekableInputFlow.Key -> backing.getConstituent(PeekableInputFlow.Key)
                .map { PeekableConstituent(it) as T }

            else -> KorneaResult.empty()
        }
}