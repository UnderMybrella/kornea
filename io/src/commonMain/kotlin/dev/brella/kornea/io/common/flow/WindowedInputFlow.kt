package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.ChangedSince
import dev.brella.kornea.composite.common.Constituent
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.errors.common.asType
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
        @Deprecated(
            "Base flow handles constituents now",
            replaceWith = ReplaceWith("invoke(window, offset, windowSize, location)")
        )
        public suspend fun <T> seekable(
            window: T,
            offset: ULong,
            windowSize: ULong,
            location: String? =
                "${window.location}[${offset.toString(16).uppercase()}h,${
                    offset.plus(windowSize).toString(16).uppercase()
                }h]"
        ): WindowedInputFlow where T : InputFlow, T : SeekableFlow =
            invoke(window, offset, windowSize, location)

        public suspend operator fun invoke(
            window: InputFlow,
            offset: ULong,
            windowSize: ULong,
            location: String? =
                "${window.location}[${offset.toString(16).uppercase()}h,${
                    offset.plus(windowSize).toString(16)
                        .uppercase()
                }h]"
        ): WindowedInputFlow =
            init(WindowedInputFlow(window, offset, windowSize, location))
    }

    public inner class SeekableConstituent(public val constituent: SeekableFlow) : SeekableFlow {
        override val flow: KorneaFlow
            get() = this@WindowedInputFlow

        override suspend fun seek(pos: Long, mode: EnumSeekMode): ULong {
            when (mode) {
                EnumSeekMode.FROM_BEGINNING -> {
                    val n = pos.coerceIn(0 until windowSize.toLong())
                    windowPosition = n.toULong()
                    constituent.seek(baseOffset.toLong() + n, mode)
                }

                EnumSeekMode.FROM_POSITION -> {
                    val n = (windowPosition.toLong() + pos).coerceIn(0 until windowSize.toLong())
                    windowPosition = n.toULong()
                    constituent.seek(baseOffset.toLong() + n, EnumSeekMode.FROM_BEGINNING)
                }

                EnumSeekMode.FROM_END -> {
                    val n = (windowSize.toLong() - pos).coerceIn(0 until windowSize.toLong())
                    windowPosition = n.toULong()
                    constituent.seek(baseOffset.toLong() + n, EnumSeekMode.FROM_BEGINNING)
                }
            }

            return position()
        }
    }

    public inner class PeekableConstituent(public val constituent: PeekableInputFlow) : PeekableInputFlow {
        override val flow: InputFlow
            get() = this@WindowedInputFlow

        override suspend fun peek(forward: Int): Int? =
            if (windowPosition + forward.toUInt() < windowSize) constituent.peek(forward)
            else null

        override suspend fun peek(forward: Int, b: ByteArray, off: Int, len: Int): Int? {
            if (len < 0 || off < 0 || len > b.size - off)
                throw IndexOutOfBoundsException()

            val avail = minOf((windowSize - (windowPosition + forward.toUInt())).toInt(), len)

            if (avail <= 0)
                return null

            return constituent.peek(b, off, avail)
        }
    }

    private val seekableConstituent by lazy { window.seekable { SeekableConstituent(this) } }
    private val peekableConstituent by lazy { window.peekableInputFlow { PeekableConstituent(this) } }

    public var windowPosition: ULong = 0uL
        protected set

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

        val read = window.read(b, off, avail) ?: return null
        windowPosition += read.toULong()
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

    //Composite
    override fun hasConstituent(key: Constituent.Key<*>): Boolean =
        when (key) {
            SeekableFlow.Key,
            PeekableInputFlow.Key -> window.hasConstituent(key)

            else -> false
        }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Constituent> getConstituent(key: Constituent.Key<T>): KorneaResult<T> =
        when (key) {
            SeekableFlow.Key -> seekableConstituent.asType()
            PeekableInputFlow.Key -> peekableConstituent.asType()

            else -> KorneaResult.empty()
        }
}