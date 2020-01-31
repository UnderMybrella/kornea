package org.abimon.kornea.img

import kotlinx.cinterop.*
import kotlinx.coroutines.runBlocking
import org.abimon.kornea.io.common.flow.OutputFlow
import org.abimon.kornea.io.posix.asStableReference
import org.abimon.kornea.io.posix.use
import platform.posix.*
import png.*
import pointerToPtrVar
import pointerVar

data class MemEncode(var buffer: png_voidp?, var size: size_t)

@ExperimentalUnsignedTypes
actual suspend fun OutputFlow.writePngImage(img: RgbMatrix) {
    asStableReference { stableRef ->
        val pngPtr: png_structp = png_create_write_struct(PNG_LIBPNG_VER_STRING, null, null, null)
            ?: return@asStableReference

        val infoPtr: png_infop? = png_create_info_struct(pngPtr)
        if (infoPtr == null) {
            png_destroy_write_struct(pngPtr.pointerToPtrVar, null)
            return@asStableReference
        }

        png_set_IHDR(
            pngPtr,
            infoPtr,
            img.width.toUInt(),
            img.height.toUInt(),
            8,
            PNG_COLOR_TYPE_RGBA,
            PNG_INTERLACE_NONE,
            PNG_COMPRESSION_TYPE_DEFAULT,
            PNG_FILTER_TYPE_DEFAULT
        )

        png_set_write_fn(pngPtr, stableRef.asCPointer(), staticCFunction(::libpngWriteData), null)
        png_write_info(pngPtr, infoPtr)

        img.rgb.usePinned { pinned ->
            for (y in 0 until img.height) {
                png_write_row(pngPtr, pinned.addressOf(y * img.width).reinterpret())
            }
        }

        png_write_end(pngPtr, infoPtr)
        png_destroy_write_struct(pngPtr.pointerToPtrVar, infoPtr.pointerToPtrVar)
    }
}

@ExperimentalUnsignedTypes
fun libpngWriteData(pngPtr: png_structp?, data: png_bytep?, length: png_size_t) {
    val flow = png_get_io_ptr(pngPtr)?.asStableRef<OutputFlow>()?.get() ?: return
    if (data != null) {
        runBlocking { flow.write(data.readBytes(length.toInt())) }
    }
}