package org.abimon.kornea.io.jvm

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.abimon.kornea.io.common.DataCloseableEventHandler
import org.abimon.kornea.io.common.flow.InputFlow
import org.abimon.kornea.io.common.flow.readResultIsValid
import java.io.InputStream

@ExperimentalUnsignedTypes
open class JVMInputFlow private constructor(val stream: CountingInputStream, override val location: String? = null): InputFlow {
    constructor(stream: InputStream, location: String?): this(CountingInputStream(stream), location)

    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()

    private var closed: Boolean = false
    override val isClosed: Boolean
        get() = closed

    override suspend fun read(): Int? = withContext(Dispatchers.IO) { stream.read().takeIf(::readResultIsValid) }
    override suspend fun read(b: ByteArray): Int? = withContext(Dispatchers.IO) { stream.read(b).takeIf(::readResultIsValid) }
    override suspend fun read(b: ByteArray, off: Int, len: Int): Int? = withContext(Dispatchers.IO) { stream.read(b, off, len).takeIf(::readResultIsValid) }
    override suspend fun skip(n: ULong): ULong? = withContext(Dispatchers.IO) { stream.skip(n.toLong()).toULong() }
    override suspend fun available(): ULong? = withContext(Dispatchers.IO) { stream.available().toULong() }
    override suspend fun remaining(): ULong? = null
    override suspend fun size(): ULong? = null
    override suspend fun close() {
        super.close()

        if (!closed) {
            stream.close()
            closed = true
        }
    }

    override suspend fun position(): ULong = stream.count.toULong()
    override suspend fun seek(pos: Long, mode: Int): ULong? {
        when (mode) {
            InputFlow.FROM_BEGINNING -> {
                if (stream.markSupported()) {
                    withContext(Dispatchers.IO) { stream.reset() }
                    stream.mark(Int.MAX_VALUE)
                    skip(pos.toULong())
                    return position()
                } else if (pos >= stream.count) {
                    skip(pos.toULong())
                    return position()
                } else {
                    return null
                }
            }
            InputFlow.FROM_END -> return null
            InputFlow.FROM_POSITION -> {
                if (pos > 0) {
                    skip(pos.toULong())
                    return position()
                } else {
                    val currentPosition = position()
                    return seek(currentPosition.toLong() + pos, InputFlow.FROM_BEGINNING)
                }
            }
            else -> return null
        }
    }

    init {
        if (stream.markSupported()) {
            stream.mark(Int.MAX_VALUE)
        }
    }
}