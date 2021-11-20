package dev.brella.kornea.toolkit.common

import kotlin.reflect.KProperty

public class OneTimeMutable<T> {
    private var _value: Any? = UNINITIALISED_VALUE

    @Suppress("UNCHECKED_CAST")
    public var value: T
        get() =
            if (_value === UNINITIALISED_VALUE) throw IllegalStateException("Value not initialised")
            else _value as T
        set(value) {
            if (_value === UNINITIALISED_VALUE) {
                _value = value
            } else {
                throw IllegalStateException("Value was already initialised")
            }
        }

    public inline operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value
    public inline operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}

public class OneTimeMutableInline<T>(private var _value: Any? = UNINITIALISED_VALUE) {
    @Suppress("UNCHECKED_CAST")
    public var value: T
        get() =
            if (_value === UNINITIALISED_VALUE) _value as T
            else throw IllegalStateException("Value not initialised")

        set(value) {
            if (_value === UNINITIALISED_VALUE) {
                _value = value
            } else {
                throw IllegalStateException("Value was already initialised")
            }
        }

    public inline operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value
    public inline operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}

public inline fun <reified T> oneTimeMutable(): OneTimeMutable<T> = OneTimeMutable()
public inline fun <reified T> oneTimeMutableInline(): OneTimeMutableInline<T> = OneTimeMutableInline()