package dev.brella.kornea.io.coroutine.flow

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.annotations.ChangedSince
import dev.brella.kornea.annotations.ExperimentalKorneaToolkit
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.Url
import dev.brella.kornea.io.common.flow.*
import dev.brella.kornea.toolkit.coroutines.ReadWriteSemaphore
import dev.brella.kornea.toolkit.coroutines.withWritePermit

@ExperimentalKorneaToolkit
@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_3_2_0_ALPHA)
@ChangedSince(KorneaIO.VERSION_5_0_0_ALPHA, "Implement IntFlowState")
public open class SynchronisedOutputFlow<O : OutputFlow>(
    override val output: O,
    protected val semaphore: ReadWriteSemaphore,
    protected val closeBacking: Boolean = true,
) : BaseDataCloseable(), OutputFlowByDelegate<O>, PrintOutputFlow, OutputFlowState, IntFlowState by IntFlowState.base() {
    protected suspend inline fun <T> access(crossinline block: suspend () -> T): T =
        semaphore.withWritePermit { block() }

    override suspend fun write(byte: Int): Unit = access { output.write(byte) }
    override suspend fun write(b: ByteArray): Unit = access { output.write(b) }
    override suspend fun write(b: ByteArray, off: Int, len: Int): Unit = access { output.write(b, off, len) }

    override suspend fun flush(): Unit = access { output.flush() }

    override suspend fun whenClosed() {
        super.whenClosed()

        if (closeBacking) access { output.close() }
    }

    override fun locationAsUrl(): KorneaResult<Url> = KorneaResult.empty()
}