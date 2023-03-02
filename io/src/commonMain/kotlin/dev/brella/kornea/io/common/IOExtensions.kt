package dev.brella.kornea.io.common

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.base.common.closeAfter
import dev.brella.kornea.base.common.use
import dev.brella.kornea.errors.common.*
import dev.brella.kornea.io.common.flow.InputFlow
import dev.brella.kornea.io.common.flow.bookmark
import dev.brella.kornea.io.common.flow.flatSeekable

@AvailableSince(KorneaIO.VERSION_4_1_0_INDEV)
public suspend inline fun <T : InputFlow, reified R> KorneaResult<DataSource<T>>.useAndMapInputFlow(block: (T) -> R): KorneaResult<R> =
    useAndFlatMap { source -> source.openInputFlow().map(block) }

@AvailableSince(KorneaIO.VERSION_4_1_0_INDEV)
public suspend inline fun <T : InputFlow, reified R> KorneaResult<DataSource<T>>.useAndFlatMapInputFlow(block: (T) -> KorneaResult<R>): KorneaResult<R> =
    useAndFlatMap { source -> source.openInputFlow().flatMap(block) }

@AvailableSince(KorneaIO.VERSION_4_1_0_INDEV)
public suspend inline fun <T : InputFlow, reified R> DataSource<T>.useInputFlowForResult(block: (T) -> KorneaResult<R>): KorneaResult<R> =
    openInputFlow().flatMap { flow -> flow.use(block) }

@AvailableSince(KorneaIO.VERSION_4_1_0_INDEV)
public suspend inline fun <F : InputFlow, reified T> F.fauxSeekFromStartForResult(
    offset: ULong, dataSource: DataSource<F>, crossinline block: suspend (F) -> KorneaResult<T>
): KorneaResult<T> =
    flatSeekable {
        bookmark(offset.toLong(), EnumSeekMode.FROM_BEGINNING) {
            block(this@fauxSeekFromStartForResult)
        }
    }.switchIfEmpty {
        dataSource.openInputFlow().flatMap { flow ->
            closeAfter(flow) {
                flow.skip(offset)
                block(flow)
            }
        }
    }