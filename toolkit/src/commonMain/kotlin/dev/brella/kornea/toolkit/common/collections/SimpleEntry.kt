package dev.brella.kornea.toolkit.common.collections

public data class SimpleEntry<K, V>(override val key: K, override val value: V): Map.Entry<K, V>