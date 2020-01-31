package org.abimon.kornea.io.jvm.files

import org.abimon.kornea.io.common.DataCloseableEventHandler
import org.abimon.kornea.io.common.DataPool
import org.abimon.kornea.io.common.DataSink
import org.abimon.kornea.io.common.DataSource
import java.io.File

@ExperimentalUnsignedTypes
class FileDataPool(val file: File, private val sinkBacker: DataSink<FileOutputFlow> = FileDataSink(file), private val sourceBacker: DataSource<FileInputFlow> = FileDataSource(file)) :
        DataPool<FileInputFlow, FileOutputFlow>,
        DataSink<FileOutputFlow> by sinkBacker,
        DataSource<FileInputFlow> by sourceBacker {
    private var closed: Boolean = false
    override val isClosed: Boolean
        get() = closed

    override val closeHandlers: MutableList<DataCloseableEventHandler> = ArrayList()

    override suspend fun close() {
        super<DataPool>.close()

        if (!closed) {
            sinkBacker.close()
            sourceBacker.close()
        }
    }
}