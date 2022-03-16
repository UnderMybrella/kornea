package dev.brella.kornea.office

import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.errors.common.korneaNotEnoughData
import dev.brella.kornea.errors.common.useAndFlatMap
import dev.brella.kornea.io.common.DataSource
import dev.brella.kornea.io.common.flow.extensions.readUInt64BE

class CompoundBinaryFile {
    companion object {
        public const val HEADER_SIGNATURE = 0xD0CF11E0uL

        suspend operator fun invoke(source: DataSource<*>): KorneaResult<Unit> =
            source.openInputFlow().useAndFlatMap { flow ->
                val headerSignature = flow.readUInt64BE() ?: return@useAndFlatMap korneaNotEnoughData()

                if (headerSignature != HEADER_SIGNATURE) return@useAndFlatMap KorneaResult.errorAsIllegalState(0, "Header signature is invalid")



                KorneaResult.empty()
            }
    }
}