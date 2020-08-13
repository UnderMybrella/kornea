package dev.brella.kornea.config.common

import kotlin.coroutines.CoroutineContext

public interface Configuration : CoroutineContext.Element {
    public data class MISSING(override val key: CoroutineContext.Key<*>): Configuration

    override fun minusKey(key: CoroutineContext.Key<*>): CoroutineContext =
        if (this.key == key) MISSING(key) else this
}