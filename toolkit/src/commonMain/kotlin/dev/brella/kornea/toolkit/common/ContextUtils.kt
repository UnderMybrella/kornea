package dev.brella.kornea.toolkit.common

//import lumberjack.Logger
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

public data class KLogger(val out: PrintFlow): CoroutineContext.Element, PrintFlow by out {
    public companion object Key: CoroutineContext.Key<KLogger>

    override val key: CoroutineContext.Key<*>
        get() = Key
}

public suspend inline fun logger(): KLogger? = coroutineContext[KLogger]