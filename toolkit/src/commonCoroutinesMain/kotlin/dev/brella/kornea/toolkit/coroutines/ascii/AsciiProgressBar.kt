package dev.brella.kornea.toolkit.coroutines.ascii

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.toolkit.common.*
import dev.brella.kornea.toolkit.coroutines.ChannelBasedProgressBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.math.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@AvailableSince(KorneaToolkit.VERSION_3_0_0_ALPHA)
public fun interface AsciiProgressBarStyle {
    public companion object {
        public val BASIC: AsciiProgressBarStyle
            inline get() = Basic(
                Basic.DEFAULT_TRACK_START,
                Basic.DEFAULT_TRACK_END,
                Basic.DEFAULT_TRACK_SPACE,
                Basic.DEFAULT_TRACK_FILLED
            )
        public val BASIC_TRAILING: AsciiProgressBarStyle
            inline get() = BasicTrailing(
                BasicTrailing.DEFAULT_TRACK_START,
                BasicTrailing.DEFAULT_TRACK_END,
                BasicTrailing.DEFAULT_TRACK_SPACE,
                BasicTrailing.DEFAULT_TRACK_FILLED,
                BasicTrailing.DEFAULT_TRACK_TRAILING
            )
        public val FLOWING: AsciiProgressBarStyle
            inline get() = Flowing(
                Flowing.DEFAULT_TRACK_START,
                Flowing.DEFAULT_TRACK_END,
                Flowing.DEFAULT_TRACK_SPACE,
                Flowing.DEFAULT_TRACK_RIVER
            )
    }

    public data class Basic(
        val trackStart: String,
        val trackEnd: String,
        val trackSpace: String,
        val trackFilled: String
    ) : AsciiProgressBarStyle {
        public companion object {
            public const val DEFAULT_TRACK_START: String = "["
            public const val DEFAULT_TRACK_END: String = "]"
            public const val DEFAULT_TRACK_SPACE: String = " "
            public const val DEFAULT_TRACK_FILLED: String = "#"
        }

        override fun StringBuilder.buildTrack(
            current: Long,
            percent: Double,
            trackLength: Int
        ) {
            append(trackStart)

            val filled = min(trackLength - 1, (percent / ceil(100.0 / trackLength.toDouble())).roundToInt())

            repeat(filled) { append(trackFilled) }
            repeat(trackLength - filled) { append(trackSpace) }

            append(trackEnd)
        }
    }

    public data class BasicTrailing(
        val trackStart: String,
        val trackEnd: String,
        val trackSpace: String,
        val trackFilled: String,
        val trackTrailing: String
    ) : AsciiProgressBarStyle {
        public companion object {
            public const val DEFAULT_TRACK_START: String = "["
            public const val DEFAULT_TRACK_END: String = "]"
            public const val DEFAULT_TRACK_SPACE: String = " "
            public const val DEFAULT_TRACK_FILLED: String = "="
            public const val DEFAULT_TRACK_TRAILING: String = "#"
        }

        override fun StringBuilder.buildTrack(
            current: Long,
            percent: Double,
            trackLength: Int
        ) {
            append(trackStart)

            val filled = min(trackLength, (percent / ceil(100.0 / trackLength.toDouble())).roundToInt())

            repeat(filled - 1) {
                append(trackFilled)
            }

            if (filled > 0) {
                append(trackTrailing)
            }

            repeat(trackLength - filled) { append(trackSpace) }

            append(trackEnd)
        }
    }

    public data class Flowing(
        val trackStart: String,
        val trackEnd: String,
        val trackSpace: String,
        val trackRiver: Array<Array<String>>
    ) : AsciiProgressBarStyle {
        public companion object {
            public const val DEFAULT_TRACK_START: String = "["
            public const val DEFAULT_TRACK_END: String = "]"
            public const val DEFAULT_TRACK_SPACE: String = " "
            public val DEFAULT_TRACK_RIVER: Array<Array<String>> = arrayOf(
                arrayOf(),
                arrayOf("▁"),
                arrayOf("▁", "▂"),
                arrayOf("▁", "▂", "▃", "▂"),
                arrayOf("▁", "▂", "▃", "▄", "▃", "▂"),
                arrayOf("▁", "▂", "▃", "▄", "▅", "▄", "▃", "▂"),
                arrayOf("▁", "▂", "▃", "▄", "▅", "▆", "▅", "▄", "▃", "▂"),
                arrayOf("▁", "▂", "▃", "▄", "▅", "▆", "▇", "▆", "▅", "▄", "▃", "▂"),
                arrayOf("▁", "▂", "▃", "▄", "▅", "▆", "▇", "█", "▇", "▆", "▅", "▄", "▃", "▂")
            )
        }

        private var waveIndex = 0
        private var prevFilled = 0
        private var prevWave = trackRiver[min(prevFilled, trackRiver.size - 1)]

        override fun StringBuilder.buildTrack(current: Long, percent: Double, trackLength: Int) {
            append(trackStart)

            val filled = min(trackLength, (percent / ceil(100.0 / trackLength.toDouble())).roundToInt())
            val wave: Array<String>

            if (filled > prevFilled) {
                if (prevWave.size < 2) {
                    wave = trackRiver[min(filled, trackRiver.size - 1)]
                    waveIndex = 0

                    prevFilled = filled
                    prevWave = wave
                } else {
                    waveIndex %= prevWave.size

                    if (waveIndex >= (prevWave.size / 2)) {
                        wave = trackRiver[min(filled, trackRiver.size - 1)]
                        waveIndex = wave.lastIndexOf(prevWave[waveIndex])

                        prevFilled = filled
                        prevWave = wave
                    } else {
                        wave = prevWave
                    }
                }
            } else {
                wave = prevWave
            }

            repeat(filled) {
                append(wave[(waveIndex + it) % wave.size])
            }

            waveIndex++

            repeat(trackLength - filled) { append(' ') }

            append("]")
        }
    }

    public fun StringBuilder.buildTrack(current: Long, percent: Double, trackLength: Int)
}

@AvailableSince(KorneaToolkit.VERSION_3_0_0_ALPHA)
public inline fun AsciiProgressBarStyle.buildTrack(
    builder: StringBuilder,
    current: Long,
    percent: Double,
    trackLength: Int
): Unit = builder.buildTrack(current, percent, trackLength)

@AvailableSince(KorneaToolkit.VERSION_3_0_0_ALPHA)
public interface AsciiProgressBarConfig : CoroutineContext.Element {
    public companion object : AsciiProgressBarConfig, CoroutineContext.Key<AsciiProgressBarConfig> {
        public const val GLOBAL_DEFAULT_TRACK_LENGTH: Int = 20

        public val GLOBAL_DEFAULT_TRACK_STYLE: AsciiProgressBarStyle
            inline get() = AsciiProgressBarStyle.BASIC
        public const val GLOBAL_DEFAULT_LOADING_TEXT: String = "Loading..."
        public const val GLOBAL_DEFAULT_LOADED_TEXT: String = "Loaded!"
        public const val GLOBAL_DEFAULT_SHOW_PERCENTAGE: Boolean = true

        public const val GLOBAL_DEFAULT_UPDATE_INTERVAL_MS: Long = 200
        public const val GLOBAL_DEFAULT_UPDATE_ON_EMPTY: Boolean = false

        override val defaultTrackLength: Int = GLOBAL_DEFAULT_TRACK_LENGTH
        override val defaultTrackStyle: AsciiProgressBarStyle
            get() = GLOBAL_DEFAULT_TRACK_STYLE

        override val defaultLoadingText: String? = GLOBAL_DEFAULT_LOADING_TEXT
        override val defaultLoadedText: String? = GLOBAL_DEFAULT_LOADED_TEXT

        override val defaultShowPercentage: Boolean = GLOBAL_DEFAULT_SHOW_PERCENTAGE

        @OptIn(ExperimentalTime::class)
        override val defaultUpdateInterval: Duration = GLOBAL_DEFAULT_UPDATE_INTERVAL_MS.milliseconds
        override val defaultUpdateOnEmpty: Boolean = GLOBAL_DEFAULT_UPDATE_ON_EMPTY

        override val defaultContext: CoroutineContext = globalDefaultContext()
        override val defaultOutput: PrintFlow = StdoutPrintFlow
    }

    override val key: CoroutineContext.Key<*>
        get() = Companion

    public val defaultTrackLength: Int
    public val defaultTrackStyle: AsciiProgressBarStyle
    public val defaultLoadingText: String?
    public val defaultLoadedText: String?
    public val defaultShowPercentage: Boolean

    @OptIn(ExperimentalTime::class)
    public val defaultUpdateInterval: Duration
    public val defaultUpdateOnEmpty: Boolean

    public val defaultContext: CoroutineContext
    public val defaultOutput: PrintFlow
}

@AvailableSince(KorneaToolkit.VERSION_3_0_0_ALPHA)
internal expect inline fun AsciiProgressBarConfig.Companion.globalDefaultContext(): CoroutineContext

@OptIn(ExperimentalTime::class)
@AvailableSince(KorneaToolkit.VERSION_3_0_0_ALPHA)
internal class AsciiProgressBar(
    scope: CoroutineScope,
    override val progressLimit: Long,
    val trackLength: Int,
    val trackStyle: AsciiProgressBarStyle,
    val loadingText: String?,
    val loadedText: String?,
    val showPercentage: Boolean,

    val context: CoroutineContext,
    val output: PrintFlow,

    updateInterval: Duration,
    updateOnEmpty: Boolean
) : ChannelBasedProgressBar(scope, context, updateInterval, updateOnEmpty) {
    override val channel: Channel<Long> = Channel(Channel.CONFLATED)

    override suspend fun update(current: Long) {
        withContext(context) {
            val percent = (current * 10000.0 / progressLimit) / 100.0
            output.print(buildString {
                append('\r')
                if (showPercentage) {
                    append(percent)
                    append("% ")
                }

                trackStyle.buildTrack(this, current, percent, trackLength)

                append(' ')
                loadingText?.let(this::append)
            })
        }
    }

    override suspend fun whenCompleted() {
        withContext(context) {
            output.print(buildString {
                append('\r')
                repeat(trackLength + 12 + (loadingText?.length ?: 0)) { append(' ') }
                append('\r')

                if (loadedText != null) appendLine(loadedText)
            })
        }
    }
}

@ExperimentalTime
@PublishedApi
@AvailableSince(KorneaToolkit.VERSION_3_0_0_ALPHA)
internal suspend fun CoroutineScope.asciiProgressBar(
    progressLimit: Long,
    trackLength: Int,
    trackStyle: AsciiProgressBarStyle,
    loadingText: String?,
    loadedText: String?,
    showPercentage: Boolean,
    context: CoroutineContext,
    output: PrintFlow,
    updateInterval: Duration,
    updateOnEmpty: Boolean
): ProgressBar = init(
    AsciiProgressBar(
        this,
        progressLimit,
        trackLength,
        trackStyle,
        loadingText,
        loadedText,
        showPercentage,
        context,
        output,
        updateInterval,
        updateOnEmpty
    )
)

@ExperimentalTime
@AvailableSince(KorneaToolkit.VERSION_3_0_0_ALPHA)
public suspend inline fun <T> progressBar(
    progressLimit: Long,
    trackLength: Int = AsciiProgressBarConfig.GLOBAL_DEFAULT_TRACK_LENGTH,
    trackStyle: AsciiProgressBarStyle = AsciiProgressBarConfig.GLOBAL_DEFAULT_TRACK_STYLE,
    loadingText: String? = AsciiProgressBarConfig.GLOBAL_DEFAULT_LOADING_TEXT,
    loadedText: String? = AsciiProgressBarConfig.GLOBAL_DEFAULT_LOADED_TEXT,
    showPercentage: Boolean = AsciiProgressBarConfig.GLOBAL_DEFAULT_SHOW_PERCENTAGE,
    context: CoroutineContext = AsciiProgressBarConfig.defaultContext,
    output: PrintFlow = AsciiProgressBarConfig.defaultOutput,
    updateInterval: Duration = AsciiProgressBarConfig.defaultUpdateInterval,
    updateOnEmpty: Boolean = AsciiProgressBarConfig.GLOBAL_DEFAULT_UPDATE_ON_EMPTY,
    crossinline op: suspend ProgressBar.() -> T
): T = coroutineScope {
    val bar = asciiProgressBar(
        progressLimit,
        trackLength,
        trackStyle,
        loadingText,
        loadedText,
        showPercentage,
        context,
        output,
        updateInterval,
        updateOnEmpty
    )

    try {
        bar.op()
    } finally {
        bar.complete()
    }
}

@ExperimentalTime
@AvailableSince(KorneaToolkit.VERSION_3_0_0_ALPHA)
public suspend inline fun <T> AsciiProgressBarConfig.progressBar(
    progressLimit: Long,
    trackLength: Int = defaultTrackLength,
    trackStyle: AsciiProgressBarStyle = defaultTrackStyle,
    loadingText: String? = defaultLoadingText,
    loadedText: String? = defaultLoadedText,
    showPercentage: Boolean = defaultShowPercentage,
    context: CoroutineContext = defaultContext,
    output: PrintFlow = defaultOutput,
    updateInterval: Duration = defaultUpdateInterval,
    updateOnEmpty: Boolean = defaultUpdateOnEmpty,
    crossinline op: suspend ProgressBar.() -> T
): T = coroutineScope {
    val bar = asciiProgressBar(
        progressLimit,
        trackLength,
        trackStyle,
        loadingText,
        loadedText,
        showPercentage,
        context,
        output,
        updateInterval,
        updateOnEmpty
    )

    try {
        bar.op()
    } finally {
        bar.complete()
    }
}