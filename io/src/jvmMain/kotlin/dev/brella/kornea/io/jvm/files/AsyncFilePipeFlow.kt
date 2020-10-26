package dev.brella.kornea.io.jvm.files

import dev.brella.kornea.io.common.BaseDataCloseable
import dev.brella.kornea.io.common.flow.*
import dev.brella.kornea.io.jvm.limitSafe
import kotlinx.coroutines.sync.Mutex
import java.nio.ByteBuffer

//TODO
//class AsyncFilePipeFlow
//    : BaseDataCloseable(), SeekablePipeFlow<AsyncFilePipeFlow, AsyncFilePipeFlow>, PeekableInputFlow, SeekableInputFlow,
//    IntFlowState, InputFlowState<BinaryPipeFlow>,
//    CountingOutputFlow, SeekableOutputFlow, PrintOutputFlow {
//    private val mutex: Mutex = Mutex()
//
//    private var flowFilePointer: Long = 0L
//    private var buffer: ByteBuffer = ByteBuffer.allocate(AsyncFileInputFlow.DEFAULT_BUFFER_SIZE).apply { limitSafe(0) } //Force a refill
//    private var peekFilePointer: Long = 0L
//    private var peekBuffer: ByteBuffer = ByteBuffer.allocate(AsyncFileInputFlow.DEFAULT_BUFFER_SIZE).apply { limitSafe(0) }
//
//
//}