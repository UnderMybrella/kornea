package dev.brella.kornea.io.jvm.files

import dev.brella.kornea.annotations.DangerousApiUsage
import dev.brella.kornea.errors.common.runOrNull
import java.io.FileDescriptor
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.nio.channels.AsynchronousFileChannel
import java.util.concurrent.ExecutorService

public object FileSystemProviderUtils {
    @JvmStatic
    private val METHOD_LOOKUP = MethodHandles.lookup()

    @JvmStatic
    private val THREAD_POOL_CLASS = runOrNull { Class.forName("sun.nio.ch.ThreadPool") }

    @JvmStatic
    private val NEW_WINDOWS_ASYNCHRONOUS_CHANNEL = runOrNull {
        METHOD_LOOKUP.findStatic(
            Class.forName("sun.nio.ch.WindowsAsynchronousFileChannelImpl"),
            "open",
            MethodType.methodType(
                AsynchronousFileChannel::class.java,
                FileDescriptor::class.java,
                Boolean::class.javaPrimitiveType,
                Boolean::class.javaPrimitiveType,
                THREAD_POOL_CLASS
            )
        )
    }

    @JvmStatic
    private val NEW_SIMPLE_ASYNCHRONOUS_CHANNEL = runOrNull {
        METHOD_LOOKUP.findStatic(
            Class.forName("sun.nio.ch.SimpleAsynchronousFileChannelImpl"),
            "open",
            MethodType.methodType(
                AsynchronousFileChannel::class.java,
                FileDescriptor::class.java,
                Boolean::class.javaPrimitiveType,
                Boolean::class.javaPrimitiveType,
                THREAD_POOL_CLASS
            )
        )
    }

    @JvmStatic
    private val THREAD_POOL_WRAP = runOrNull {
        METHOD_LOOKUP.findStatic(
            THREAD_POOL_CLASS,
            "wrap",
            MethodType.methodType(
                THREAD_POOL_CLASS,
                ExecutorService::class.java,
                Int::class.javaPrimitiveType
            )
        )
    }

    /**
     * Open an asynchronous file channel using a file descriptor
     * WARNING: This file uses Sun Apis, and is likely to change without warning
     */
    @DangerousApiUsage("Sun API usage")
    public fun newAsynchronousFileChannel(
        fd: FileDescriptor,
        read: Boolean,
        write: Boolean,
        executor: ExecutorService?
    ): AsynchronousFileChannel? {
        val pool = if (executor == null) null else THREAD_POOL_WRAP?.invokeExact(executor, 0)

        return NEW_WINDOWS_ASYNCHRONOUS_CHANNEL?.invokeExact(fd, read, write, pool) as? AsynchronousFileChannel
            ?: NEW_SIMPLE_ASYNCHRONOUS_CHANNEL?.invokeExact(fd, read, write, pool) as? AsynchronousFileChannel
    }
}