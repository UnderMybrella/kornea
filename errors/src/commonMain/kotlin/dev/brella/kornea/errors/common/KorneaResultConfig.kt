package dev.brella.kornea.errors.common

import dev.brella.kornea.config.common.Configuration
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmInline

@JvmInline
public value class KorneaResultConfig(public val usePooledResult: Boolean = DEFAULT_INLINE_CLASSES): Configuration {
    public companion object {
        public const val DEFAULT_INLINE_CLASSES: Boolean = true

        public val DEFAULT: KorneaResultConfig = KorneaResultConfig(usePooledResult = DEFAULT_INLINE_CLASSES)
    }
    override val key: CoroutineContext.Key<*> get() = KorneaResult
}