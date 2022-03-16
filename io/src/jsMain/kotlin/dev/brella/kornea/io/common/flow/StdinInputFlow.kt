package dev.brella.kornea.io.common.flow

import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.Uri
import dev.brella.kornea.io.coroutine.flow.ConflatingBufferedInputFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.withLock
import org.w3c.dom.HTMLInputElement

public actual class StdinInputFlow(inputElement: HTMLInputElement, location: String? = inputElement.name, private val scope: CoroutineScope) : InputFlow, ConflatingBufferedInputFlow(location) {
    override fun locationAsUri(): KorneaResult<Uri> = KorneaResult.empty()

    init {
        inputElement.onkeydown = { event ->
            if (event.keyCode == 13) {
                scope.launch {
                    mutex.withLock { channel.send(inputElement.value.encodeToByteArray()) }
                    inputElement.value = ""
                }
            }
        }
    }
}