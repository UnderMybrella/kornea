package org.abimon.kornea.io.jvm.files

import org.abimon.kornea.io.common.*
import java.io.File
import kotlin.math.max

@ExperimentalUnsignedTypes
class FileDataSource(val backing: File, val maxInstanceCount: Int = -1, override val location: String? = backing.absolutePath): DataSource<FileInputFlow> {
    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()
    override val dataSize: ULong
        get() = backing.length().toULong()

    private val openInstances: MutableList<FileInputFlow> = ArrayList(max(maxInstanceCount, 0))
    private var closed: Boolean = false
    override val isClosed: Boolean
        get() = closed

    override val reproducibility: DataSourceReproducibility = DataSourceReproducibility(isStatic = true, isRandomAccess = true)

    override suspend fun openNamedInputFlow(location: String?): FileInputFlow? {
        if (canOpenInputFlow()) {
            val stream = FileInputFlow(backing, location ?: this.location)
            stream.addCloseHandler(this::instanceClosed)
            openInstances.add(stream)
            return stream
        } else {
            return null
        }
    }

    override suspend fun canOpenInputFlow(): Boolean = !closed && (maxInstanceCount == -1 || openInstances.size < maxInstanceCount)

    private suspend fun instanceClosed(closeable: ObservableDataCloseable) {
        if (closeable is FileInputFlow) {
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
}