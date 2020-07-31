package dev.brella.kornea.toolkit.common.pools

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.toolkit.common.KorneaToolkit
import dev.brella.kornea.toolkit.common.UNINITIALIZED_VALUE
import dev.brella.kornea.toolkit.common.collections.KorneaWaiter
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public class KorneaFixedPool<T>(
    public val poolSize: Int,
    private val initialiser: (index: Int) -> Poolable<T>,
    private val mutex: Mutex,
    private val waiter: KorneaWaiter<Poolable<T>> = KorneaWaiter()
) : KorneaPool<T> {
    private val pool: Array<Any?> = Array(poolSize) { UNINITIALIZED_VALUE }

    override suspend fun hire(): KorneaResult<Poolable<T>> {
        mutex.withLock {
            pool.forEachIndexed { index, element ->
                if (element === UNINITIALIZED_VALUE) {
                    val current = initialiser(index)
                    pool[index] = null
                    return KorneaResult.success(current.obtainFrom(this))
                } else if (element != null) {
                    pool[index] = null
                    @Suppress("UNCHECKED_CAST")
                    return KorneaResult.success((element as Poolable<T>).obtainFrom(this))
                }
            }
        }

        return KorneaResult.success(waiter.untilAvailable().obtainFrom(this))
    }

    override suspend fun retire(obj: Poolable<T>) {
        mutex.withLock {
            obj.whenReturned(this)
            pool[pool.indexOf(null)] = obj
        }
    }
}