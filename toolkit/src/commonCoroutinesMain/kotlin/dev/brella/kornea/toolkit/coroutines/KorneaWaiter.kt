package dev.brella.kornea.toolkit.coroutines

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.toolkit.common.KorneaToolkit
import dev.brella.kornea.toolkit.common.collections.MutableChainNode
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public interface KorneaWaiter<T> {
    public suspend fun untilAvailable(): T
    public suspend fun becomeAvailable(resumeWith: T)
    public suspend fun isAvailable(): Boolean
}

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public fun <T> KorneaWaiter(): KorneaWaiter<T> = KorneaWaiterImpl()
@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public suspend inline fun KorneaWaiter<Unit>.becomeAvailable(): Unit = becomeAvailable(Unit)

private class KorneaWaiterImpl<T>: KorneaWaiter<T> {
//    private val head: AtomicRef<MutableChainNode.Base<CancellableContinuation<T>>?> = atomic(null)
//    private val tail: AtomicRef<MutableChainNode.Base<CancellableContinuation<T>>?> = atomic(null)
    private val internal: Mutex = Mutex()
    private var head: MutableChainNode.Base<CancellableContinuation<T>>? = null
    private var tail: MutableChainNode.Base<CancellableContinuation<T>>? = null

    override suspend fun isAvailable(): Boolean = internal.withLock { head == null }

    public override suspend fun untilAvailable(): T {
        internal.lock()

        return suspendCancellableCoroutine { cont ->
            if (tail == null) {
                val base = MutableChainNode.Base(cont, null)
                head = base
                tail = base
            } else {
                tail = MutableChainNode.Base(cont, tail)
            }

            internal.unlock()
        }
    }

    public override suspend fun becomeAvailable(resumeWith: T) {
        internal.withLock {
            if (head != null) {
                head!!.node.resume(resumeWith)
                head = head!!.next
            } else {
                head = null
            }
        }
    }
}