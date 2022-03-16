package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.base.common.DataCloseableEventHandler
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.errors.common.switchIfEmpty
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.Uri

@AvailableSince(KorneaIO.VERSION_3_2_0_ALPHA)
public interface PipeFlow<I : InputFlow, O : OutputFlow> : InputFlow, OutputFlowByDelegate<O> {
    public companion object {
        public inline operator fun <I : InputFlow, O : OutputFlow> invoke(input: I, output: O): PipeFlow<I, O> =
            Sink(input, output)
    }

    public data class Sink<I : InputFlow, O : OutputFlow>(
        override val input: I,
        override val output: O,
        val uri: Uri? = null
    ) : PipeFlow<I, O>, BaseDataCloseable(), InputFlow by input, OutputFlow by output {
        override val isClosed: Boolean
            get() = super.isClosed

        override val closeHandlers: List<DataCloseableEventHandler>
            get() = super.closeHandlers

        override suspend fun close() {
            super<BaseDataCloseable>.close()
        }

        override suspend fun registerCloseHandler(handler: DataCloseableEventHandler): Boolean {
            return super.registerCloseHandler(handler)
        }

        override fun locationAsUri(): KorneaResult<Uri> =
            KorneaResult.successOrEmpty(uri)
                .switchIfEmpty { input.locationAsUri() }
                .switchIfEmpty { output.locationAsUri() }
    }

    public val input: I
    public override val output: O
}