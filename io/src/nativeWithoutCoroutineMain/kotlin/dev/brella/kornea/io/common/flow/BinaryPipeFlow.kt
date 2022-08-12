package dev.brella.kornea.io.common.flow

import dev.brella.kornea.io.common.native.flow.NativeListBackedBinaryPipeFlow

public actual operator fun BinaryPipeFlow.Companion.invoke(): BinaryPipeFlow = NativeListBackedBinaryPipeFlow()