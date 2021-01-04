package dev.brella.kornea.toolkit.common.collections

public class HashTriMap<K, PV, SV>(private val backingMap: HashMap<K, Pair<PV, SV>> = HashMap(), private val backingPrimaryMap: HashMap<PV, K> = HashMap(), private val backingSecondaryMap: HashMap<SV, K> = HashMap()): MutableTriMap<K, PV, SV>, MutableMap<K, Pair<PV, SV>> by backingMap {
    override fun containsPrimaryValue(value: PV): Boolean = value in backingPrimaryMap
    override fun containsSecondaryValue(value: SV): Boolean = value in backingSecondaryMap

    override fun getPrimaryFromKey(key: K): PV? = backingMap[key]?.first
    override fun getSecondaryFromKey(key: K): SV? = backingMap[key]?.second

    override fun getKeyFromPrimary(value: PV): K? = backingPrimaryMap[value]
    override fun getKeyFromSecondary(value: SV): K? = backingSecondaryMap[value]

    override fun getOrDefaultPrimaryFromKey(key: K, defaultValue: PV): PV = backingMap[key]?.first ?: defaultValue
    override fun getOrDefaultSecondaryFromKey(key: K, defaultValue: SV): SV = backingMap[key]?.second ?: defaultValue

    override fun getOrDefaultKeyFromPrimary(value: PV, defaultKey: K): K = backingPrimaryMap[value] ?: defaultKey
    override fun getOrDefaultKeyFromSecondary(value: SV, defaultKey: K): K = backingSecondaryMap[value] ?: defaultKey

    override val entries: MutableSet<MutableMap.MutableEntry<K, Pair<PV, SV>>>
        get() = backingMap.entries
    override val primaryValues: MutableSet<PV>
        get() = backingPrimaryMap.keys
    override val secondaryValues: MutableSet<SV>
        get() = backingSecondaryMap.keys

    override fun put(key: K, primary: PV, secondary: SV): Pair<PV, SV>? {
        if (primary in backingPrimaryMap) {
            throw IllegalArgumentException("Given primary value is already bound to a key (Key: $key, Value: $primary)")
        } else if (secondary in backingSecondaryMap) {
            throw IllegalArgumentException("Given secondary value is already bound to a key (Key: $key, Value: $secondary)")
        } else {
            val existing = backingMap.put(key, Pair(primary, secondary))
            backingPrimaryMap[primary] = key
            backingSecondaryMap[secondary] = key
            return existing
        }
    }

    override fun put(key: K, value: Pair<PV, SV>): Pair<PV, SV>? {
        if (value.first in backingPrimaryMap) {
            throw IllegalArgumentException("Given primary value is already bound to a key (Key: $key, Value: ${value.first})")
        } else if (value.second in backingSecondaryMap) {
            throw IllegalArgumentException("Given secondary value is already bound to a key (Key: $key, Value: ${value.second})")
        } else {
            val existing = backingMap.put(key, value)
            backingPrimaryMap[value.first] = key
            backingSecondaryMap[value.second] = key
            return existing
        }
    }

    override fun updatePrimary(key: K, value: PV): PV? {
        val existing = backingMap[key] ?: return null
        backingPrimaryMap.remove(existing.first)
        backingMap[key] = Pair(value, existing.second)
        backingPrimaryMap[value] = key
        return existing.first
    }

    override fun updateSecondary(key: K, value: SV): SV? {
        val existing = backingMap[key] ?: return null
        backingSecondaryMap.remove(existing.second)
        backingMap[key] = Pair(existing.first, value)
        backingSecondaryMap[value] = key
        return existing.second
    }

    override fun forcePut(key: K, primary: PV, secondary: SV): Pair<PV, SV>? {
        val existing = backingMap.put(key, Pair(primary, secondary))
        backingPrimaryMap[primary] = key
        backingSecondaryMap[secondary] = key
        return existing
    }

    override fun forcePut(key: K, value: Pair<PV, SV>): Pair<PV, SV>? {
        val existing = backingMap.put(key, value)
        backingPrimaryMap[value.first] = key
        backingSecondaryMap[value.second] = key
        return existing
    }

    override fun putAll(from: Map<out K, Pair<PV, SV>>) {
        from.entries.firstOrNull { (_, v) -> v.first in backingPrimaryMap || v.second in backingSecondaryMap }?.let { (k, v) -> throw IllegalArgumentException("Given value is already bound to a key (Key: $k, Value: $v)") }
        from.forEach { (k, v) -> put(k, v) }
    }
}