package dev.brella.kornea.io.coroutine.flow

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.annotations.ChangedSince
import dev.brella.kornea.annotations.ExperimentalKorneaToolkit
import dev.brella.kornea.composite.common.Constituent
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.EnumSeekMode
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.Uri
import dev.brella.kornea.io.common.flow.*
import dev.brella.kornea.toolkit.coroutines.ReadWriteSemaphore
import dev.brella.kornea.toolkit.coroutines.withReadPermit
import dev.brella.kornea.toolkit.coroutines.withWritePermit

@ExperimentalKorneaToolkit
@AvailableSince(KorneaIO.VERSION_3_2_0_ALPHA)
@ChangedSince(KorneaIO.VERSION_5_0_0_ALPHA, "Implement IntFlowState")
public open class SynchronisedOutputFlow<O : OutputFlow>(
    override val output: O,
    protected val semaphore: ReadWriteSemaphore,
    protected val closeBacking: Boolean = true,
    override val location: String? = "SynchronisedOutputFlow(${output.location})"
) : BaseDataCloseable(), OutputFlowByDelegate<O>, OutputFlowState, IntFlowState by IntFlowState.base() {
    public inner class SeekableConstituent(public val constituent: SeekableFlow) : SeekableFlow {
        override val flow: KorneaFlow
            get() = this@SynchronisedOutputFlow

        override suspend fun seek(pos: Long, mode: EnumSeekMode): ULong =
            access { constituent.seek(pos, mode) }
    }

    protected suspend inline fun <T> access(crossinline block: suspend () -> T): T =
        semaphore.withWritePermit { block() }

    override suspend fun position(): ULong =
        semaphore.withReadPermit { output.position() }

    override suspend fun write(byte: Int): Unit = access { output.write(byte) }
    override suspend fun write(b: ByteArray): Unit = access { output.write(b) }
    override suspend fun write(b: ByteArray, off: Int, len: Int): Unit = access { output.write(b, off, len) }

    override suspend fun flush(): Unit = access { output.flush() }

    override suspend fun whenClosed() {
        super.whenClosed()

        if (closeBacking) access { output.close() }
    }

    override fun locationAsUri(): KorneaResult<Uri> =
        output.locationAsUri()

    override fun hasConstituent(key: Constituent.Key<*>): Boolean =
        when (key) {
            SeekableFlow.Key -> output.isSeekable()
            else -> false
        }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Constituent> getConstituent(key: Constituent.Key<T>): KorneaResult<T> =
        when (key) {
            SeekableFlow.Key -> output.seekable { SeekableConstituent(this) as T }

            else -> KorneaResult.empty()
        }
}