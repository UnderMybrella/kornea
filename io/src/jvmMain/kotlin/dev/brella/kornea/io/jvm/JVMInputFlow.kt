package dev.brella.kornea.io.jvm

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import dev.brella.kornea.io.common.*
import dev.brella.kornea.io.common.flow.InputFlow
import dev.brella.kornea.io.common.flow.readResultIsValid
import java.io.InputStream

@ExperimentalUnsignedTypes
public open class JVMInputFlow private constructor(protected val stream: CountingInputStream, override val location: String? = null): BaseDataCloseable(), InputFlow {
    public constructor(stream: InputStream, location: String?): this(
        CountingInputStream(
            stream
        ), location)

    override suspend fun read(): Int? = withContext(Dispatchers.IO) { stream.read().takeIf(::readResultIsValid) }
    override suspend fun read(b: ByteArray): Int? = withContext(Dispatchers.IO) { stream.read(b).takeIf(::readResultIsValid) }
    override suspend fun read(b: ByteArray, off: Int, len: Int): Int? = withContext(Dispatchers.IO) { stream.read(b, off, len).takeIf(::readResultIsValid) }
    override suspend fun skip(n: ULong): ULong? = withContext(Dispatchers.IO) { stream.skip(n.toLong()).toULong() }
    override suspend fun available(): ULong? = withContext(Dispatchers.IO) { stream.available().toULong() }
    override suspend fun remaining(): ULong? = null
    override suspend fun size(): ULong? = null

    override suspend fun whenClosed() {
        super.whenClosed()

        withContext(Dispatchers.IO) { stream.close() }
    }

    override suspend fun position(): ULong = stream.count.toULong()
//    override suspend fun seek(pos: Long, mode: Int): ULong? {
//        when (mode) {
//            EnumSeekMode.FROM_BEGINNING -> {
//                if (stream.markSupported()) {
//                    withContext(Dispatchers.IO) { stream.reset() }
//                    stream.mark(Int.MAX_VALUE)
//                    skip(pos.toULong())
//                    return position()
//                } else if (pos >= stream.count) {
//                    skip(pos.toULong())
//                    return position()
//                } else {
//                    return null
//                }
//            }
//            EnumSeekMode.FROM_END -> return null
//            EnumSeekMode.FROM_POSITION -> {
//                if (pos > 0) {
//                    skip(pos.toULong())
//                    return position()
//                } else {
//                    val currentPosition = position()
//                    return seek(currentPosition.toLong() + pos, EnumSeekMode.FROM_BEGINNING)
//                }
//            }
//            else -> return null
//        }
//    }

    init {
        if (stream.markSupported()) {
            stream.mark(Int.MAX_VALUE)
        }
    }
}