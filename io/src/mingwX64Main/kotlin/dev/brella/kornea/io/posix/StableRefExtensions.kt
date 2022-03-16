package dev.brella.kornea.io.posix

import kotlinx.cinterop.StableRef

public suspend inline fun <T : Any, R> T.asStableReference(noinline block: suspend (StableRef<T>) -> R): R {
    val ref = StableRef.create(this)
    var exception: Throwable? = null
    try {
        return block(ref)
    } catch (e: Throwable) {
        exception = e
        throw e
    } finally {
        ref.closeFinally(exception)
    }
}

public suspend inline fun <T : StableRef<*>?, R> T.use(noinline block: suspend (T) -> R): R {
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

public suspend inline fun <T : StableRef<*>?, R> T.useBlock(block: (T) -> R): R {
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
public suspend inline fun <T : StableRef<*>?, R> use(t: T, block: () -> R): R {
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

@PublishedApi
internal suspend fun StableRef<*>?.closeFinally(cause: Throwable?) = when {
    this == null -> {
    }
    cause == null -> dispose()
    else ->
        try {
            dispose()
        } catch (closeException: Throwable) {
            //cause.addSuppressed(closeException)
        }
}

suspend fun <T : StableRef<*>> Array<T>.disposeAll() = forEach { data -> data.dispose() }