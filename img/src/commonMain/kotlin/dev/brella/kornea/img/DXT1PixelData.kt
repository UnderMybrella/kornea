package dev.brella.kornea.img

import dev.brella.kornea.io.common.flow.InputFlow
import dev.brella.kornea.io.common.readInt16LE
import dev.brella.kornea.io.common.readInt32LE

object DXT1PixelData {
    @ExperimentalUnsignedTypes
    suspend fun read(width: Int, height: Int, flow: InputFlow): RgbMatrix {
        val matrix = RgbMatrix(width, height)

        for (supposedIndex in 0 until ((height * width) / 16)) {
            val texelPalette = Array(4) { RgbColour(0) }
            val colourBytes = IntArray(2) { requireNotNull(flow.readInt16LE()) }
            val indices = requireNotNull(flow.readInt32LE())

            colourBytes.forEachIndexed { i, rgb565 ->
                val r = (rgb565 and 0xF800) shr 8
                val g = (rgb565 and 0x7E0) shr 3
                val b = (rgb565 and 0x1F) shl 3

                texelPalette[i] = RgbColour.rgba(
                    r or (r shr 5),
                    g or (g shr 6),
                    b or (b shr 5)
                )
            }

            if (colourBytes[0] > colourBytes[1]) {
                texelPalette[2] = RgbColour.rgba(
                    (2 * texelPalette[0].red + 1 * texelPalette[1].red) / 3,
                    (2 * texelPalette[0].green + 1 * texelPalette[1].green) / 3,
                    (2 * texelPalette[0].blue + 1 * texelPalette[1].blue) / 3
                )

                texelPalette[3] = RgbColour.rgba(
                    (1 * texelPalette[0].red + 2 * texelPalette[1].red) / 3,
                    (1 * texelPalette[0].green + 2 * texelPalette[1].green) / 3,
                    (1 * texelPalette[0].blue + 2 * texelPalette[1].blue) / 3
                )
            } else {
                texelPalette[2] = RgbColour.rgba(
                    (texelPalette[0].red + texelPalette[1].red) / 2,
                    (texelPalette[0].green + texelPalette[1].green) / 2,
                    (texelPalette[0].blue + texelPalette[1].blue) / 2
                )
                texelPalette[3] = RgbColour.rgba(0, 0, 0, 0)
            }

            for (index in 0 until 16)
                matrix[
                        (supposedIndex % (width / 4)) * 4 + (index % 4),
                        (supposedIndex / (width / 4)) * 4 + (index / 4)
                ] = texelPalette[3 and (indices shr (2 * index))].rgb
        }

        return matrix
    }
}