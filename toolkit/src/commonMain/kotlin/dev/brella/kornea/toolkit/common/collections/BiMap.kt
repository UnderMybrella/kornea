package dev.brella.kornea.toolkit.common.collections

public interface BiMap<K, V> : Map<K, V> {
    override val values: Set<V>

    override operator fun get(key: K): V? = getFromKey(key)

    public fun getFromKey(key: K): V?
    public fun getFromValue(value: V): K?

    public fun inverse(): BiMap<V, K>
}

public interface MutableBiMap<K, V> : MutableMap<K, V>, BiMap<K, V> {
    override val values: MutableSet<V>

    override fun put(key: K, value: V): V?
    override fun putAll(from: Map<out K, V>)

    public fun forcePut(key: K, value: V): V?

    override fun inverse(): MutableBiMap<V, K>
}