package dev.brella.kornea.toolkit.common.ascii

import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

internal val GLOBAL_ASCII_THREAD_DISPATCHER = Executors.newSingleThreadExecutor { Thread(it, "KorneaAscii") }.asCoroutineDispatcher()

internal actual inline fun AsciiArbitraryProgressBarConfig.Companion.globalDefaultContext(): CoroutineContext = GLOBAL_ASCII_THREAD_DISPATCHER
internal actual inline fun AsciiProgressBarConfig.Companion.globalDefaultContext(): CoroutineContext = GLOBAL_ASCII_THREAD_DISPATCHER