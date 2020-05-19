package org.abimon.kornea.io.common.flow

import org.abimon.kornea.io.common.*

@ExperimentalUnsignedTypes
open class WindowedInputFlow private constructor(
    open val window: InputFlow,
    override val baseOffset: ULong,
    val windowSize: ULong,
    override val location: String?
) : OffsetInputFlow {
    companion object {
        suspend operator fun invoke(
            window: SeekableInputFlow,
            offset: ULong,
            windowSize: ULong,
            location: String? =
                "${window.location}[${offset.toString(16).toUpperCase()}h,${offset.plus(windowSize).toString(16)
                    .toUpperCase()}h]"
        ): WindowedInputFlow {
            val flow = Seekable(window, offset, windowSize, location)
            flow.initialSkip()
            return flow
        }
        suspend operator fun invoke(
            window: InputFlow,
            offset: ULong,
            windowSize: ULong,
            location: String? =
                "${window.location}[${offset.toString(16).toUpperCase()}h,${offset.plus(windowSize).toString(16)
                    .toUpperCase()}h]"
        ): WindowedInputFlow {
            val flow = WindowedInputFlow(window, offset, windowSize, location)
            flow.initialSkip()
            return flow
        }
    }

    class Seekable private constructor(override val window: SeekableInputFlow, baseOffset: ULong, windowSize: ULong, location: String?): WindowedInputFlow(window, baseOffset, windowSize, location), SeekableInputFlow {
        companion object {
            suspend operator fun invoke(
                window: SeekableInputFlow,
                offset: ULong,
                windowSize: ULong,
                location: String? =
                    "${window.location}[${offset.toString(16).toUpperCase()}h,${offset.plus(windowSize).toString(16)
                        .toUpperCase()}h]"
            ): WindowedInputFlow {
                val flow = Seekable(window, offset, windowSize, location)
                flow.initialSkip()
                return flow
            }
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

    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()
    protected var windowPosition: ULong = 0uL
    private var closed: Boolean = false
    override val isClosed: Boolean
        get() = closed

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

    suspend fun initialSkip() {
        window.skip(baseOffset)
    }

    override suspend fun close() {
        super.close()

        if (!closed) {
            window.close()
            closed = true
        }
    }
}