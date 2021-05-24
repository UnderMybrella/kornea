package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.base.common.DataCloseableEventHandler
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.errors.common.switchIfEmpty
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.EnumSeekMode
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.Uri

@AvailableSince(KorneaIO.VERSION_3_2_0_ALPHA)
public interface SeekablePipeFlow<I : SeekableInputFlow, O : SeekableOutputFlow> : InputFlow, OutputFlowByDelegate<O> {
    public companion object {
        public inline operator fun <I : SeekableInputFlow, O : SeekableOutputFlow> invoke(input: I, output: O): SeekablePipeFlow<I, O> =
            Sink(input, output)
    }

    public data class Sink<I : SeekableInputFlow, O : SeekableOutputFlow>(override val input: I, override val output: O, val uri: Uri? = null) : SeekablePipeFlow<I, O>,
        BaseDataCloseable(), SeekableInputFlow by input, SeekableOutputFlow by output {
        override val isClosed: Boolean
            get() = super<BaseDataCloseable>.isClosed

        override val closeHandlers: List<DataCloseableEventHandler>
            get() = super<BaseDataCloseable>.closeHandlers

        override suspend fun close() {
            super<BaseDataCloseable>.close()
        }

        override suspend fun seek(pos: Long, mode: EnumSeekMode): ULong {
            val result = output.seek(pos, mode)
            input.seek(pos, mode)
            return result
        }

        override suspend fun registerCloseHandler(handler: DataCloseableEventHandler): Boolean {
            return super<BaseDataCloseable>.registerCloseHandler(handler)
        }

        override fun locationAsUri(): KorneaResult<Uri> =
            KorneaResult.successOrEmpty(uri, null)
                .switchIfEmpty { input.locationAsUri() }
                .switchIfEmpty { output.locationAsUri() }
    }

    public val input: I
    public override val output: O
}