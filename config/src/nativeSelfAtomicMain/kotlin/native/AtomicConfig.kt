package dev.brella.kornea.config.native

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.config.common.KorneaConfig
import kotlin.native.concurrent.FreezableAtomicReference
import kotlin.native.concurrent.freeze
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@AvailableSince(KorneaConfig.VERSION_1_0_0_INDEV)
internal value class AtomicConfig<T>(private val ref: FreezableAtomicReference<T>): ReadWriteProperty<Any?, T> {
    companion object {
        /**
         * NOTE: This has to be an operator function not a constructor, otherwise we get an IR error
         */
        inline operator fun <T> invoke(value: T): AtomicConfig<T> = AtomicConfig(FreezableAtomicReference(value).apply { freeze() })
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T =
        ref.value

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        ref.value = value
    }
}