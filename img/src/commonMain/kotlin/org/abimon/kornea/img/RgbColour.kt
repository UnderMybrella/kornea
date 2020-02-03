package org.abimon.kornea.img

inline class RgbColour(val rgb: Int) {
    companion object {
        fun rgba(red: Int, green: Int, blue: Int, alpha: Int = 0xFF) = RgbColour((alpha and 255 shl 24) or (red and 255 shl 16) or (green and 255 shl 8) or (blue and 255 shl 0))
        fun argb(alpha: Int, red: Int, green: Int, blue: Int) = RgbColour((alpha and 255 shl 24) or (red and 255 shl 16) or (green and 255 shl 8) or (blue and 255 shl 0))
    }

    val alpha: Int
        get() = rgb shr 24 and 0xFF

    val red: Int
        get() = rgb shr 16 and 0xFF

    val green: Int
        get() = rgb shr 8 and 0xFF

    val blue: Int
        get() = rgb shr 0 and 0xFF
}

fun rgba(red: Int, green: Int, blue: Int, alpha: Int = 0xFF) = argb(alpha, red, green, blue)
fun argb(alpha: Int, red: Int, green: Int, blue: Int) =
    (alpha and 255 shl 24) or (red and 255 shl 16) or (green and 255 shl 8) or (blue and 255 shl 0)