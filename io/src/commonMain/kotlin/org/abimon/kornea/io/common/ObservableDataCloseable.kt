package org.abimon.kornea.io.common

import org.abimon.kornea.annotations.WrongBytecodeGenerated

@ExperimentalUnsignedTypes
typealias DataCloseableEventHandler = suspend (closeable: ObservableDataCloseable) -> Unit

interface DataCloseable {
    suspend fun close()
}

@ExperimentalUnsignedTypes
interface ObservableDataCloseable: DataCloseable {
    val closeHandlers: MutableList<DataCloseableEventHandler>
    val isClosed: Boolean

    override suspend fun close() {
        if (!isClosed) {
            closeHandlers.forEach { closed -> closed(this) }
        }
    }
}

@ExperimentalUnsignedTypes
fun ObservableDataCloseable.addCloseHandler(handler: DataCloseableEventHandler) {
    closeHandlers.add(handler)
}

@ExperimentalUnsignedTypes
@WrongBytecodeGenerated(WrongBytecodeGenerated.STACK_SHOULD_BE_SPILLED, ReplaceWith("useBlockCrossinline(t, block)", "org.abimon.kornea.io.common.useBlockCrossinline"))
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
@WrongBytecodeGenerated(WrongBytecodeGenerated.STACK_SHOULD_BE_SPILLED, ReplaceWith("useCrossinline(t, block)", "org.abimon.kornea.io.common.useCrossinline"))
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
public suspend inline fun <T : DataCloseable?, R> use(t: T, crossinline block: suspend () -> R): R {
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

@WrongBytecodeGenerated(WrongBytecodeGenerated.STACK_SHOULD_BE_SPILLED, ReplaceWith("use(t, block)", "org.abimon.kornea.io.common.use"))
public suspend inline fun <T : DataCloseable?, R> useInline(t: T, block: () -> R): R {
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

suspend fun <T: DataCloseable> Array<T>.closeAll() = forEach { data -> data.close() }