package dev.brella.kornea.img

import dev.brella.kornea.io.common.flow.OutputFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

fun RgbMatrix.createPngImage(): BufferedImage {
    val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    rgb.copyInto((bufferedImage.raster.dataBuffer as DataBufferInt).data, 0, 0, rgb.size)
    return bufferedImage
}

@ExperimentalUnsignedTypes
actual suspend fun OutputFlow.writePngImage(img: RgbMatrix) {
    val baos = ByteArrayOutputStream()
    runInterruptible(Dispatchers.IO) { ImageIO.write(img.createPngImage(), "PNG", baos) }
    write(baos.toByteArray())
}