package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.ChangedSince
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.Uri

@ChangedSince(KorneaIO.VERSION_5_0_0_ALPHA)
@Deprecated("Deprecating PipeFlow until further notice", level = DeprecationLevel.ERROR)
@Suppress("DEPRECATION_ERROR")
public interface BinaryPipeFlow
    : SeekablePipeFlow<BinaryPipeFlow, BinaryPipeFlow>,
    PeekableInputFlow, SeekableFlow, OutputFlow {
    public companion object {

    }

    public suspend fun getData(): ByteArray
    public suspend fun getDataSize(): ULong

    override fun locationAsUri(): KorneaResult<Uri> = KorneaResult.empty()
}

@Deprecated("Deprecating PipeFlow until further notice", level = DeprecationLevel.ERROR)
@Suppress("DEPRECATION_ERROR")
public expect operator fun BinaryPipeFlow.Companion.invoke(): BinaryPipeFlow