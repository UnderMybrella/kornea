package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.ChangedSince
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.EnumSeekMode
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.Uri
import dev.brella.kornea.toolkit.common.SuspendInit0
import dev.brella.kornea.toolkit.common.init

@ChangedSince(KorneaIO.VERSION_5_0_0_ALPHA, "Implement IntFlowState")
public open class WindowedInputFlow private constructor(
    protected open val window: InputFlow,
    override val baseOffset: ULong,
    public val windowSize: ULong,
    override val location: String?
) : BaseDataCloseable(), OffsetInputFlow, SuspendInit0, InputFlowState, IntFlowState by IntFlowState.base() {
    public companion object {
        public suspend fun <T> seekable(
            window: T,
            offset: ULong,
            windowSize: ULong,
            location: String? =
                "${window.location}[${offset.toString(16).uppercase()}h,${
                    offset.plus(windowSize).toString(16).uppercase()
                }h]"
        ): Seekable<T> where T : InputFlow, T : SeekableFlow =
            Seekable<T>(window, offset, windowSize, location)

        public suspend operator fun invoke(
            window: InputFlow,
            offset: ULong,
            windowSize: ULong,
            location: String? =
                "${window.location}[${offset.toString(16).uppercase()}h,${
                    offset.plus(windowSize).toString(16)
                        .uppercase()
                }h]"
        ): WindowedInputFlow {
            if (window is SeekableFlow)
                return Seekable(window, offset, windowSize, location)

            return init(WindowedInputFlow(window, offset, windowSize, location))
        }
    }

    public open class Seekable<T> private constructor(
        override val window: T,
        baseOffset: ULong,
        windowSize: ULong,
        location: String?
    ) : WindowedInputFlow(window, baseOffset, windowSize, location),
        SeekableFlow where T : InputFlow, T : SeekableFlow {
        public companion object {
            public suspend operator fun <T> invoke(
                window: T,
                offset: ULong,
                windowSize: ULong,
                location: String? =
                    "${window.location}[${offset.toString(16).uppercase()}h,${
                        offset.plus(windowSize).toString(16)
                            .uppercase()
                    }h]"
            ): Seekable<T> where T : InputFlow, T : SeekableFlow = init(Seekable(window, offset, windowSize, location))
        }

        override suspend fun seek(pos: Long, mode: EnumSeekMode): ULong {
            when (mode) {
                EnumSeekMode.FROM_BEGINNING -> {
                    val n = pos.coerceIn(0 until windowSize.toLong())
                    this.windowPosition = n.toULong()
                    window.seek(baseOffset.toLong() + n, mode)
                }
                EnumSeekMode.FROM_POSITION -> {
                    val n = (this.windowPosition.toLong() + pos).coerceIn(0 until windowSize.toLong())
                    this.windowPosition = n.toULong()
                    window.seek(baseOffset.toLong() + n, EnumSeekMode.FROM_BEGINNING)
                }
                EnumSeekMode.FROM_END -> {
                    val n = (this.windowSize.toLong() - pos).coerceIn(0 until windowSize.toLong())
                    this.windowPosition = n.toULong()
                    window.seek(baseOffset.toLong() + n, EnumSeekMode.FROM_BEGINNING)
                }
            }

            return position()
        }
    }

    protected var windowPosition: ULong = 0uL

    override suspend fun read(): Int? = if (windowPosition < windowSize) {
        windowPosition++
        window.read()
    } else {
        null
    }

    override suspend fun read(b: ByteArray, off: Int, len: Int): Int? {
        if (len < 0 || off < 0 || len > b.size - off)
            throw IndexOutOfBoundsException()

        val avail = minOf((windowSize - windowPosition).toInt(), len)

        if (avail <= 0)
            return null

        window.read(b, off, avail)
        windowPosition += avail.toULong()
        return avail
    }

    override suspend fun skip(n: ULong): ULong? {
        val avail = minOf(windowSize - windowPosition, n)
        if (avail <= 0u)
            return null

        window.skip(avail)
        windowPosition += avail
        return avail
    }

    override suspend fun available(): ULong? {
        val avail = minOf(windowSize - windowPosition, window.available() ?: return null)
        if (avail <= 0u)
            return null

        return avail
    }

    override suspend fun remaining(): ULong = windowSize - windowPosition
    override suspend fun size(): ULong = windowSize
    override suspend fun position(): ULong = windowPosition

    override suspend fun init() {
        window.skip(baseOffset)
    }

    override suspend fun whenClosed() {
        super.whenClosed()

        window.close()
    }

    override suspend fun globalOffset(): ULong = baseOffset + window.globalOffset()
    override suspend fun absPosition(): ULong = (window as? KorneaFlowWithBacking)?.absPosition() ?: window.position()

    override fun locationAsUri(): KorneaResult<Uri> = window.locationAsUri()
}