package dev.brella.kornea.io.common.flow

import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.Uri
import dev.brella.kornea.io.coroutine.flow.ConflatingBufferedInputFlow
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.withLock
import org.w3c.dom.HTMLInputElement

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
public actual class StdinInputFlow(inputElement: HTMLInputElement, location: String? = inputElement.name) : InputFlow, ConflatingBufferedInputFlow(location) {
    override fun locationAsUri(): KorneaResult<Uri> = KorneaResult.empty()

    init {
        inputElement.onkeydown = { event ->
            if (event.keyCode == 13) {
                GlobalScope.launch {
                    mutex.withLock { channel.send(inputElement.value.encodeToByteArray()) }
                    inputElement.value = ""
                }
            }
        }
    }
}