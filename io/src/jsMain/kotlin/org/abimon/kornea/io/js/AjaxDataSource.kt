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
import org.w3c.xhr.ARRAYBUFFER
import org.w3c.xhr.XMLHttpRequest
import org.w3c.xhr.XMLHttpRequestResponseType
import kotlin.js.Promise
import kotlin.math.max

@ExperimentalUnsignedTypes
class AjaxDataSource (val url: String, val maxInstanceCount: Int = -1, override val location: String? = url) : DataSource<BinaryInputFlow> {
    companion object {
        fun async(url: String, maxInstanceCount: Int = -1, location: String? = url): Promise<AjaxDataSource> = GlobalScope.promise { AjaxDataSource(url, maxInstanceCount, location) }
    }

    private var data: ByteArray? = null
    private val dataMutex: Mutex = Mutex()
    private val dataPromise: Promise<ArrayBuffer?>
    override val dataSize: ULong?
        get() = data?.size?.toULong()
    override val reproducibility: DataSourceReproducibility = DataSourceReproducibility(isStatic = true, isRandomAccess = true)

    private val openInstances: MutableList<BinaryInputFlow> = ArrayList(max(maxInstanceCount, 0))
    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()
    private var closed: Boolean = false
    override val isClosed: Boolean
        get() = closed

    override suspend fun openNamedInputFlow(location: String?): KorneaResult<BinaryInputFlow> {
        waitIfNeeded()

        when {
            closed -> return KorneaResult.Failure(DataSource.ERRORS_SOURCE_CLOSED, "Instance closed")
            canOpenInputFlow() -> {
                val stream = BinaryInputFlow(data!!, location = location ?: this.location)
                stream.addCloseHandler(this::instanceClosed)
                openInstances.add(stream)
                return KorneaResult.Success(stream)
            }
            else -> return KorneaResult.Failure(
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
            val headRequest = XMLHttpRequest()
            headRequest.open("GET", url)
            headRequest.responseType = XMLHttpRequestResponseType.ARRAYBUFFER
            headRequest.onreadystatechange = { if (headRequest.readyState == XMLHttpRequest.DONE) resolve(if (headRequest.status.toInt() == 200) headRequest.response as ArrayBuffer else null) }
            headRequest.send()
        }
    }
}