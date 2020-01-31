package org.abimon.kornea.img

data class RgbMatrix(val width: Int, val height: Int, val rgb: IntArray) {
    constructor(width: Int, height: Int): this(width, height, IntArray(width * height))
    operator fun get(x: Int, y: Int): RgbColour = RgbColour(rgb[x * width + y])
    operator fun set(x: Int, y: Int, colour: RgbColour) {
        rgb[y * width + x] = colour.rgb
    }
    operator fun set(x: Int, y: Int, colour: Int) {
        rgb[y * width + x] = colour
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as RgbMatrix

        if (width != other.width) return false
        if (height != other.height) return false
        if (!rgb.contentEquals(other.rgb)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        result = 31 * result + rgb.contentHashCode()
        return result
    }


    init {
        require(rgb.size == width * height)
    }
}