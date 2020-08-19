package dev.brella.kornea.toolkit.coroutines.pools

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.toolkit.common.KorneaToolkit

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public class KorneaEmptyPool<T>: KorneaPool<T> {
    override suspend fun hire(): KorneaResult<Poolable<T>> = KorneaResult.empty()
    override suspend fun retire(obj: Poolable<T>) {}
}