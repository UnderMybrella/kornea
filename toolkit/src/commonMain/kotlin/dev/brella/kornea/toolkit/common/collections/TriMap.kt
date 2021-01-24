package dev.brella.kornea.toolkit.common.collections

public interface TriMap<K, PV, SV>: Map<K, Pair<PV, SV>> {
    /**
     * Returns `true` if the map maps one or more keys to the specified [value].
     */
    public fun containsPrimaryValue(value: PV): Boolean

    /**
     * Returns `true` if the map maps one or more keys to the specified [value].
     */
    public fun containsSecondaryValue(value: SV): Boolean

    /**
     * Returns the value corresponding to the given [key], or `null` if such a key is not present in the map.
     */
    public fun getPrimaryFromKey(key: K): PV?

    /**
     * Returns the value corresponding to the given [key], or `null` if such a key is not present in the map.
     */
    public fun getSecondaryFromKey(key: K): SV?

    /**
     * Returns the key corresponding to the given [value], or `null` if such a value is not present in the map.
     */
    public fun getKeyFromPrimary(value: PV): K?

    /**
     * Returns the key corresponding to the given [value], or `null` if such a value is not present in the map.
     */
    public fun getKeyFromSecondary(value: SV): K?

    /**
     * Returns the value corresponding to the given [key], or [defaultValue] if such a key is not present in the map.
     *
     * @since JDK 1.8
     */
    @SinceKotlin("1.1")
    public fun getOrDefaultPrimaryFromKey(key: K, defaultValue: @UnsafeVariance PV): PV = getPrimaryFromKey(key) ?: defaultValue

    /**
     * Returns the value corresponding to the given [key], or [defaultValue] if such a key is not present in the map.
     *
     * @since JDK 1.8
     */
    @SinceKotlin("1.1")
    public fun getOrDefaultSecondaryFromKey(key: K, defaultValue: @UnsafeVariance SV): SV = getSecondaryFromKey(key) ?: defaultValue

    /**
     * Returns the value corresponding to the given [value], or [defaultKey] if such a value is not present in the map.
     *
     * @since JDK 1.8
     */
    @SinceKotlin("1.1")
    public fun getOrDefaultKeyFromPrimary(value: PV, defaultKey: @UnsafeVariance K): K = getKeyFromPrimary(value) ?: defaultKey

    /**
     * Returns the value corresponding to the given [value], or [defaultKey] if such a value is not present in the map.
     *
     * @since JDK 1.8
     */
    @SinceKotlin("1.1")
    public fun getOrDefaultKeyFromSecondary(value: SV, defaultKey: @UnsafeVariance K): K = getKeyFromSecondary(value) ?: defaultKey

    /**
     * Returns a read-only [Set] of all key/value pairs in this map.
     */
    public override val entries: Set<Map.Entry<K, Pair<PV, SV>>>
    
    /**
     * Returns a read-only [Collection] of the primary values in this map.
     */
    public val primaryValues: Set<PV>
    /**
     * Returns a read-only [Collection] of the secondaryary values in this map.
     */
    public val secondaryValues: Set<SV>

//    public fun inversePrimary(): TriMap<PV, K, SV>
//    public fun inverseSecondary(): TriMap<SV, K, PV>
}

public interface MutableTriMap<K, PV, SV> : TriMap<K, PV, SV>, MutableMap<K, Pair<PV, SV>> {
    /**
     * Returns a [Collection] of the primary values in this map.
     */
    public override val primaryValues: MutableSet<PV>
    /**
     * Returns a [Collection] of the secondaryary values in this map.
     */
    public override val secondaryValues: MutableSet<SV>

    public fun put(key: K, primary: PV, secondary: SV): Pair<PV, SV>?
    public override fun put(key: K, value: Pair<PV, SV>): Pair<PV, SV>?
    public override fun putAll(from: Map<out K, Pair<PV, SV>>)

    public fun updatePrimary(key: K, value: PV): PV?
    public fun updateSecondary(key: K, value: SV): SV?

    public fun forcePut(key: K, primary: PV, secondary: SV): Pair<PV, SV>?
    public fun forcePut(key: K, value: Pair<PV, SV>): Pair<PV, SV>?

//    override fun inversePrimary(): MutableTriMap<PV, K, SV>
//    override fun inverseSecondary(): MutableTriMap<SV, K, PV>
}