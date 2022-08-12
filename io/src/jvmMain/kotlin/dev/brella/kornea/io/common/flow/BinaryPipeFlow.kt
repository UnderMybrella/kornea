package dev.brella.kornea.io.common.flow

import dev.brella.kornea.io.coroutine.flow.MutexListBackedBinaryPipeFlow


public actual operator fun BinaryPipeFlow.Companion.invoke(): BinaryPipeFlow = MutexListBackedBinaryPipeFlow()