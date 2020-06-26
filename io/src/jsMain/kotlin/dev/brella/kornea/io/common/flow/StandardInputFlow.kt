package dev.brella.kornea.io.common.flow

import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.BinaryDataPool
import dev.brella.kornea.io.common.DataCloseableEventHandler
import dev.brella.kornea.io.common.DataPool
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLInputElement

@ExperimentalUnsignedTypes
public actual class StandardInputFlow(private val bridgeOut: PrintOutputFlow, private val bridgeIn: InputFlow, inputElement: HTMLInputElement, override val location: String? = inputElement.name) : BaseDataCloseable(), InputFlow by bridgeIn {
    public companion object {
        public suspend operator fun invoke(inputElement: HTMLInputElement, location: String? = inputElement.name): StandardInputFlow = invoke(
            BinaryDataPool(), inputElement, location)
        public suspend operator fun invoke(pool: DataPool<*, PrintOutputFlow>, inputElement: HTMLInputElement, location: String? = inputElement.name): StandardInputFlow {
            val outFlow = pool.openOutputFlow().get()
            val inFlow = pool.openInputFlow().get()

            return StandardInputFlow(outFlow, inFlow, inputElement, location)
        }

    }

    override suspend fun close() {
        super<BaseDataCloseable>.close()
    }

    override val closeHandlers: List<DataCloseableEventHandler>
        get() = super.closeHandlers
    override val isClosed: Boolean
        get() = super.isClosed

    override suspend fun registerCloseHandler(handler: DataCloseableEventHandler): Boolean {
        return super.registerCloseHandler(handler)
    }

    init {
        inputElement.onkeydown = { event ->
            if (event.keyCode == 13) {
                GlobalScope.launch {
                    bridgeOut.printLine(inputElement.value)
                    inputElement.value = ""
                }
            }
        }
    }
}