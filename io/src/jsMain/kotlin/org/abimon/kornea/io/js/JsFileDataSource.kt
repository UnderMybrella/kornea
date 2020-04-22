package org.abimon.kornea.io.js

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.promise
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.abimon.kornea.erorrs.common.KorneaResult
import org.abimon.kornea.io.common.*
import org.abimon.kornea.io.common.flow.BinaryInputFlow
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.w3c.files.File
import org.w3c.files.FileReader
import kotlin.js.Promise
import kotlin.math.max

@ExperimentalUnsignedTypes
class JsFileDataSource(val file: File, val maxInstanceCount: Int = -1, override val location: String? = file.name) : DataSource<BinaryInputFlow> {
    companion object {
        fun async(file: File, maxInstanceCount: Int = -1, location: String? = file.name): Promise<JsFileDataSource> = GlobalScope.promise { JsFileDataSource(file, maxInstanceCount, location) }
    }

    private var data: ByteArray? = null
    private val dataMutex: Mutex = Mutex()
    private val dataPromise: Promise<ArrayBuffer?>
    override val dataSize: ULong?
        get() = data?.size?.toULong()

    override val reproducibility: DataSourceReproducibility = DataSourceReproducibility(isStatic = true, isRandomAccess = true)
    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()

    private val openInstances: MutableList<BinaryInputFlow> = ArrayList(max(maxInstanceCount, 0))
    private var closed: Boolean = false
    override val isClosed: Boolean
        get() = closed

    override suspend fun openNamedInputFlow(location: String?): KorneaResult<BinaryInputFlow> {
        waitIfNeeded()

        when {
            closed -> return KorneaResult.Error(DataSource.ERRORS_SOURCE_CLOSED, "Instance closed")
            canOpenInputFlow() -> {
                val stream = BinaryInputFlow(data!!, location = location ?: this.location)
                stream.addCloseHandler(this::instanceClosed)
                openInstances.add(stream)
                return KorneaResult.Success(stream)
            }
            else -> return KorneaResult.Error(
                DataSource.ERRORS_TOO_MANY_SOURCES_OPEN,
                "Too many instances open (${openInstances.size}/${maxInstanceCount})"
            )
        }
    }
    override suspend fun canOpenInputFlow(): Boolean {
        waitIfNeeded()

        return !closed && data != null && (maxInstanceCount == -1 || openInstances.size < maxInstanceCount)
    }

    private suspend fun instanceClosed(closeable: DataCloseable) {
        if (closeable is BinaryInputFlow) {
            openInstances.remove(closeable)
        }
    }

    override suspend fun close() {
        super.close()

        if (!closed) {
            closed = true
            openInstances.toTypedArray().closeAll()
            openInstances.clear()
        }
    }

    private suspend fun waitIfNeeded() {
        dataMutex.withLock {
            if (!closed && data == null) {
                val buffer = dataPromise.await()

                if (buffer == null) {
                    close()
                } else {
                    data = Int8Array(buffer).asDynamic() as ByteArray
                }
            }
        }
    }

    init {
        dataPromise = Promise { resolve: (ArrayBuffer?) -> Unit, _: (Throwable) -> Unit ->
            val reader = FileReader()
            reader.onloadend = { _ -> resolve(reader.result as? ArrayBuffer) }
            reader.readAsArrayBuffer(file)
        }
    }
}