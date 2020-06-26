package dev.brella.kornea.io.jvm

import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.flow.InputFlow
import dev.brella.kornea.io.common.flow.readResultIsValid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import java.io.InputStream

@ExperimentalUnsignedTypes
public open class JVMInputFlow private constructor(protected val stream: CountingInputStream, override val location: String? = null): BaseDataCloseable(), InputFlow {
    public constructor(stream: InputStream, location: String?): this(
        CountingInputStream(
            stream
        ), location)

    override suspend fun read(): Int? = runInterruptible(Dispatchers.IO) { stream.read().takeIf(::readResultIsValid) }
    override suspend fun read(b: ByteArray): Int? = runInterruptible(Dispatchers.IO) { stream.read(b).takeIf(::readResultIsValid) }
    override suspend fun read(b: ByteArray, off: Int, len: Int): Int? = runInterruptible(Dispatchers.IO) { stream.read(b, off, len).takeIf(::readResultIsValid) }
    override suspend fun skip(n: ULong): ULong? = runInterruptible(Dispatchers.IO) { stream.skip(n.toLong()).toULong() }
    override suspend fun available(): ULong? = runInterruptible(Dispatchers.IO) { stream.available().toULong() }
    override suspend fun remaining(): ULong? = null
    override suspend fun size(): ULong? = null

    override suspend fun whenClosed() {
        super.whenClosed()

        runInterruptible(Dispatchers.IO) { stream.close() }
    }

    override suspend fun position(): ULong = stream.count.toULong()

    init {
        if (stream.markSupported()) {
            stream.mark(Int.MAX_VALUE)
        }
    }
}