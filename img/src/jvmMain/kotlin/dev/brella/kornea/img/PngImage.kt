package dev.brella.kornea.img

import dev.brella.kornea.io.common.flow.OutputFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import java.awt.Image
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

fun RgbMatrix.createPngImage(): BufferedImage {
    val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    rgb.copyInto((bufferedImage.raster.dataBuffer as DataBufferInt).data, 0, 0, rgb.size)
    return bufferedImage
}

fun BufferedImage.asMatrixFromPng(): RgbMatrix =
    when (val buffer = raster.dataBuffer) {
        is DataBufferInt -> RgbMatrix(width, height, buffer.data)
        else -> RgbMatrix(width, height) { rgb -> getRGB(0, 0, width, height, rgb, 0, width) }
    }

fun Image.asMatrixFromPng(): RgbMatrix {
    val img = BufferedImage(getWidth(null), getHeight(null), BufferedImage.TYPE_INT_ARGB)
    val g = img.graphics
    try {
        g.drawImage(this, 0, 0, null)
    } finally {
        g.dispose()
    }

    return img.asMatrixFromPng()
}

actual suspend fun OutputFlow.writePngImage(img: RgbMatrix) {
    val baos = ByteArrayOutputStream()
    runInterruptible(Dispatchers.IO) { ImageIO.write(img.createPngImage(), "PNG", baos) }
    write(baos.toByteArray())
}