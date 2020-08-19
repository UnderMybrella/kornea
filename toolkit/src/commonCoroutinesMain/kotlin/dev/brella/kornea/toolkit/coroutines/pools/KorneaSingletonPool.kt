package dev.brella.kornea.toolkit.coroutines.pools

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.toolkit.common.KorneaToolkit
import kotlinx.coroutines.sync.Mutex

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public class KorneaSingletonPool<T>(instance: Poolable<T>, private val mutex: Mutex = Mutex()): KorneaPool<T> {
    private var instance: Poolable<T>? = instance

    override suspend fun hire(): KorneaResult<Poolable<T>> {
        mutex.lock()
        val current = instance ?: throw IllegalStateException("Instance is null, despite mutex being available (Unlocking... ${mutex.unlock()})")
        instance = null
        current.whenObtained(this)
        return KorneaResult.success(current)
    }

    override suspend fun retire(obj: Poolable<T>) {
        obj.whenReturned(this)
        instance = obj
        mutex.unlock()
    }
}