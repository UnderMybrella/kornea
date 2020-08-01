package dev.brella.kornea.toolkit.common.ascii

import dev.brella.kornea.toolkit.common.PrintFlow
import dev.brella.kornea.toolkit.common.ProgressBar
import dev.brella.kornea.toolkit.common.StdoutPrintFlow
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.roundToInt

public interface AsciiProgressBarConfig : CoroutineContext.Element {
    public companion object : AsciiProgressBarConfig, CoroutineContext.Key<AsciiProgressBarConfig> {
        public const val GLOBAL_DEFAULT_TRACK_LENGTH: Int = 20
        public const val GLOBAL_DEFAULT_TRACK_START: Char = '['
        public const val GLOBAL_DEFAULT_TRACK_END: Char = ']'
        public const val GLOBAL_DEFAULT_TRACK_SPACE: Char = ' '
        public const val GLOBAL_DEFAULT_TRACK_FILLED: Char = '#'
        public const val GLOBAL_DEFAULT_LOADING_TEXT: String = "Loading..."
        public const val GLOBAL_DEFAULT_LOADED_TEXT: String = "Loaded!"
        public const val GLOBAL_DEFAULT_SHOW_PERCENTAGE: Boolean = true

        override val defaultTrackLength: Int = GLOBAL_DEFAULT_TRACK_LENGTH
        override val defaultTrackStart: Char = GLOBAL_DEFAULT_TRACK_START
        override val defaultTrackEnd: Char = GLOBAL_DEFAULT_TRACK_END
        override val defaultTrackSpace: Char = GLOBAL_DEFAULT_TRACK_SPACE
        override val defaultTrackFilled: Char = GLOBAL_DEFAULT_TRACK_FILLED

        override val defaultLoadingText: String? = GLOBAL_DEFAULT_LOADING_TEXT
        override val defaultLoadedText: String? = GLOBAL_DEFAULT_LOADED_TEXT

        override val defaultShowPercentage: Boolean = GLOBAL_DEFAULT_SHOW_PERCENTAGE

        override val defaultContext: CoroutineContext = globalDefaultContext()
        override val defaultOutput: PrintFlow = StdoutPrintFlow
    }

    override val key: CoroutineContext.Key<*>
        get() = Companion

    public val defaultTrackLength: Int
    public val defaultTrackStart: Char
    public val defaultTrackEnd: Char
    public val defaultTrackSpace: Char
    public val defaultTrackFilled: Char
    public val defaultLoadingText: String?
    public val defaultLoadedText: String?
    public val defaultShowPercentage: Boolean

    public val defaultContext: CoroutineContext
    public val defaultOutput: PrintFlow
}

internal expect inline fun AsciiProgressBarConfig.Companion.globalDefaultContext(): CoroutineContext

internal class AsciiProgressBar(
    val trackLength: Int,
    val trackStart: Char,
    val trackEnd: Char,
    val trackSpace: Char,
    val trackFilled: Char,
    val loadingText: String?,
    val loadedText: String?,
    val showPercentage: Boolean
) : ProgressBar {
    val percentPerTrackSpace = ceil(100.0 / trackLength.toDouble())
    val tracks = Array(trackLength) { filled ->
        buildString {
            append(trackStart)

            repeat(filled) { append(trackFilled) }
            repeat(trackLength - filled) { append(trackSpace) }

            append(trackEnd)
            append(' ')
            append(loadingText)
        }
    }

    override fun trackProgress(current: Long, total: Long): Double {
        val percent = (current * 10000.0 / total) / 100.0
        val filled = min(tracks.size - 1, floor(percent / percentPerTrackSpace).roundToInt())
        print(buildString {
            append('\r')
            if (showPercentage) {
                append(percent)
                append("% ")
            }
            append(tracks[filled])
        })

        return percent
    }

    override fun complete() {
        print(buildString {
            append('\r')
            repeat(trackLength + 12 + (loadingText?.length ?: 0)) { append(' ') }
            append('\r')
        })
        loadedText?.let(::println)
    }
}

public fun asciiProgressBar(
    trackLength: Int,
    trackStart: Char,
    trackEnd: Char,
    trackSpace: Char,
    trackFilled: Char,
    loadingText: String?,
    loadedText: String?,
    showPercentage: Boolean
): ProgressBar = AsciiProgressBar(
    trackLength,
    trackStart,
    trackEnd,
    trackSpace,
    trackFilled,
    loadingText,
    loadedText,
    showPercentage
)

public inline fun <T> progressBar(
    trackLength: Int = AsciiProgressBarConfig.GLOBAL_DEFAULT_TRACK_LENGTH,
    trackStart: Char = AsciiProgressBarConfig.GLOBAL_DEFAULT_TRACK_START,
    trackEnd: Char = AsciiProgressBarConfig.GLOBAL_DEFAULT_TRACK_END,
    trackSpace: Char = AsciiProgressBarConfig.GLOBAL_DEFAULT_TRACK_SPACE,
    trackFilled: Char = AsciiProgressBarConfig.GLOBAL_DEFAULT_TRACK_FILLED,
    loadingText: String = AsciiProgressBarConfig.GLOBAL_DEFAULT_LOADING_TEXT,
    loadedText: String = AsciiProgressBarConfig.GLOBAL_DEFAULT_LOADED_TEXT,
    showPercentage: Boolean = AsciiProgressBarConfig.GLOBAL_DEFAULT_SHOW_PERCENTAGE,
    op: ProgressBar.() -> T
): T {
    val bar = asciiProgressBar(
        trackLength,
        trackStart,
        trackEnd,
        trackSpace,
        trackFilled,
        loadingText,
        loadedText,
        showPercentage
    )

    try {
        return bar.op()
    } finally {
        bar.complete()
    }
}