package dev.brella.kornea.io.common

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.errors.common.asType
import dev.brella.kornea.errors.common.flatMap
import dev.brella.kornea.errors.common.map
import dev.brella.kornea.io.common.flow.InputFlow
import dev.brella.kornea.io.common.flow.SeekableInputFlow
import dev.brella.kornea.io.common.flow.bookmark

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_4_1_0_INDEV)
public suspend inline fun <T : DataCloseable, reified R> KorneaResult<T>.useAndMap(block: (T) -> R): KorneaResult<R> =
    when (this) {
        is KorneaResult.Success<T> -> mapValue(get().use(block))
        is KorneaResult.Failure -> asType()
        else -> throw IllegalStateException(KorneaResult.dirtyImplementationString(this))
    }

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_4_1_0_INDEV)
public suspend inline fun <T : DataCloseable, reified R> KorneaResult<T>.useAndFlatMap(block: (T) -> KorneaResult<R>): KorneaResult<R> =
    when (this) {
        is KorneaResult.Success<T> -> get().use(block)
        is KorneaResult.Failure -> asType()
        else -> throw IllegalStateException(KorneaResult.dirtyImplementationString(this))
    }

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_4_1_0_INDEV)
public suspend inline fun <T : InputFlow, reified R> KorneaResult<DataSource<T>>.useAndMapInputFlow(block: (T) -> R): KorneaResult<R> =
    when (this) {
        is KorneaResult.Success<DataSource<T>> -> useAndFlatMap { source -> source.openInputFlow().map(block) }
        is KorneaResult.Failure -> asType()
        else -> throw IllegalStateException(KorneaResult.dirtyImplementationString(this))
    }

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_4_1_0_INDEV)
public suspend inline fun <T : InputFlow, reified R> KorneaResult<DataSource<T>>.useAndFlatMapInputFlow(block: (T) -> KorneaResult<R>): KorneaResult<R> =
    when (this) {
        is KorneaResult.Success<DataSource<T>> -> useAndFlatMap { source -> source.openInputFlow().flatMap(block) }
        is KorneaResult.Failure -> asType()
        else -> throw IllegalStateException(KorneaResult.dirtyImplementationString(this))
    }

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_4_1_0_INDEV)
public suspend inline fun <T : InputFlow, reified R> DataSource<T>.useInputFlowForResult(block: (T) -> KorneaResult<R>): KorneaResult<R> =
    openInputFlow().flatMap { flow -> flow.use(block) }

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_4_1_0_INDEV)
public suspend inline fun <F : InputFlow, reified T> F.fauxSeekFromStartForResult(offset: ULong, dataSource: DataSource<out F>, crossinline block: suspend (F) -> KorneaResult<T>): KorneaResult<T> {
    if (this is SeekableInputFlow) {
        return bookmark(this) {
            seek(offset.toLong(), EnumSeekMode.FROM_BEGINNING)
            block(this)
        }
    } else {
        return dataSource.openInputFlow().flatMap { flow ->
            closeAfter(flow) {
                flow.skip(offset)
                block(flow)
            }
        }
    }
}