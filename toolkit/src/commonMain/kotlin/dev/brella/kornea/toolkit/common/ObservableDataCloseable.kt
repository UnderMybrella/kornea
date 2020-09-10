package dev.brella.kornea.toolkit.common

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.annotations.WrongBytecodeGenerated
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.errors.common.asType

@ExperimentalUnsignedTypes
@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public typealias DataCloseableEventHandler = suspend (closeable: ObservableDataCloseable) -> Unit

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public interface DataCloseable {
    public val isClosed: Boolean

    public suspend fun close()
}

@ExperimentalUnsignedTypes
@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public interface ObservableDataCloseable: DataCloseable {
    public val closeHandlers: List<DataCloseableEventHandler>

    public suspend fun registerCloseHandler(handler: DataCloseableEventHandler): Boolean

    override suspend fun close() {
        if (!isClosed) {
            closeHandlers.forEach { closed -> closed(this) }
        }
    }
}

@ExperimentalUnsignedTypes
@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public suspend fun <D: ObservableDataCloseable> D.withCloseHandler(handler: DataCloseableEventHandler): D {
    registerCloseHandler(handler)

    return this
}

@ExperimentalUnsignedTypes
@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
@WrongBytecodeGenerated(WrongBytecodeGenerated.STACK_SHOULD_BE_SPILLED, ReplaceWith("useBlockCrossinline(t, block)", "dev.brella.kornea.toolkit.common.useBlockCrossinline"))
public suspend inline fun <T : DataCloseable?, R> T.use(block: (T) -> R): R {
    var exception: Throwable? = null
    try {
        return block(this)
    } catch (e: Throwable) {
        exception = e
        throw e
    } finally {
        this.closeFinally(exception)
    }
}

@ExperimentalUnsignedTypes
@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public suspend inline fun <T : DataCloseable?, R> T.useCrossinline(crossinline block: suspend (T) -> R): R {
    var exception: Throwable? = null
    try {
        return block(this)
    } catch (e: Throwable) {
        exception = e
        throw e
    } finally {
        this.closeFinally(exception)
    }
}

@ExperimentalUnsignedTypes
@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
@WrongBytecodeGenerated(WrongBytecodeGenerated.STACK_SHOULD_BE_SPILLED, ReplaceWith("useCrossinline(t, block)", "dev.brella.kornea.toolkit.common.useCrossinline"))
public suspend inline fun <T : DataCloseable?, R> T.useSuspending(@Suppress("REDUNDANT_INLINE_SUSPEND_FUNCTION_TYPE") block: suspend (T) -> R): R {
    var exception: Throwable? = null
    try {
        return block(this)
    } catch (e: Throwable) {
        exception = e
        throw e
    } finally {
        this.closeFinally(exception)
    }
}

@ExperimentalUnsignedTypes
@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public suspend inline fun <T : DataCloseable?, R> T.useBlockCrossinline(crossinline block: (T) -> R): R {
    var exception: Throwable? = null
    try {
        return block(this)
    } catch (e: Throwable) {
        exception = e
        throw e
    } finally {
        this.closeFinally(exception)
    }
}

//@ExperimentalContracts
/**
 * Close the provided variable [t] after [block] finishes running
 *
 * NOTE: Currently, if [block] is *not* marked with crossinline, Kotlin may crash with an internal compiler error.
 */
@ExperimentalUnsignedTypes
@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public suspend inline fun <T : DataCloseable?, R> closeAfter(t: T, crossinline block: suspend () -> R): R {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//    }

    var exception: Throwable? = null
    try {
        return block()
    } catch (e: Throwable) {
        exception = e
        throw e
    } finally {
        t.closeFinally(exception)
    }
}

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
@WrongBytecodeGenerated(WrongBytecodeGenerated.STACK_SHOULD_BE_SPILLED, ReplaceWith("use(t, block)", "dev.brella.kornea.toolkit.common.use"))
public suspend inline fun <T : DataCloseable?, R> closeAfterInline(t: T, block: () -> R): R {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//    }

    var exception: Throwable? = null
    try {
        return block()
    } catch (e: Throwable) {
        exception = e
        throw e
    } finally {
        t.closeFinally(exception)
    }
}

@ExperimentalUnsignedTypes
@PublishedApi
@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
internal suspend fun DataCloseable?.closeFinally(cause: Throwable?) = when {
    this == null -> {
    }
    cause == null -> close()
    else ->
        try {
            close()
        } catch (closeException: Throwable) {
            //cause.addSuppressed(closeException)
        }
}

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public suspend fun <T: DataCloseable> Array<T>.closeAll(): Unit = toList().forEach { data -> data.close() }
@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public suspend fun <T: DataCloseable> Iterable<T>.closeAll(): Unit = toList().forEach { data -> data.close() }

@ExperimentalUnsignedTypes
@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public suspend inline fun <T : DataCloseable, reified R> KorneaResult<T>.useAndMap(block: (T) -> R): KorneaResult<R> =
    when (this) {
        is KorneaResult.Success<T> -> mapValue(get().use(block))
        is KorneaResult.Failure -> asType()
        else -> throw IllegalStateException(KorneaResult.dirtyImplementationString(this))
    }

@ExperimentalUnsignedTypes
@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)

public suspend inline fun <T : DataCloseable, reified R> KorneaResult<T>.useAndFlatMap(block: (T) -> KorneaResult<R>): KorneaResult<R> =
    when (this) {
        is KorneaResult.Success<T> -> get().use(block)
        is KorneaResult.Failure -> asType()
        else -> throw IllegalStateException(KorneaResult.dirtyImplementationString(this))
    }