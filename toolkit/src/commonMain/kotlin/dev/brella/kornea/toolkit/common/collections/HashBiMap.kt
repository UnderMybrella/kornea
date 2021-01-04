package dev.brella.kornea.toolkit.common.collections

public class HashBiMap<K, V>(private val backingKeyMap: HashMap<K, V> = HashMap(), private val backingValueMap: HashMap<V, K> = HashMap()): MutableBiMap<K, V>, MutableMap<K, V> by backingKeyMap {
    override val values: MutableSet<V>
        get() = backingValueMap.keys

    override fun get(key: K): V? = backingKeyMap[key]

    override fun getFromKey(key: K): V? = backingKeyMap[key]
    override fun getFromValue(value: V): K? = backingValueMap[value]

    override fun put(key: K, value: V): V? {
        if (value !in backingValueMap) {
            val existing = backingKeyMap.put(key, value)
            backingValueMap[value] = key
            return existing
        }

        throw IllegalArgumentException("Given value is already bound to a key (Key: $key, Value: $value)")
    }

    override fun putAll(from: Map<out K, V>) {
        from.entries.firstOrNull { (_, v) -> v in backingValueMap }?.let { (k, v) -> throw IllegalArgumentException("Given value is already bound to a key (Key: $k, Value: $v)") }
        from.forEach { (k, v) -> put(k, v) }
    }

    override fun forcePut(key: K, value: V): V? {
        val existing = backingKeyMap.put(key, value)
        backingValueMap[value] = key
        return existing
    }

    override fun remove(key: K): V? {
        val existing = backingKeyMap.remove(key) ?: return null
        backingValueMap.remove(existing)
        return existing
    }

    override fun inverse(): MutableBiMap<V, K> = HashBiMap(backingValueMap, backingKeyMap)
}