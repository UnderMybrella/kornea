package dev.brella.kornea.io.jvm

import dev.brella.kornea.annotations.ChangedSince
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.Uri
import dev.brella.kornea.io.common.flow.InputFlow
import dev.brella.kornea.io.common.flow.InputFlowState
import dev.brella.kornea.io.common.flow.IntFlowState
import dev.brella.kornea.io.common.flow.readResultIsValid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import java.io.InputStream

@ChangedSince(KorneaIO.VERSION_5_0_0_ALPHA, "Implement IntFlowState")
public open class JVMInputFlow private constructor(
    protected val stream: CountingInputStream,
    override val location: String? = null
) : BaseDataCloseable(), InputFlow, InputFlowState, IntFlowState by IntFlowState.base() {
    public constructor(stream: InputStream, location: String?) : this(CountingInputStream(stream), location)

    override suspend fun read(): Int? =
        runInterruptible(Dispatchers.IO) { stream.read().takeIf(::readResultIsValid) }

    override suspend fun read(b: ByteArray): Int? =
        runInterruptible(Dispatchers.IO) { stream.read(b).takeIf(::readResultIsValid) }

    override suspend fun read(b: ByteArray, off: Int, len: Int): Int? =
        runInterruptible(Dispatchers.IO) { stream.read(b, off, len).takeIf(::readResultIsValid) }

    override suspend fun skip(n: ULong): ULong? = runInterruptible(Dispatchers.IO) { stream.skip(n.toLong()).toULong() }
    override suspend fun available(): ULong? = runInterruptible(Dispatchers.IO) { stream.available().toULong() }
    override suspend fun remaining(): ULong? = null
    override suspend fun size(): ULong? = null

    override suspend fun whenClosed() {
        super.whenClosed()

        runInterruptible(Dispatchers.IO) { stream.close() }
    }

    override suspend fun position(): ULong = stream.count.toULong()
    override fun locationAsUri(): KorneaResult<Uri> = KorneaResult.empty()

    init {
        if (stream.markSupported()) {
            stream.mark(Int.MAX_VALUE)
        }
    }
}