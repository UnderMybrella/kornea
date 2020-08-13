package dev.brella.kornea.config.common

import dev.brella.kornea.config.native.AtomicConfig
import kotlin.properties.ReadWriteProperty

internal actual inline fun <C : Configuration> defaultConfig(
    defaultConfig: C
): ReadWriteProperty<Configurable<C>, C> = AtomicConfig(defaultConfig)