package dev.brella.kornea.io.jvm.files

import dev.brella.kornea.annotations.BlockingOperation
import dev.brella.kornea.io.common.flow.readResultIsValid
import java.io.File
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.channels.CompletionHandler
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.concurrent.ExecutorService
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Get the relative path to this file, from parent directory [to]
 * Path will start with the name of [to]
 * @see [relativePathFrom]
 */
public infix fun File.relativePathTo(to: File): String = to.name + absolutePath.replace(to.absolutePath, "")

/**
 * Get the relative path from parent directory [to] to this file
 * Path will ***not*** start with the name of [to]
 * @see [relativePathTo]
 */
public infix fun File.relativePathFrom(to: File): String = absolutePath.replace(to.absolutePath + File.separator, "")

public fun File.existingDirectory(): Boolean = isDirectory && exists()

public fun File.ensureFileExists(): File {
    if (!exists()) createNewFile()
    return this
}

public fun File.ensureDirectoryExists(): File {
    if (!exists()) mkdirs()
    return this
}

public val File.absoluteParentFile: File?
    get(): File? = absoluteFile.parentFile

/** JDK 7 Compat */
public class ContinuationCompletionHandler<V> : CompletionHandler<V, Continuation<V>> {
    override fun completed(result: V, attachment: Continuation<V>) {
        attachment.resume(result)
    }

    override fun failed(exc: Throwable, attachment: Continuation<V>) {
        attachment.resumeWithException(exc)
    }
}

public class NullableContinuationCompletionHandler<V> : CompletionHandler<V, Continuation<V?>> {
    override fun completed(result: V, attachment: Continuation<V?>) {
        attachment.resume(result)
    }

    override fun failed(exc: Throwable, attachment: Continuation<V?>) {
        attachment.resume(null)
    }
}

public val INT_CONTINUATION_COMPLETION_HANDLER: ContinuationCompletionHandler<Int> =
    ContinuationCompletionHandler()
public val INT_NULLABLE_CONTINUATION_COMPLETION_HANDLER: NullableContinuationCompletionHandler<Int> =
    NullableContinuationCompletionHandler()

public suspend fun AsynchronousFileChannel.readAwait(dst: ByteBuffer, position: Long): Int =
    suspendCoroutine<Int> { cont ->
        read(
            dst, position, cont,
            INT_CONTINUATION_COMPLETION_HANDLER
        )
    }

public suspend fun AsynchronousFileChannel.readAwaitOrNull(dst: ByteBuffer, position: Long): Int? =
    suspendCoroutine<Int> { cont ->
        read(
            dst, position, cont,
            INT_CONTINUATION_COMPLETION_HANDLER
        )
    }.takeIf(::readResultIsValid)

public suspend fun AsynchronousFileChannel.writeAwait(src: ByteBuffer, position: Long): Int =
    suspendCoroutine { cont ->
        write(
            src, position, cont,
            INT_CONTINUATION_COMPLETION_HANDLER
        )
    }

public suspend fun AsynchronousFileChannel.writeAwaitOrNull(src: ByteBuffer, position: Long): Int? =
    suspendCoroutine<Int> { cont ->
        write(
            src, position, cont,
            INT_CONTINUATION_COMPLETION_HANDLER
        )
    }
        .takeIf(::readResultIsValid)

@BlockingOperation
public fun openAsynchronousFileChannel(
    path: Path,
    executor: ExecutorService? = null,
    read: Boolean,
    write: Boolean,
    append: Boolean,
    truncate: Boolean,
    create: Boolean,
    createNew: Boolean,
    deleteOnClose: Boolean,
    sparse: Boolean,
    sync: Boolean,
    dsync: Boolean
): AsynchronousFileChannel {
    val set = EnumSet.noneOf(StandardOpenOption::class.java)

    if (read) set.add(StandardOpenOption.READ)
    if (write) set.add(StandardOpenOption.WRITE)
    if (append) set.add(StandardOpenOption.APPEND)
    if (truncate) set.add(StandardOpenOption.TRUNCATE_EXISTING)
    if (create) set.add(StandardOpenOption.CREATE)
    if (createNew) set.add(StandardOpenOption.CREATE_NEW)
    if (deleteOnClose) set.add(StandardOpenOption.DELETE_ON_CLOSE)
    if (sparse) set.add(StandardOpenOption.SPARSE)
    if (sync) set.add(StandardOpenOption.SYNC)
    if (dsync) set.add(StandardOpenOption.DSYNC)

    return AsynchronousFileChannel.open(
        path,
        set,
        executor
    )
}