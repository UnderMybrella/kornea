package dev.brella.kornea.toolkit.common.pools

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.toolkit.common.KorneaToolkit
import dev.brella.kornea.toolkit.common.collections.KorneaWaiter
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.yield

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public class KorneaCachedPool<T>(
    public val maxPoolSize: Int,
    private val initialiser: () -> Poolable<T>,
    private val mutex: Mutex,
    private val waiter: KorneaWaiter<Poolable<T>> = KorneaWaiter()
) : KorneaPool<T> {
    private val pool: MutableList<Poolable<T>> = ArrayList()
    private var poolSize: Int = 0

    override suspend fun hire(): KorneaResult<Poolable<T>> {
        mutex.withLock {
            if (pool.isNotEmpty()) return KorneaResult.success(pool.removeFirst())
            if (poolSize < maxPoolSize) {
                poolSize++
                return KorneaResult.success(initialiser())
            }
        }

        return KorneaResult.success(waiter.untilAvailable().obtainFrom(this))
    }

    override suspend fun retire(obj: Poolable<T>) {
        if (waiter.isAvailable()) {
            mutex.withLock { pool.add(obj.returnTo(this)) }
        } else {
            waiter.becomeAvailable(obj.returnTo(this))
        }
    }
}