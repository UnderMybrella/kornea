import kotlinx.cinterop.*
import org.abimon.kornea.io.common.flow.BinaryOutputFlow
import org.abimon.kornea.io.common.use
import platform.posix.FILE

val <T : CPointed> CPointer<T>.pointerVar: CPointerVar<T>
    get() = nativeHeap.allocPointerTo<T>().apply { value = this@pointerVar }

val <T : CPointed> CPointer<T>.pointerToPtrVar: CPointer<CPointerVar<T>>
    get() = nativeHeap.allocArrayOfPointersTo<T>().apply { set(0, this@pointerToPtrVar) }