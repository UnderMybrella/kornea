package dev.brella.kornea.toolkit.coroutines.pools

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.toolkit.common.KorneaToolkit
import kotlinx.coroutines.sync.Mutex

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public object KorneaPools {
    public inline fun <T, P: Poolable<T>> newCachedPool(maximumSize: Int, noinline initialiser: () -> P): KorneaPool<T> =
        KorneaCachedPool(maximumSize, initialiser, Mutex())
}

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public inline fun <T> KorneaPools.newCachedPool(maximumSize: Int, noinline initialiser: () -> T): KorneaPool<T> =
    KorneaCachedPool(maximumSize, { PoolableWrapper(initialiser()) }, Mutex())