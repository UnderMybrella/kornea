package dev.brella.kornea.toolkit.common

public interface ProgressBar : DataCloseable {
    /**
     * Get the last progress value as recorded by [trackProgress]
     * Note that this may or may not be perfectly synchronised
     */
    public val lastProgressValue: Long?
    public val progressLimit: Long
    public val isCompleted: Boolean

    public suspend fun trackProgress(current: Number)
    public suspend fun complete()

    override val isClosed: Boolean
        get() = isCompleted

    override suspend fun close(): Unit = complete()
}