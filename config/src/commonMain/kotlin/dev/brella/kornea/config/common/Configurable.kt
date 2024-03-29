package dev.brella.kornea.config.common

import dev.brella.kornea.annotations.AvailableSince
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.properties.ReadWriteProperty

@AvailableSince(KorneaConfig.VERSION_1_0_0_INDEV)
public interface Configurable<C: Configuration>: CoroutineContext.Key<Configuration> {
    public abstract class Base<C: Configuration>(initial: C): Configurable<C> {
        override var defaultConfig: C by defaultConfig(initial)
    }
    public var defaultConfig: C
}

@AvailableSince(KorneaConfig.VERSION_1_0_0_INDEV)
internal expect inline fun <C: Configuration> defaultConfig(defaultConfig: C): ReadWriteProperty<Configurable<C>, C>

@Suppress("UNCHECKED_CAST")
@AvailableSince(KorneaConfig.VERSION_1_0_0_INDEV)
public suspend inline fun <C: Configuration> Configurable<C>.config(): C? =
    coroutineContext[this]?.let { if (it is Configuration.MISSING) null else it as C }

@Suppress("UNCHECKED_CAST")
@AvailableSince(KorneaConfig.VERSION_1_0_1_INDEV)
public inline fun <C: Configuration> Configurable<C>.config(coroutineContext: CoroutineContext): C? =
    coroutineContext[this]?.let { if (it is Configuration.MISSING) null else it as C }

@AvailableSince(KorneaConfig.VERSION_1_0_0_INDEV)
public suspend inline fun <C: Configuration> Configurable<C>.configure(configure: (config: C?) -> C?): CoroutineContext {
    val newConfig = configure(config())

    return if (newConfig == null)
        coroutineContext + Configuration.MISSING(this)
    else
        coroutineContext + newConfig
}