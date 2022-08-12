package dev.brella.kornea.io.common.native.flow

import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.EnumSeekMode
import dev.brella.kornea.io.common.flow.BinaryPipeFlow
import dev.brella.kornea.toolkit.common.asInt
import kotlin.math.min

public open class NativeListBackedBinaryPipeFlow(
    private val view: MutableList<Byte>,
    private var pos: Int = 0,
    override val location: String? = null
) : BaseDataCloseable(), BinaryPipeFlow {
    public constructor() : this(ArrayList())

    override val input: BinaryPipeFlow
        get() = this

    override val output: BinaryPipeFlow
        get() = this

    override suspend fun peek(forward: Int): Int? =
        when {
            pos < 0 -> null
            (pos + forward - 1) < view.size -> view[pos + forward - 1].asInt()
            else -> null
        }

    override suspend fun peek(forward: Int, b: ByteArray, off: Int, len: Int): Int? {
        if (pos < 0) {
            return null
        } else if (pos + forward - 1 < view.size) {
            val peeking = min(len, view.size - (pos + forward - 1))

//                view.subList(pos + forward - 1, pos + forward - 1 + peeking)
//                    .forEachIndexed { index, byte -> b[off + index] = byte }

            for (i in 0 until peeking) {
                b[off + i] = view[pos + forward - 1]
            }

            return peeking
        }

        return null
    }

    override suspend fun read(): Int? =
        when {
            pos < 0 -> null //view.last().asInt()
            pos < view.size -> view[pos++].asInt()
            else -> null
        }

    override suspend fun read(b: ByteArray): Int? = read(b, 0, b.size)
    override suspend fun read(b: ByteArray, off: Int, len: Int): Int? {
        if (len < 0 || off < 0 || len > b.size - off)
            throw IndexOutOfBoundsException()

        if (pos < 0 || pos >= view.size)
            return null

        val avail = view.size - pos

        @Suppress("NAME_SHADOWING")
        val len: Int = if (len > avail) avail else len
        if (len <= 0)
            return 0

//            view.subList(pos, pos + len).forEachIndexed { index, byte -> b[off + index] = byte }
        for (i in 0 until len) {
            b[off + i] = view[pos + i]
        }

        pos += len
        return len
    }

    override suspend fun skip(n: ULong): ULong? {
        val k = min((view.size - pos).toULong(), n)
        pos += k.toInt()
        return k
    }

    override suspend fun available(): ULong = remaining()
    override suspend fun remaining(): ULong = (view.size - pos).toULong()
    override suspend fun size(): ULong = view.size.toULong()
    override suspend fun position(): ULong = pos.toULong()

    override suspend fun seek(pos: Long, mode: EnumSeekMode): ULong {
//            when (mode) {
//                EnumSeekMode.FROM_BEGINNING -> this.pos = pos.toInt()
//                EnumSeekMode.FROM_POSITION -> this.pos += pos.toInt()
//                EnumSeekMode.FROM_END -> this.pos = view.size - pos.toInt()
//            }

        val seekPos = when (mode) {
            EnumSeekMode.FROM_BEGINNING -> pos.toInt()
            EnumSeekMode.FROM_END -> view.size - pos.toInt() - 1
            EnumSeekMode.FROM_POSITION -> this.pos + pos.toInt()
        }

        if (seekPos > view.size) {
            view.addAll(ByteArray(pos.toInt() - view.size).asList())
            this.pos = -1
        } else {
            this.pos = pos.toInt().coerceAtLeast(0)
        }

        return position()
    }

    override suspend fun write(byte: Int) {
//            view.add(pos++, byte.toByte())

        if (pos < 0) {
            view.add(byte.toByte())
        } else if (pos < view.size) {
            view[pos++] = byte.toByte()
        } else {
            pos = -1
            view.add(byte.toByte())
        }
    }

    override suspend fun write(b: ByteArray): Unit = write(b, 0, b.size)
    override suspend fun write(b: ByteArray, off: Int, len: Int) {
//            view.addAll(pos, b.slice(off until off + len))
//            pos += len.coerceAtMost(b.size - off)

        if (pos < 0) {
            view.addAll(b.slice(off until off + len))
        } else {
            val space = view.size - pos
            for (i in off until off + min(space, len)) {
                view[pos++] = b[off + i]
            }

            if (space < len) {
                view.addAll(b.slice((off + space) until (off + len)))
                pos = -1
            }
        }
    }

    override suspend fun flush() {}

    override suspend fun getData(): ByteArray = view.toByteArray()
    override suspend fun getDataSize(): ULong = view.size.toULong()
}