package dev.brella.kornea.io.common.flow

import dev.brella.kornea.io.common.native.flow.NativeListBackedBinaryPipeFlow

@Deprecated("Deprecating PipeFlow until further notice", level = DeprecationLevel.ERROR)
@Suppress("DEPRECATION_ERROR")
public actual operator fun BinaryPipeFlow.Companion.invoke(): BinaryPipeFlow = NativeListBackedBinaryPipeFlow()