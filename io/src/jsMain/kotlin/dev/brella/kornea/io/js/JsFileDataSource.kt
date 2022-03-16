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
import org.w3c.files.File
import org.w3c.files.FileReader
import kotlin.js.Promise

public class JsFileDataSource(
    private val file: File,
    override val maximumInstanceCount: Int? = null,
    override val location: String? = file.name
) : LimitedInstanceDataSource.Typed<BinaryInputFlow, JsFileDataSource>(withBareOpener(this::openBareInputFlow)) {
    public companion object {
        public fun openBareInputFlow(
            self: JsFileDataSource,
            location: String?
        ): BinaryInputFlow = BinaryInputFlow(self.data!!, location = location ?: self.location)
    }

    private var data: ByteArray? = null
    private val dataMutex: Mutex = Mutex()
    override val dataSize: ULong?
        get() = data?.size?.toULong()

    private val dataPromise: Promise<ArrayBuffer?> =
        Promise { resolve: (ArrayBuffer?) -> Unit, _: (Throwable) -> Unit ->
            val reader = FileReader()
            reader.onloadend = { _ -> resolve(reader.result as? ArrayBuffer) }
            reader.readAsArrayBuffer(file)
        }

    override val reproducibility: DataSourceReproducibility =
        DataSourceReproducibility(isStatic = true, isRandomAccess = true)

    override fun locationAsUri(): KorneaResult<Uri> = KorneaResult.empty()

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
public inline fun CoroutineScope.jsFileDataSourceAsync(
    file: File,
    maxInstanceCount: Int = -1,
    location: String? = file.name
): Promise<JsFileDataSource> =
    promise {
        JsFileDataSource(
            file,
            maxInstanceCount,
            location
        )
    }