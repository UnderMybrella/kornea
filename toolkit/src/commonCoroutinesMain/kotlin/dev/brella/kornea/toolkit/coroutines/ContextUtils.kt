package dev.brella.kornea.toolkit.coroutines

import dev.brella.kornea.toolkit.common.KLogger
import dev.brella.kornea.toolkit.common.PrintFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

public suspend inline fun <T> withLogger(logger: PrintFlow?, crossinline block: CoroutineScope.() -> T): T =
    withContext(if (logger == null) coroutineContext.minusKey(KLogger) else coroutineContext + KLogger(logger)) { block() }