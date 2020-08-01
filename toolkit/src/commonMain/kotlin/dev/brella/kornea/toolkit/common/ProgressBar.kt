package dev.brella.kornea.toolkit.common

public interface ProgressBar {
    public fun trackProgress(current: Long, total: Long): Double
    public fun complete()
}