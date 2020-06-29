package dev.brella.kornea.io.common.flow

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.receiveOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.w3c.dom.HTMLInputElement

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
public actual class StandardInputFlow(inputElement: HTMLInputElement, location: String? = inputElement.name) : InputFlow, ConflatingBufferedInputFlow(location) {
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