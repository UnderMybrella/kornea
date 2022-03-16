package dev.brella.kornea.io.coroutine.flow

import dev.brella.kornea.base.common.use
import dev.brella.kornea.io.common.flow.FlowReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.isActive

//Removed noinline from operation. If the compiler starts crashing, add it back
@OptIn(ExperimentalCoroutinesApi::class)
public suspend inline fun <T> FlowReader.useLines(scope: CoroutineScope, operation: (ReceiveChannel<String>) -> T): T = use { reader ->
    operation(scope.produce {
        while (isActive) {
            send(reader.readLine() ?: break)
        }

        close()
    })
}