package dev.brella.kornea.io.common.flow

@Deprecated("Deprecating PipeFlow until further notice", level = DeprecationLevel.ERROR)
@Suppress("DEPRECATION_ERROR")
public actual operator fun BinaryPipeFlow.Companion.invoke(): BinaryPipeFlow = dev.brella.kornea.io.coroutine.flow.MutexListBackedBinaryPipeFlow()