package dev.brella.kornea.io.js

import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.DataSourceReproducibility
import dev.brella.kornea.io.common.LimitedInstanceDataSource
import dev.brella.kornea.io.common.Uri
import dev.brella.kornea.io.common.flow.BinaryInputFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.await
import kotlinx.coroutines.promise
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.w3c.xhr.ARRAYBUFFER
import org.w3c.xhr.XMLHttpRequest
import org.w3c.xhr.XMLHttpRequestResponseType
import kotlin.js.Promise

public class AjaxDataSource(
    public val url: String,
    override val maximumInstanceCount: Int? = null,
    override val location: String? = url
) : LimitedInstanceDataSource.Typed<BinaryInputFlow, AjaxDataSource>(withBareOpener(this::openBareLimitedInputFlow)) {
    public companion object {
        public fun openBareLimitedInputFlow(self: AjaxDataSource, location: String?): BinaryInputFlow =
            BinaryInputFlow(self.data!!, location = location ?: self.location)
    }

    private var data: ByteArray? = null
    private val dataMutex: Mutex = Mutex()
    override val dataSize: ULong?
        get() = data?.size?.toULong()
    override val reproducibility: DataSourceReproducibility =
        DataSourceReproducibility(isStatic = true, isRandomAccess = true)

    private val dataPromise: Promise<ArrayBuffer?> =
        Promise { resolve: (ArrayBuffer?) -> Unit, _: (Throwable) -> Unit ->
            val headRequest = XMLHttpRequest()
            headRequest.open("GET", url)
            headRequest.responseType = XMLHttpRequestResponseType.ARRAYBUFFER
            headRequest.onreadystatechange =
                { if (headRequest.readyState == XMLHttpRequest.DONE) resolve(if (headRequest.status.toInt() == 200) headRequest.response as ArrayBuffer else null) }
            headRequest.send()
        }

    override fun locationAsUri(): KorneaResult<Uri> =
        Uri.from(url)

    override suspend fun openNamedInputFlow(location: String?): KorneaResult<BinaryInputFlow> {
        waitIfNeeded()

        return super.openNamedInputFlow(location)
    }

    override suspend fun canOpenInputFlow(): Boolean {
        waitIfNeeded()

        return data != null && super.canOpenInputFlow()
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

}

@Suppress("NOTHING_TO_INLINE")
public inline fun CoroutineScope.ajaxDataSourceAsync(
    url: String,
    maxInstanceCount: Int = -1,
    location: String? = url
): Promise<AjaxDataSource> =
    promise {
        AjaxDataSource(
            url,
            maxInstanceCount,
            location
        )
    }