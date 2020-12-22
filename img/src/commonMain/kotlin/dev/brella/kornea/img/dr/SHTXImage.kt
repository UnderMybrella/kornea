package dev.brella.kornea.img.dr

import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.errors.common.StaticSuccess
import dev.brella.kornea.errors.common.korneaNotEnoughData
import dev.brella.kornea.img.RgbMatrix
import dev.brella.kornea.io.common.flow.InputFlow
import dev.brella.kornea.io.common.flow.OutputFlow
import dev.brella.kornea.io.common.flow.extensions.*

sealed class SHTXImage(width: Int, height: Int, rgb: IntArray) : RgbMatrix(width, height, rgb) {
    companion object {
        const val MAGIC_NUMBER = 0x58544853
    }

    class None(width: Int, height: Int, rgb: IntArray, val unk: Int, val unk2: Int, val palette: IntArray) :
        SHTXImage(width, height, rgb) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            if (!super.equals(other)) return false

            other as None

            if (width != other.width) return false
            if (height != other.height) return false
            if (!rgb.contentEquals(other.rgb)) return false
            if (unk != other.unk) return false
            if (unk2 != other.unk2) return false
            if (!palette.contentEquals(other.palette)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + width
            result = 31 * result + height
            result = 31 * result + rgb.contentHashCode()
            result = 31 * result + unk
            result = 31 * result + unk2
            result = 31 * result + palette.contentHashCode()
            return result
        }
    }

    /** SHTXFs */
    class ARGBPalette(width: Int, height: Int, rgb: IntArray, val unk: Int, val palette: IntArray) :
        SHTXImage(width, height, rgb) {
        companion object {
            const val MAGIC_NUMBER = 0x7346
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            if (!super.equals(other)) return false

            other as ARGBPalette

            if (width != other.width) return false
            if (height != other.height) return false
            if (!rgb.contentEquals(other.rgb)) return false
            if (unk != other.unk) return false
            if (!palette.contentEquals(other.palette)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + width
            result = 31 * result + height
            result = 31 * result + rgb.contentHashCode()
            result = 31 * result + unk
            result = 31 * result + palette.contentHashCode()
            return result
        }
    }

    /** SHTXFS */
    class BGRAPalette(width: Int, height: Int, rgb: IntArray, val unk: Int, val palette: IntArray) :
        SHTXImage(width, height, rgb) {
        companion object {
            const val MAGIC_NUMBER = 0x5346
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            if (!super.equals(other)) return false

            other as BGRAPalette

            if (width != other.width) return false
            if (height != other.height) return false
            if (!rgb.contentEquals(other.rgb)) return false
            if (unk != other.unk) return false
            if (!palette.contentEquals(other.palette)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + width
            result = 31 * result + height
            result = 31 * result + rgb.contentHashCode()
            result = 31 * result + unk
            result = 31 * result + palette.contentHashCode()
            return result
        }
    }

    class ARGB(width: Int, height: Int, rgb: IntArray, val unk: Int) : SHTXImage(width, height, rgb) {
        companion object {
            const val MAGIC_NUMBER = 0x6646
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            if (!super.equals(other)) return false

            other as ARGB

            if (width != other.width) return false
            if (height != other.height) return false
            if (!rgb.contentEquals(other.rgb)) return false
            if (unk != other.unk) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + width
            result = 31 * result + height
            result = 31 * result + rgb.contentHashCode()
            result = 31 * result + unk
            return result
        }
    }

    class BGRA(width: Int, height: Int, rgb: IntArray, val unk: Int) : SHTXImage(width, height, rgb) {
        companion object {
            const val MAGIC_NUMBER = 0x4646
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            if (!super.equals(other)) return false

            other as BGRA

            if (width != other.width) return false
            if (height != other.height) return false
            if (!rgb.contentEquals(other.rgb)) return false
            if (unk != other.unk) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + width
            result = 31 * result + height
            result = 31 * result + rgb.contentHashCode()
            result = 31 * result + unk
            return result
        }
    }
}

public suspend fun InputFlow.readSHTXImage(): KorneaResult<SHTXImage> {
    val magic = readInt32LE() ?: return korneaNotEnoughData()
    if (magic != SHTXImage.MAGIC_NUMBER) return KorneaResult.errorAsIllegalArgument(-1, "Invalid magic $magic")
    val potentialMagic = readInt16LE() ?: return korneaNotEnoughData()

    return when (potentialMagic) {
        SHTXImage.ARGBPalette.MAGIC_NUMBER -> readSHTXFsImage()
        SHTXImage.BGRAPalette.MAGIC_NUMBER -> readSHTXFSImage()
        SHTXImage.ARGB.MAGIC_NUMBER -> readSHTXFfImage()
        SHTXImage.BGRA.MAGIC_NUMBER -> readSHTXFFImage()
        else -> readSHTXUnkImage(potentialMagic)
    }
}

public suspend fun InputFlow.readSHTXUnkImage(providedWidth: Int? = null): KorneaResult<SHTXImage.None> {
    val width: Int = providedWidth ?: run {
        val magic = readInt32LE() ?: return korneaNotEnoughData()
        if (magic != SHTXImage.MAGIC_NUMBER) return KorneaResult.errorAsIllegalArgument(-1, "Invalid magic $magic")

        val width = readInt16LE() ?: return korneaNotEnoughData()
        when (width) {
            SHTXImage.ARGBPalette.MAGIC_NUMBER -> return KorneaResult.errorAsIllegalArgument(
                -1,
                "Invalid width $width; got Fs magic number"
            )
            SHTXImage.BGRAPalette.MAGIC_NUMBER -> return KorneaResult.errorAsIllegalArgument(
                -1,
                "Invalid width $width; got FS magic number"
            )
            SHTXImage.ARGB.MAGIC_NUMBER -> return KorneaResult.errorAsIllegalArgument(
                -1,
                "Invalid width $width; got Ff magic number"
            )
            SHTXImage.BGRA.MAGIC_NUMBER -> return KorneaResult.errorAsIllegalArgument(
                -1,
                "Invalid width $width; got FF magic number"
            )
        }
        width
    }
    val height = readInt16LE() ?: return korneaNotEnoughData()
    val unk = readInt16LE() ?: return korneaNotEnoughData()
    val paletteSize = readInt16LE() ?: return korneaNotEnoughData()
    val unk2 = readInt32LE() ?: return korneaNotEnoughData()

    val palette = IntArray(paletteSize) { readInt32LE() ?: return korneaNotEnoughData() }
    val rgba = IntArray(width * height)

    for (xy in rgba.indices step 2) {
        val i = read() ?: return korneaNotEnoughData()
        rgba[xy + 0] = palette[i shr 4]
        rgba[xy + 1] = palette[i and 0xF]
    }

    return KorneaResult.success(SHTXImage.None(width, height, rgba, unk, unk2, palette))
}

public suspend fun InputFlow.readSHTXFsImage(): KorneaResult<SHTXImage.ARGBPalette> {
    val width = readInt16LE() ?: return korneaNotEnoughData()
    val height = readInt16LE() ?: return korneaNotEnoughData()
    val unk = readInt16LE() ?: return korneaNotEnoughData()

    val palette = IntArray(256) { readInt32LE() ?: return korneaNotEnoughData() }
    val argb = IntArray(width * height)

    for (y in 0 until height) {
        for (x in 0 until width) {
            argb[(y * width) + x] = palette[read() ?: return korneaNotEnoughData()]
        }
    }

    return KorneaResult.success(SHTXImage.ARGBPalette(width, height, argb, unk, palette))
}

public suspend fun InputFlow.readSHTXFSImage(): KorneaResult<SHTXImage.BGRAPalette> {
    val width = readInt16LE() ?: return korneaNotEnoughData()
    val height = readInt16LE() ?: return korneaNotEnoughData()
    val unk = readInt16LE() ?: return korneaNotEnoughData()

    val palette = IntArray(256) { readInt32BE() ?: return korneaNotEnoughData() }
    val argb = IntArray(width * height)

    for (y in 0 until height) {
        for (x in 0 until width) {
            argb[(y * width) + x] = palette[read() ?: return korneaNotEnoughData()]
        }
    }

    return KorneaResult.success(SHTXImage.BGRAPalette(width, height, argb, unk, palette))
}

public suspend fun InputFlow.readSHTXFfImage(): KorneaResult<SHTXImage.ARGB> {
    val width = readInt16LE() ?: return korneaNotEnoughData()
    val height = readInt16LE() ?: return korneaNotEnoughData()
    val unk = readInt16LE() ?: return korneaNotEnoughData()
    val argb = IntArray(width * height)

    for (y in 0 until height) {
        for (x in 0 until width) {
            argb[(y * width) + x] = readInt32LE() ?: return korneaNotEnoughData()
        }
    }

    return KorneaResult.success(SHTXImage.ARGB(width, height, argb, unk))
}

public suspend fun InputFlow.readSHTXFFImage(): KorneaResult<SHTXImage.BGRA> {
    val width = readInt16LE() ?: return korneaNotEnoughData()
    val height = readInt16LE() ?: return korneaNotEnoughData()
    val unk = readInt16LE() ?: return korneaNotEnoughData()
    val bgra = IntArray(width * height)

    for (y in 0 until height) {
        for (x in 0 until width) {
            bgra[(y * width) + x] = readInt32BE() ?: return korneaNotEnoughData()
        }
    }

    return KorneaResult.success(SHTXImage.BGRA(width, height, bgra, unk))
}

public suspend fun OutputFlow.writeSHTXImage(img: RgbMatrix, unk: Int = 4, preferBigEndian: Boolean = false): KorneaResult<StaticSuccess> {
    val heatmap: MutableMap<Int, Int> = HashMap()
    img.rgb.forEach { rgba -> heatmap[rgba] = heatmap[rgba]?.plus(1) ?: 1 }

    return when {
        heatmap.size <= 16 -> writeSHTXUnkImage(img, unk = unk, heatmap = heatmap)
        heatmap.size <= 256 -> if (preferBigEndian) writeSHTXFSImage(img, unk, heatmap) else writeSHTXFsImage(img, unk, heatmap)
        else -> if (preferBigEndian) writeSHTXFfImage(img, unk) else writeSHTXFFImage(img, unk)
    }
}
public suspend fun OutputFlow.writeSHTXUnkImage(
    img: RgbMatrix,
    unk: Int = if (img is SHTXImage.None) img.unk else 4,
    unk2: Int = if (img is SHTXImage.None) img.unk2 else 0,
    heatmap: Map<Int, Int>? = null
): KorneaResult<StaticSuccess> {
    if (img is SHTXImage.None) {
        writeInt32LE(SHTXImage.MAGIC_NUMBER)
        writeInt16LE(img.width)
        writeInt16LE(img.height)
        writeInt16LE(unk)

        writeInt16LE(img.palette.size)
        writeInt32LE(unk2)

        img.palette.forEach { writeInt32LE(it) }
        for (xy in img.rgb.indices step 2) {
            write((img.palette.indexOf(img.rgb[xy + 0]) shl 4) or (img.palette.indexOf(img.rgb[xy + 0])))
        }
    } else {
        //First step, create heatmap buckets of colours
        val heatmap: Map<Int, Int> = heatmap ?: HashMap<Int, Int>().apply {
            img.rgb.forEach { rgba -> this[rgba] = this[rgba]?.plus(1) ?: 1 }
        }
        val remap: MutableMap<Int, Int> = HashMap()

        println("Unique Colours: ${heatmap.size}")

        //We need to create a palette of 16 colours, let's-a go
/*        val sorted = ArrayList<Map.Entry<Int, Int>>(heatmap.entries)
        val comparator = Comparator<Map.Entry<Int, Int>> { (c1), (c2) ->
            abs((c1 shr 24 and 0xFF).compareTo(c2 shr 24 and 0xFF) + (c1 shr 16 and 0xFF).compareTo(c2 shr 16 and 0xFF) + (c1 shr 8 and 0xFF).compareTo(c2 shr 8 and 0xFF) + (c1 and 0xFF).compareTo(c2 and 0xFF))
        }

        while (heatmap.size > 16) {
            sorted.sortedWith(comparator)

            val firstSacrifice = sorted.removeAt(sorted.size - 1)
            val secondSacrifice = sorted.removeAt(sorted.size - 1)

            val percentageFirst = firstSacrifice.value / (firstSacrifice.value + secondSacrifice.value).toDouble()
            val percentageSecond = secondSacrifice.value / (firstSacrifice.value + secondSacrifice.value).toDouble()

            heatmap.remove(firstSacrifice.key)
            heatmap.remove(secondSacrifice.key)

            val colourFirst = RgbColour(firstSacrifice.key)
            val colourSecond = RgbColour(secondSacrifice.key)

            val result = rgba(
                ((colourFirst.red * percentageFirst) +
                        (colourSecond.red * percentageSecond)).roundToInt(),
                ((colourFirst.green * percentageFirst) +
                        (colourSecond.green * percentageSecond)).roundToInt(),
                ((colourFirst.blue * percentageFirst) +
                        (colourSecond.blue * percentageSecond)).roundToInt(),
                ((colourFirst.alpha * percentageFirst) +
                        (colourSecond.alpha * percentageSecond)).roundToInt()
            )

            heatmap[result] = firstSacrifice.value + secondSacrifice.value
            sorted.add(SimpleEntry(result, firstSacrifice.value + secondSacrifice.value))

            remap[firstSacrifice.key] = result
            remap[secondSacrifice.key] = result
        }*/

        if (heatmap.size > 16) return KorneaResult.empty()

        writeInt32LE(SHTXImage.MAGIC_NUMBER)
        writeInt16LE(img.width)
        writeInt16LE(img.height)
        writeInt16LE(unk)

        writeInt16LE(heatmap.size)
        writeInt32LE(unk2)

        val uniqueColours = heatmap.keys.toList()
        uniqueColours.forEach { writeInt32LE(it) }
        for (xy in img.rgb.indices step 2) {
            var a = img.rgb[xy + 0]
            while (true) a = remap[a]?.takeUnless { it == a } ?: break
            var b = img.rgb[xy + 1]
            while (true) b = remap[b]?.takeUnless { it == b } ?: break

            write((uniqueColours.indexOf(a) shl 4) or (uniqueColours.indexOf(b)))
        }
    }

    return StaticSuccess
}

public suspend fun OutputFlow.writeSHTXFsImage(
    img: RgbMatrix,
    unk: Int = if (img is SHTXImage.None) img.unk else 4,
    heatmap: Map<Int, Int>? = null
): KorneaResult<StaticSuccess> {
    if (img is SHTXImage.ARGBPalette) {
        writeInt32LE(SHTXImage.MAGIC_NUMBER)
        writeInt16LE(SHTXImage.ARGBPalette.MAGIC_NUMBER)
        writeInt16LE(img.width)
        writeInt16LE(img.height)
        writeInt16LE(unk)

        img.palette.forEach { writeInt32LE(it) }
        img.rgb.forEach { rgb -> write(img.palette.indexOf(rgb)) }
    } else {
        //First step, create heatmap buckets of colours
        val heatmap: Map<Int, Int> = heatmap ?: HashMap<Int, Int>().apply {
            img.rgb.forEach { rgba -> this[rgba] = this[rgba]?.plus(1) ?: 1 }
        }
//        val remap: MutableMap<Int, Int> = HashMap()

        println("Unique Colours: ${heatmap.size}")

        //We need to create a palette of 16 colours, let's-a go
        if (heatmap.size > 256) return KorneaResult.empty()

        writeInt32LE(SHTXImage.MAGIC_NUMBER)
        writeInt16LE(SHTXImage.ARGBPalette.MAGIC_NUMBER)
        writeInt16LE(img.width)
        writeInt16LE(img.height)
        writeInt16LE(unk)

        val uniqueColours = heatmap.keys.toList()
        uniqueColours.forEach { writeInt32LE(it) }
        repeat(256 - uniqueColours.size) { writeInt32LE(0) }

        img.rgb.forEach { rgb -> write(uniqueColours.indexOf(rgb)) }
    }

    return StaticSuccess
}

public suspend fun OutputFlow.writeSHTXFSImage(
    img: RgbMatrix,
    unk: Int = if (img is SHTXImage.None) img.unk else 4,
    heatmap: Map<Int, Int>? = null
): KorneaResult<StaticSuccess> {
    if (img is SHTXImage.BGRAPalette) {
        writeInt32LE(SHTXImage.MAGIC_NUMBER)
        writeInt16LE(SHTXImage.BGRAPalette.MAGIC_NUMBER)
        writeInt16LE(img.width)
        writeInt16LE(img.height)
        writeInt16LE(unk)

        img.palette.forEach { writeInt32BE(it) }
        img.rgb.forEach { rgb -> write(img.palette.indexOf(rgb)) }
    } else {
        //First step, create heatmap buckets of colours
        val heatmap: Map<Int, Int> = heatmap ?: HashMap<Int, Int>().apply {
            img.rgb.forEach { rgba -> this[rgba] = this[rgba]?.plus(1) ?: 1 }
        }
//        val remap: MutableMap<Int, Int> = HashMap()

        println("Unique Colours: ${heatmap.size}")

        if (heatmap.size > 256) return KorneaResult.empty()

        writeInt32LE(SHTXImage.MAGIC_NUMBER)
        writeInt16LE(SHTXImage.BGRAPalette.MAGIC_NUMBER)
        writeInt16LE(img.width)
        writeInt16LE(img.height)
        writeInt16LE(unk)

        val uniqueColours = heatmap.keys.toList()
        uniqueColours.forEach { writeInt32BE(it) }
        repeat(256 - uniqueColours.size) { writeInt32LE(0) }

        img.rgb.forEach { rgb -> write(uniqueColours.indexOf(rgb)) }
    }

    return StaticSuccess
}

public suspend fun OutputFlow.writeSHTXFfImage(
    img: RgbMatrix,
    unk: Int = if (img is SHTXImage.None) img.unk else 4
): KorneaResult<StaticSuccess> {
    writeInt32LE(SHTXImage.MAGIC_NUMBER)
    writeInt16LE(SHTXImage.ARGB.MAGIC_NUMBER)
    writeInt16LE(img.width)
    writeInt16LE(img.height)
    writeInt16LE(unk)

    img.rgb.forEach { rgb -> writeInt32LE(rgb) }

    return StaticSuccess
}

public suspend fun OutputFlow.writeSHTXFFImage(
    img: RgbMatrix,
    unk: Int = if (img is SHTXImage.None) img.unk else 4
): KorneaResult<StaticSuccess> {
    writeInt32LE(SHTXImage.MAGIC_NUMBER)
    writeInt16LE(SHTXImage.BGRA.MAGIC_NUMBER)
    writeInt16LE(img.width)
    writeInt16LE(img.height)
    writeInt16LE(unk)

    img.rgb.forEach { rgb -> writeInt32BE(rgb) }

    return StaticSuccess
}