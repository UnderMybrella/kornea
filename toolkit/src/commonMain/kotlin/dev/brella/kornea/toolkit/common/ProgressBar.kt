package dev.brella.kornea.toolkit.common

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.receiveOrNull
import kotlin.coroutines.CoroutineContext
import kotlin.math.max
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

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

@ExperimentalTime
public abstract class ChannelBasedProgressBar(
    jobCoroutineScope: CoroutineScope,
    jobCoroutineContext: CoroutineContext,
    protected val updateInterval: Duration,
    protected val shouldUpdateOnEmpty: Boolean = false
) : ProgressBar, SuspendInit0 {
    protected abstract val channel: Channel<Long>
    protected val job: Job = jobCoroutineScope.launch(jobCoroutineContext, CoroutineStart.LAZY, ::progressJob)
    protected var _current: Long? = null
    override val lastProgressValue: Long? by ::_current
    protected var _completed: Boolean = false
    override val isCompleted: Boolean by ::_completed

    override suspend fun trackProgress(current: Number) {
        channel.send(current.toLong())
    }

    override suspend fun complete() {
        if (!_completed) {
            _completed = true
            channel.close()
            job.cancelAndJoin()

            whenCompleted()
        }
    }

    protected abstract suspend fun update(current: Long)
    protected abstract suspend fun whenCompleted()

    override suspend fun init() {
        job.start()
    }

    private suspend fun progressJob(scope: CoroutineScope) = with(scope) {
        if (shouldUpdateOnEmpty) {
            var mark = TimeSource.Monotonic.markNow()
            while (isActive && !channel.isClosedForReceive) {
                yield()
                if (_current == null) {
                    _current = channel.poll()

                    update(_current ?: 0)
                } else {
                    val polled = channel.poll()
                    if (polled != null)
                        _current = max(_current!!, polled)

                    update(_current!!)
                }

                delay(updateInterval - mark.elapsedNow())
                mark = TimeSource.Monotonic.markNow()
            }
        } else {
            var mark = TimeSource.Monotonic.markNow()
            while (isActive && !channel.isClosedForReceive) {
                yield()
                _current = max(_current ?: 0L, channel.poll() ?: continue)

                update(_current!!)

                delay(updateInterval - mark.elapsedNow())
                mark = TimeSource.Monotonic.markNow()
            }
        }

        channel.close()
        close()
    }
}