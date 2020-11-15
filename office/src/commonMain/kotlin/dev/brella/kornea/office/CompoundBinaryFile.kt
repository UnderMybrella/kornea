package dev.brella.kornea.office

import dev.brella.kornea.errors.common.*
import dev.brella.kornea.io.common.*
import dev.brella.kornea.io.common.flow.extensions.readUInt64BE
import dev.brella.kornea.toolkit.common.useAndFlatMap

class CompoundBinaryFile {
    companion object {
        public const val HEADER_SIGNATURE = 0xD0CF11E0uL

        @ExperimentalUnsignedTypes
        suspend operator fun invoke(source: DataSource<*>): KorneaResult<Unit> =
            source.openInputFlow().useAndFlatMap { flow ->
                val headerSignature = flow.readUInt64BE() ?: return@useAndFlatMap korneaNotEnoughData()

                if (headerSignature != HEADER_SIGNATURE) return@useAndFlatMap KorneaResult.errorAsIllegalState(0, "Header signature is invalid")



                KorneaResult.empty()
            }
    }
}