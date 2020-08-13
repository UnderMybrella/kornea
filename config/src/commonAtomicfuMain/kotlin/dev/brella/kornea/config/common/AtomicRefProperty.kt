package dev.brella.kornea.config.common

import dev.brella.kornea.annotations.AvailableSince
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlin.jvm.JvmName
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@AvailableSince(KorneaConfig.VERSION_1_0_0_INDEV)
internal class AtomicRefProperty<T>(startingValue: T): ReadWriteProperty<Any?, T> {
    private val ref = atomic(startingValue)

    override fun getValue(thisRef: Any?, property: KProperty<*>): T =
        ref.value

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        ref.value = value
    }
}

internal actual inline fun <C: Configuration> defaultConfig(defaultConfig: C): ReadWriteProperty<Configurable<C>, C> =
    AtomicRefProperty(defaultConfig)