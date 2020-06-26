package org.abimon.kornea.io.common.flow

import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.abimon.kornea.annotations.AvailableSince
import org.abimon.kornea.annotations.ExperimentalKorneaToolkit
import org.abimon.kornea.io.common.BaseDataCloseable
import org.abimon.kornea.io.common.DataCloseableEventHandler
import org.kornea.toolkit.common.KorneaToolkit
import org.kornea.toolkit.common.SharedStateRW
import org.kornea.toolkit.common.asImmutableView

@ExperimentalKorneaToolkit
@ExperimentalUnsignedTypes
@AvailableSince(KorneaToolkit.VERSION_1_2_0)
public open class MultiViewOutputFlow<O : OutputFlow> protected constructor(
    private val backing: O,
    protected val mutex: Mutex = Mutex(),
    protected val instanceCount: SharedStateRW<Int>
) : BaseDataCloseable(), OutputFlow, PrintOutputFlow {
    public companion object {
        public suspend operator fun <O : OutputFlow> invoke(
            backing: O,
            mutex: Mutex = Mutex(),
            instanceCount: SharedStateRW<Int>
        ): MultiViewOutputFlow<O> {
            val flow = MultiViewOutputFlow(backing, mutex, instanceCount)
            instanceCount.mutateState { it + 1 }
            return flow
        }
    }

    protected suspend inline fun <T> access(crossinline block: suspend () -> T): T =
        mutex.withLock { block() }

    override suspend fun write(byte: Int): Unit = access { backing.write(byte) }
    override suspend fun write(b: ByteArray): Unit = access { backing.write(b) }
    override suspend fun write(b: ByteArray, off: Int, len: Int): Unit = access { backing.write(b, off, len) }

    override suspend fun flush(): Unit = access { backing.flush() }

    override suspend fun whenClosed() {
        super.whenClosed()

        access {
            if (instanceCount.mutateState { it - 1 }.accessState { it == 0 })
                backing.close()
        }
    }
}