package dev.brella.kornea.toolkit.common.ascii

import dev.brella.kornea.toolkit.common.PrintFlow
import dev.brella.kornea.toolkit.common.StdoutPrintFlow
import dev.brella.kornea.toolkit.common.UNINITIALIZED_VALUE
import dev.brella.kornea.toolkit.common.printLine
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

public interface AsciiArbitraryProgressBarConfig : CoroutineContext.Element {
    public companion object : AsciiArbitraryProgressBarConfig, CoroutineContext.Key<AsciiArbitraryProgressBarConfig> {
        public const val GLOBAL_DEFAULT_DELAY: Long = 200L
        public const val GLOBAL_DEFAULT_TRACK_LIMIT: Int = 9
        public const val GLOBAL_DEFAULT_TRACK_START: Char = '['
        public const val GLOBAL_DEFAULT_TRACK_END: Char = ']'
        public const val GLOBAL_DEFAULT_TRACK_SPACE: Char = ' '
        public const val GLOBAL_DEFAULT_TRACK_INDICATOR: Char = 'o'
        public const val GLOBAL_DEFAULT_LOADING_TEXT: String = "Loading..."
        public const val GLOBAL_DEFAULT_LOADING_KEY: String = "ascii.arbitrary.loading"
        public const val GLOBAL_DEFAULT_LOADED_TEXT: String = "Loaded!"
        public const val GLOBAL_DEFAULT_LOADED_KEY: String = "ascii.arbitrary.loaded"

        override val defaultDelay: Long = GLOBAL_DEFAULT_DELAY
        override val defaultTrackLimit: Int = GLOBAL_DEFAULT_TRACK_LIMIT
        override val defaultTrackStart: Char = GLOBAL_DEFAULT_TRACK_START
        override val defaultTrackEnd: Char = GLOBAL_DEFAULT_TRACK_END
        override val defaultTrackSpace: Char = GLOBAL_DEFAULT_TRACK_SPACE
        override val defaultTrackIndicator: Char = GLOBAL_DEFAULT_TRACK_INDICATOR
        override val defaultLoadingText: String? = GLOBAL_DEFAULT_LOADING_TEXT
        override val defaultLoadedText: String? = GLOBAL_DEFAULT_LOADED_TEXT

        override val defaultContext: CoroutineContext = globalDefaultContext()
        override val defaultOutput: PrintFlow = StdoutPrintFlow
    }

    override val key: CoroutineContext.Key<*>
        get() = Companion

    public val defaultDelay: Long
    public val defaultTrackLimit: Int
    public val defaultTrackStart: Char
    public val defaultTrackEnd: Char
    public val defaultTrackSpace: Char
    public val defaultTrackIndicator: Char
    public val defaultLoadingText: String?
    public val defaultLoadedText: String?

    public val defaultContext: CoroutineContext
    public val defaultOutput: PrintFlow
}

internal expect inline fun AsciiArbitraryProgressBarConfig.Companion.globalDefaultContext(): CoroutineContext

public suspend inline fun <T> arbitraryProgressBar(
    context: CoroutineContext = AsciiArbitraryProgressBarConfig.defaultContext,
    output: PrintFlow = AsciiArbitraryProgressBarConfig.defaultOutput,
    delay: Long = AsciiArbitraryProgressBarConfig.defaultDelay,
    trackLimit: Int = AsciiArbitraryProgressBarConfig.defaultTrackLimit,
    trackStart: Char = AsciiArbitraryProgressBarConfig.defaultTrackStart,
    trackEnd: Char = AsciiArbitraryProgressBarConfig.defaultTrackEnd,
    trackSpace: Char = AsciiArbitraryProgressBarConfig.defaultTrackSpace,
    trackIndicator: Char = AsciiArbitraryProgressBarConfig.defaultTrackIndicator,
    loadingText: String? = AsciiArbitraryProgressBarConfig.defaultLoadingText,
    loadedText: String? = AsciiArbitraryProgressBarConfig.defaultLoadedText,
    crossinline operation: suspend () -> T
): T = coroutineScope {
    val arbitrary =
        createArbitraryProgressBar(
            context,
            output,
            delay,
            trackLimit,
            trackStart,
            trackEnd,
            trackSpace,
            trackIndicator,
            loadingText,
            loadedText
        )

    try {
        operation()
    } finally {
        arbitrary.cancelAndJoin()
    }
}

public suspend inline fun <T> AsciiArbitraryProgressBarConfig.arbitraryProgressBar(
    context: CoroutineContext = defaultContext,
    output: PrintFlow = defaultOutput,
    delay: Long = defaultDelay,
    trackLimit: Int = defaultTrackLimit,
    trackStart: Char = defaultTrackStart,
    trackEnd: Char = defaultTrackEnd,
    trackSpace: Char = defaultTrackSpace,
    trackIndicator: Char = defaultTrackIndicator,
    loadingText: String? = defaultLoadingText,
    loadedText: String? = defaultLoadedText,
    crossinline operation: suspend () -> T
): T = coroutineScope {
    val arbitrary =
        createArbitraryProgressBar(
            context,
            output,
            delay,
            trackLimit,
            trackStart,
            trackEnd,
            trackSpace,
            trackIndicator,
            loadingText,
            loadedText
        )

    try {
        operation()
    } finally {
        arbitrary.cancelAndJoin()
    }
}

public inline fun CoroutineScope.createArbitraryProgressBar(
    context: CoroutineContext? = null,
    output: PrintFlow? = null,
    delay: Long? = null,
    trackLimit: Int? = null,
    trackStart: Char? = null,
    trackEnd: Char? = null,
    trackSpace: Char? = null,
    trackIndicator: Char? = null,
    loadingText: Any? = UNINITIALIZED_VALUE,
    loadedText: Any? = UNINITIALIZED_VALUE
): Job {
    val config = this.coroutineContext[AsciiArbitraryProgressBarConfig] ?: AsciiArbitraryProgressBarConfig

    return createArbitraryProgressBar(
        context ?: config.defaultContext,
        output ?: config.defaultOutput,
        delay ?: config.defaultDelay,
        trackLimit ?: config.defaultTrackLimit,
        trackStart ?: config.defaultTrackStart,
        trackEnd ?: config.defaultTrackEnd,
        trackSpace ?: config.defaultTrackSpace,
        trackIndicator ?: config.defaultTrackIndicator,
        if (loadingText === UNINITIALIZED_VALUE) config.defaultLoadingText else loadingText?.toString(),
        if (loadedText === UNINITIALIZED_VALUE) config.defaultLoadedText else loadedText?.toString()
    )
}

public fun CoroutineScope.createArbitraryProgressBar(
    context: CoroutineContext,
    output: PrintFlow,
    delay: Long,
    trackLimit: Int,
    trackStart: Char,
    trackEnd: Char,
    trackSpace: Char,
    trackIndicator: Char,
    loadingText: String?,
    loadedText: String?
): Job = launch(context) {
    try {
        var progress: Int = 0
        var goingRight: Boolean = true

        while (isActive) {
            output.print(buildString {
                append('\r')
                append(trackStart)
                for (i in 0 until progress)
                    append(trackSpace)
                append(trackIndicator)
                for (i in 0 until (trackLimit - progress))
                    append(trackSpace)
                append(trackEnd)
                append(' ')
                if (loadingText != null) append(loadingText)
            })

            if (goingRight)
                progress++
            else
                progress--

            if (progress == trackLimit || progress == 0)
                goingRight = !goingRight

            delay(delay)
        }
    } finally {
        withContext(NonCancellable) {
            if (loadedText != null) {
                output.print(buildString {
                    append('\r')
                    for (i in 0 until trackLimit)
                        append(' ')
                    append("    ")
                    for (i in 0 until (loadingText?.length ?: 0))
                        append(' ')
                    append('\r')
                })

                output.printLine(loadedText)
            }
        }
    }
}