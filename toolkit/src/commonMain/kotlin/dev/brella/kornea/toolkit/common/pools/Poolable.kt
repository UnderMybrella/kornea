package dev.brella.kornea.toolkit.common.pools

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.toolkit.common.DataCloseable
import dev.brella.kornea.toolkit.common.KorneaToolkit

@ExperimentalUnsignedTypes
@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public interface Poolable<out T>: DataCloseable {
    public fun get(): T
    public fun whenObtained(from: KorneaPool<in T>)
    public fun whenReturned(to: KorneaPool<in T>)
}

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public inline fun <T, S: Poolable<T>> S.obtainFrom(pool: KorneaPool<in T>): S {
    whenObtained(pool)
    return this
}

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public inline fun <T, S: Poolable<T>> S.returnTo(pool: KorneaPool<in T>): S {
    whenReturned(pool)
    return this
}

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public class PoolableWrapper<out T>(public val inner: T): Poolable<T> {
    private var pool: KorneaPool<in T>? = null
    override val isClosed: Boolean
        get() = pool == null

    override fun get(): T = inner
    override fun whenObtained(from: KorneaPool<in T>) {
        pool = from
    }
    override fun whenReturned(to: KorneaPool<in T>) {
        pool = null
    }

    override suspend fun close() {
        pool?.retire(this)
    }
}