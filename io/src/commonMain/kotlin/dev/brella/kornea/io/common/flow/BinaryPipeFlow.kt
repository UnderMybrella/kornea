package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.ChangedSince
import dev.brella.kornea.annotations.ExperimentalKorneaToolkit
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.EnumSeekMode
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.io.common.Uri
import dev.brella.kornea.toolkit.common.asInt
import kotlin.math.min

@ChangedSince(KorneaIO.VERSION_5_0_0_ALPHA)
public interface BinaryPipeFlow
    : SeekablePipeFlow<BinaryPipeFlow, BinaryPipeFlow>,
    PeekableInputFlow, SeekableFlow, OutputFlow {
    public companion object {

    }

    public suspend fun getData(): ByteArray
    public suspend fun getDataSize(): ULong

    override fun locationAsUri(): KorneaResult<Uri> = KorneaResult.empty()
}

public expect operator fun BinaryPipeFlow.Companion.invoke(): BinaryPipeFlow