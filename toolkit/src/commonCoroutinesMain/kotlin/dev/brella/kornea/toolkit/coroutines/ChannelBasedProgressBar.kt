package dev.brella.kornea.toolkit.coroutines

import dev.brella.kornea.toolkit.common.ProgressBar
import dev.brella.kornea.toolkit.common.SuspendInit0
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlin.coroutines.CoroutineContext
import kotlin.math.max
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

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

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun progressJob(scope: CoroutineScope) = with(scope) {
        if (shouldUpdateOnEmpty) {
            var mark = TimeSource.Monotonic.markNow()
            while (isActive && !channel.isClosedForReceive) {
                yield()
                if (_current == null) {
                    _current = channel.tryReceive().getOrNull()

                    update(_current ?: 0)
                } else {
                    val polled = channel.tryReceive().getOrNull()
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
                _current = max(_current ?: 0L, channel.tryReceive().getOrNull() ?: continue)

                update(_current!!)

                delay(updateInterval - mark.elapsedNow())
                mark = TimeSource.Monotonic.markNow()
            }
        }

        channel.close()
        close()
    }
}