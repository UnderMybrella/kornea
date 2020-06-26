package dev.brella.kornea.img

import dev.brella.kornea.io.common.CommonBase64Encoder
import dev.brella.kornea.io.common.flow.OutputFlow
import org.khronos.webgl.set
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document

//TODO: idk if this actually works

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
actual suspend fun OutputFlow.writePngImage(img: RgbMatrix) {
    val canvas = document.createElement("canvas") as HTMLCanvasElement
    canvas.width = img.width
    canvas.height = img.height
    val context = canvas.getContext("2d") as? CanvasRenderingContext2D ?: return
    val imageData = context.createImageData(img.width.toDouble(), img.height.toDouble())
    val data = imageData.data

    img.rgb.forEachIndexed { index, rgb ->
        data[index * 4 + 0] = (rgb shr 16).toByte()
        data[index * 4 + 1] = (rgb shr 8).toByte()
        data[index * 4 + 2] = (rgb shr 0).toByte()
        data[index * 4 + 3] = (rgb shr 24).toByte()
    }

    context.putImageData(imageData, 0.0, 0.0)
    canvas.toDataURL("image/png")
        .takeIf { str -> "image/png" in str }
        ?.substringAfter("base64,")
        ?.let { str -> write(CommonBase64Encoder.decode(str.encodeToByteArray())) }
        ?: return
}