package dev.brella.kornea.io.coroutine.flow

import dev.brella.kornea.annotations.ExperimentalKorneaToolkit
import dev.brella.kornea.composite.common.Composite
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.EnumSeekMode
import dev.brella.kornea.io.common.flow.InputFlow
import dev.brella.kornea.toolkit.common.asInt
import dev.brella.kornea.toolkit.coroutines.ReadWriteSemaphore
import dev.brella.kornea.toolkit.coroutines.withWritePermit
import kotlin.math.min

@OptIn(ExperimentalKorneaToolkit::class)
@Deprecated("Deprecating PipeFlow until further notice", level = DeprecationLevel.ERROR)
@Suppress("DEPRECATION_ERROR")
public open class MutexListBackedBinaryPipeFlow(
    private val view: MutableList<Byte>,
    private var pos: Int = 0,
    override val location: String? = null,
    protected val semaphore: ReadWriteSemaphore = ReadWriteSemaphore(8),
) : BaseDataCloseable(), dev.brella.kornea.io.common.flow.BinaryPipeFlow, Composite.Empty {
    public constructor() : this(ArrayList())

    override val flow: InputFlow
        get() = this

    override val input: dev.brella.kornea.io.common.flow.BinaryPipeFlow
        get() = this

    override val output: dev.brella.kornea.io.common.flow.BinaryPipeFlow
        get() = this

    protected suspend inline fun <T> doRead(crossinline block: suspend () -> T): T =
        semaphore.withWritePermit { block() }

    protected suspend inline fun <T> doWrite(crossinline block: suspend () -> T): T =
        semaphore.withWritePermit { block() }

    override suspend fun peek(forward: Int): Int? =
        doWrite {
            when {
                pos < 0 -> null
                (pos + forward - 1) < view.size -> view[pos + forward - 1].asInt()
                else -> null
            }
        }

    override suspend fun peek(forward: Int, b: ByteArray, off: Int, len: Int): Int? =
        doWrite {
            if (pos < 0) {
                return@doWrite null
            } else if (pos + forward - 1 < view.size) {
                val peeking = min(len, view.size - (pos + forward - 1))

//                view.subList(pos + forward - 1, pos + forward - 1 + peeking)
//                    .forEachIndexed { index, byte -> b[off + index] = byte }

                for (i in 0 until peeking) {
                    b[off + i] = view[pos + forward - 1]
                }

                return@doWrite peeking
            }

            return@doWrite null
        }

    override suspend fun read(): Int? =
        doWrite {
            when {
                pos < 0 -> null //view.last().asInt()
                pos < view.size -> view[pos++].asInt()
                else -> null
            }
        }

    override suspend fun read(b: ByteArray): Int? = read(b, 0, b.size)
    override suspend fun read(b: ByteArray, off: Int, len: Int): Int? =
        doWrite {
            if (len < 0 || off < 0 || len > b.size - off)
                throw IndexOutOfBoundsException()

            if (pos < 0 || pos >= view.size)
                return@doWrite null

            val avail = view.size - pos

            @Suppress("NAME_SHADOWING")
            val len: Int = if (len > avail) avail else len
            if (len <= 0)
                return@doWrite 0

//            view.subList(pos, pos + len).forEachIndexed { index, byte -> b[off + index] = byte }
            for (i in 0 until len) {
                b[off + i] = view[pos + i]
            }

            pos += len
            return@doWrite len
        }

    override suspend fun skip(n: ULong): ULong? =
        doWrite {
            val k = min((view.size - pos).toULong(), n)
            pos += k.toInt()
            return@doWrite k
        }

    override suspend fun available(): ULong = remaining()
    override suspend fun remaining(): ULong = doRead { (view.size - pos).toULong() }
    override suspend fun size(): ULong = doRead { view.size.toULong() }
    override suspend fun position(): ULong = doRead { pos.toULong() }

    override suspend fun seek(pos: Long, mode: EnumSeekMode): ULong {
//            when (mode) {
//                EnumSeekMode.FROM_BEGINNING -> this.pos = pos.toInt()
//                EnumSeekMode.FROM_POSITION -> this.pos += pos.toInt()
//                EnumSeekMode.FROM_END -> this.pos = view.size - pos.toInt()
//            }

        doWrite {
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
        }

        return position()
    }

    override suspend fun write(byte: Int) {
        doWrite {
            if (pos < 0) {
                view.add(byte.toByte())
            } else if (pos < view.size) {
                view[pos++] = byte.toByte()
            } else {
                pos = -1
                view.add(byte.toByte())
            }
        }
    }

    override suspend fun write(b: ByteArray): Unit = write(b, 0, b.size)
    override suspend fun write(b: ByteArray, off: Int, len: Int) {
        doWrite {
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
                } else {
                }
            }
        }
    }

    override suspend fun flush() {}

    override suspend fun getData(): ByteArray = doRead { view.toByteArray() }
    override suspend fun getDataSize(): ULong = doRead { view.size.toULong() }
}