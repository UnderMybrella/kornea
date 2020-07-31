package dev.brella.kornea.toolkit.common.pools

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.errors.common.success
import dev.brella.kornea.toolkit.common.KorneaToolkit
import dev.brella.kornea.toolkit.common.UNINITIALIZED_VALUE
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public class KorneaLazySingletonPool<T>(private val initialiser: () -> Poolable<T>, private val mutex: Mutex): KorneaPool<T> {
    private var instance: Any? = UNINITIALIZED_VALUE

    @Suppress("UNCHECKED_CAST")
    override suspend fun hire(): KorneaResult<Poolable<T>> {
        mutex.lock()
        if (instance === UNINITIALIZED_VALUE) instance = initialiser()
        val current = instance as? Poolable<T> ?: throw IllegalStateException("Instance is null, despite mutex being available (Unlocking... ${mutex.unlock()})")
        current.whenObtained(this)
        instance = null
        return KorneaResult.success(current)
    }

    override suspend fun retire(obj: Poolable<T>) {
        obj.whenReturned(this)
        instance = obj
        mutex.unlock()
    }
}