package dev.brella.kornea.io.common

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.base.common.ObservableDataCloseable
import dev.brella.kornea.base.common.use
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.errors.common.map
import dev.brella.kornea.io.common.flow.InputFlow
import dev.brella.kornea.io.common.flow.OutputFlow
import dev.brella.kornea.io.common.flow.extensions.copyToOutputFlow
import kotlin.jvm.JvmInline

/**
 * An interface that loosely defines a source of data - usually reproducible. This data may come from anywhere
 */
public interface DataSource<out I : InputFlow> : ObservableDataCloseable {
    public companion object {
        public const val ERRORS_SOURCE_CLOSED: Int = 0x1000
        public const val ERRORS_TOO_MANY_FLOWS_OPEN: Int = 0x1001
        public const val ERRORS_UNKNOWN: Int = 0x1FFF

        public inline fun <T> korneaSourceClosed(message: String = "Sink closed"): KorneaResult<T> =
            KorneaResult.errorAsIllegalState(ERRORS_SOURCE_CLOSED, message)

        public inline fun <T> korneaTooManySourcesOpen(capacity: Int?): KorneaResult<T> =
            korneaTooManySourcesOpen("Too many flows open (Capacity: $capacity)")

        public inline fun <T> korneaTooManySourcesOpen(message: String): KorneaResult<T> =
            KorneaResult.errorAsIllegalState(ERRORS_TOO_MANY_FLOWS_OPEN, message)

        public inline fun <T> korneaSourceUnknown(message: String = "An unknown error has occurred"): KorneaResult<T> =
            KorneaResult.errorAsIllegalState(ERRORS_UNKNOWN, message)
    }

    public val dataSize: ULong?

    public val location: String?

    /**
     * The reproducibility traits of this data source.
     *
     * These traits *may* change between invocations, so a fresh instance should be obtained each time
     */
    public val reproducibility: DataSourceReproducibility

    @AvailableSince(KorneaIO.VERSION_5_0_0_ALPHA)
    public fun locationAsUri(): KorneaResult<Uri>

    public suspend fun openInputFlow(): KorneaResult<I> = openNamedInputFlow(null)
    public suspend fun openNamedInputFlow(location: String? = null): KorneaResult<I>
    public suspend fun canOpenInputFlow(): Boolean
}

public suspend fun DataSource<*>.copyToOutputFlow(sink: OutputFlow): KorneaResult<Long> =
    openInputFlow().map { flow -> flow.copyToOutputFlow(sink) }

public suspend inline fun <T : InputFlow, reified R> DataSource<T>.useInputFlow(block: (T) -> R): KorneaResult<R> =
    openInputFlow().map { flow -> flow.use(block) }

@JvmInline
public value class DataSourceReproducibility(public val flag: Byte) {
    public constructor(flag: Number) : this(flag.toByte())
    public constructor(
        isStatic: Boolean = false,
        isDeterministic: Boolean = false,
        isExpensive: Boolean = false,
        isUnreliable: Boolean = false,
        isUnstable: Boolean = false,
        isRandomAccess: Boolean = false
    ) : this(
        (if (isStatic) STATIC_MASK else 0)
                or (if (isDeterministic) DETERMINISTIC_MASK else 0)
                or (if (isExpensive) EXPENSIVE_MASK else 0)
                or (if (isUnreliable) UNRELIABLE_MASK else 0)
                or (if (isUnstable) UNSTABLE_MASK else 0)
                or (if (isRandomAccess) RANDOM_ACCESS_MASK else 0)
    )

    public companion object {
        /**
         * The data is static, based in a reproducible, unchanging form.
         * Examples include file- or memory- based data sources.
         * Static data should not normally be cached.
         */
        public const val STATIC_MASK: Int = 0b00000001

        /**
         * The data is deterministic; it can be produced from an initial state, up to the end of the data stream.
         * Deterministic data may or may not need to be cached.
         * Slow operations, such as compression, may get a boost out of caching, whereas operations such as decompression will get little boost out of it.
         * Caching may be required in circumstances such as the underlying algorithm requiring impossible data, but that is left up to the invoker.
         */
        public const val DETERMINISTIC_MASK: Int = 0b00000010

        /**
         * The data is expensive; while it can be accessed or produced, such operations are expensive in time, memory, or computational resources.
         * Expensive data tends to cover network or compression resources; scenarios where the data is slow to retrieve, and caching will massively speed this up.
         * Expensive data sources should be cached where possible.
         */
        public const val EXPENSIVE_MASK: Int = 0b00000100

        /**
         * The source is unreliable; properties about this data, such as the size, or even the data, **may** change over time.
         * Unreliable data sources are guaranteed to be usable for at least one stream. Using this data source multiple times over a short period of time is likely to work, however it cannot be guarenteed.
         * Unreliable data should be cached if it is needed frequently, or if it is needed a while after it is created.
         */
        public const val UNRELIABLE_MASK: Int = 0b00001000

        /**
         * The source is unstable; no part of this source can be guaranteed beyond the first use.
         * Unstable data may come from a variety of sources, but most commonly it will come from network or nested data, or from a source that is non-deterministic.
         * Unstable data should be cached if it is needed more than once.
         */
        public const val UNSTABLE_MASK: Int = 0b00010000

        /**
         * The data is randomly accessible; it supports the seek operand.
         * Data that is randomly accessible tends to come from static sources, and tends to be at odds with other sources.
         * Randomly accessible data is unlikely to need to be cached, however data that is *not* randomly accessible may need to be.
         */
        public const val RANDOM_ACCESS_MASK: Int = 0b00100000

        public fun static(): DataSourceReproducibility =
            DataSourceReproducibility(STATIC_MASK)

        public fun deterministic(): DataSourceReproducibility =
            DataSourceReproducibility(DETERMINISTIC_MASK)

        public fun expensive(): DataSourceReproducibility =
            DataSourceReproducibility(EXPENSIVE_MASK)

        public fun unreliable(): DataSourceReproducibility =
            DataSourceReproducibility(UNRELIABLE_MASK)

        public fun unstable(): DataSourceReproducibility =
            DataSourceReproducibility(UNSTABLE_MASK)

        public fun randomAccess(): DataSourceReproducibility =
            DataSourceReproducibility(RANDOM_ACCESS_MASK)
    }

    public infix fun or(other: Number): DataSourceReproducibility =
        DataSourceReproducibility((flag.toInt() or other.toInt()).toByte())

    public infix fun and(other: Number): DataSourceReproducibility =
        DataSourceReproducibility((flag.toInt() and other.toInt()).toByte())

    public infix fun xor(other: Number): DataSourceReproducibility =
        DataSourceReproducibility((flag.toInt() xor other.toInt()).toByte())

    public infix fun has(mask: Number): Boolean = flag.toInt() and mask.toInt() == mask.toInt()

    public fun isStatic(): Boolean = has(STATIC_MASK)
    public fun isDeterministic(): Boolean = has(DETERMINISTIC_MASK)
    public fun isExpensive(): Boolean = has(EXPENSIVE_MASK)
    public fun isUnreliable(): Boolean = has(UNRELIABLE_MASK)
    public fun isUnstable(): Boolean = has(UNSTABLE_MASK)
    public fun isRandomAccess(): Boolean = has(RANDOM_ACCESS_MASK)
}