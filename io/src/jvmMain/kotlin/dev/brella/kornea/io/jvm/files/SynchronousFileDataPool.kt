package dev.brella.kornea.io.jvm.files

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import dev.brella.kornea.io.common.*
import java.io.File

@ExperimentalUnsignedTypes
public class SynchronousFileDataPool(
    public val file: File,
    private val sinkBacker: DataSink<SynchronousFileOutputFlow> = SynchronousFileDataSink(
        file
    ),
    private val sourceBacker: DataSource<SynchronousFileInputFlow> = SynchronousFileDataSource(
        file
    )
) : DataPool<SynchronousFileInputFlow, SynchronousFileOutputFlow>,
    DataSink<SynchronousFileOutputFlow> by sinkBacker,
    DataSource<SynchronousFileInputFlow> by sourceBacker,
    BaseDataCloseable() {

    override val isClosed: Boolean
        get() = super.isClosed

    override val closeHandlers: List<DataCloseableEventHandler>
        get() = super.closeHandlers

    override suspend fun registerCloseHandler(handler: DataCloseableEventHandler): Boolean =
        super.registerCloseHandler(handler)

    override suspend fun close() {
        super.close()
    }

    override suspend fun whenClosed() {
        super.whenClosed()

        sinkBacker.close()
        sourceBacker.close()
    }
}