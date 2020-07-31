package dev.brella.kornea.toolkit.common.pools

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.toolkit.common.KorneaToolkit

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public interface KorneaPool<T> {
    public suspend fun hire(): KorneaResult<Poolable<T>>
    public suspend fun retire(obj: Poolable<T>)
}