package dev.brella.kornea.errors.common

import dev.brella.kornea.config.common.Configuration
import kotlin.coroutines.CoroutineContext

public inline class KorneaResultConfig(public val shouldInlineClasses: Boolean = DEFAULT_INLINE_CLASSES): Configuration {
    public companion object {
        public const val DEFAULT_INLINE_CLASSES: Boolean = false

        public val DEFAULT: KorneaResultConfig = KorneaResultConfig(shouldInlineClasses = DEFAULT_INLINE_CLASSES)
    }
    override val key: CoroutineContext.Key<*> get() = KorneaResult
}