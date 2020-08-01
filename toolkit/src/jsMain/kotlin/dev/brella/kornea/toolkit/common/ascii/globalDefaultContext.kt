package dev.brella.kornea.toolkit.common.ascii

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

internal actual inline fun AsciiArbitraryProgressBarConfig.Companion.globalDefaultContext(): CoroutineContext =
    Dispatchers.Main

internal actual inline fun AsciiProgressBarConfig.Companion.globalDefaultContext(): CoroutineContext =
    Dispatchers.Main