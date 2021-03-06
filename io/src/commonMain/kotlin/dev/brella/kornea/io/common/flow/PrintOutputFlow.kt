package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.io.common.KorneaIO
import dev.brella.kornea.toolkit.common.PrintFlow

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_4_2_0_INDEV)
public interface PrintOutputFlow: PrintFlow, OutputFlow {
    override suspend fun print(value: Char): PrintOutputFlow {
        write(value.toInt())
        return this
    }

    @ExperimentalStdlibApi
    override suspend fun print(value: CharSequence?): PrintOutputFlow {
        write(value.toString().encodeToByteArray())
        return this
    }

    @ExperimentalStdlibApi
    override suspend fun print(value: CharSequence?, startIndex: Int, endIndex: Int): PrintOutputFlow {
        write((value?.subSequence(startIndex, endIndex).toString()).encodeToByteArray())
        return this
    }
}