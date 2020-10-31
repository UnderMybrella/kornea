package dev.brella.kornea.img

import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.errors.common.getOrBreak
import dev.brella.kornea.errors.common.korneaNotEnoughData
import dev.brella.kornea.io.common.EnumSeekMode
import dev.brella.kornea.io.common.flow.BinaryPipeFlow
import dev.brella.kornea.io.common.flow.InputFlow
import dev.brella.kornea.io.common.flow.extensions.*
import dev.brella.kornea.io.common.flow.extensions.readInt16LE
import dev.brella.kornea.io.common.flow.readBytes
import dev.brella.kornea.io.common.flow.readExact

class TargaImage(
    val imageID: ByteArray,
    val colourMapType: Int,
    val imageType: Int,
    val colourMap: IntArray?,
    val xOrigin: Int,
    val yOrigin: Int,
    val pixelDepth: Int,
    val imageDescriptor: Int,
    val extensionArea: TargaImageExtension?,
    width: Int,
    height: Int,
    rgb: IntArray
) : RgbMatrix(width, height, rgb) {
    override fun toString(): String {
        return "TargaImage(width=$width, height=$height, imageID=${imageID.contentToString()}, colourMapType=$colourMapType, imageType=$imageType, colourMap=${colourMap?.contentToString()}, xOrigin=$xOrigin, yOrigin=$yOrigin, pixelDepth=$pixelDepth, imageDescriptor=$imageDescriptor, extensionArea=$extensionArea)"
    }
}

data class TargaImageExtension(
    val authorName: String,
    val comments: Array<String>,

    val monthSaved: Int,
    val daySaved: Int,
    val yearSaved: Int,
    val hourSaved: Int,
    val minuteSaved: Int,
    val secondSaved: Int,

    val jobID: String,
    val jobTimeTaken: Long,

    val softwareID: String,
    val softwareVersion: Int,
    val softwareVersionTag: Char,

    val keyColour: Int,

    val pixelWidth: Int,
    val pixelHeight: Int,
    val gammaNumerator: Int,
    val gammaDenominator: Int,

    val colourCorrectionOffset: Int?,
    val postageStampOffset: Int?,
    val scanLineOffset: Int?,

    val attributesType: EnumTargaAttributesType
)

sealed class EnumTargaAttributesType(open val id: Int, val keepAlpha: Boolean) {
    object NO_ALPHA_DATA : EnumTargaAttributesType(0, false)
    object UNDEFINED_IGNORE : EnumTargaAttributesType(1, false)
    object UNDEFINED_RETAIN : EnumTargaAttributesType(2, false)
    object USEFUL_ALPHA : EnumTargaAttributesType(3, true)
    object PRE_MULTIPLIED_ALPHA : EnumTargaAttributesType(4, false)
    data class RESERVED(override val id: Int) : EnumTargaAttributesType(id, false)
    data class UNASSIGNED(override val id: Int) : EnumTargaAttributesType(id, false)
}

inline fun InputFlow.createRLERefill(
    heap: BinaryPipeFlow,
    pixelDepth: Int,
    colourMapped: Boolean
): KorneaResult<suspend (buffer: ByteArray) -> ByteArray?> {
    if (colourMapped) {
        when (pixelDepth) {
            8 -> return KorneaResult.success(refill@{ buffer ->
                val startPos = heap.position()
                while ((heap.size()!! - startPos).toInt() < buffer.size) {
                    val packetHeader = read() ?: return@refill null
                    val packetSize = (packetHeader and 0x7F) + 1
                    val isRLE = packetHeader and 0x80 == 0x80

                    if (isRLE) {
                        val index = read() ?: return@refill null
                        repeat(packetSize) { heap.write(index) }
                    } else {
                        repeat(packetSize) {
                            heap.write(read() ?: return@refill null)
                        }
                    }
                }

                heap.seek(startPos.toLong(), EnumSeekMode.FROM_BEGINNING)
                heap.readExact(buffer)
            }, null)
            16 -> return KorneaResult.success(refill@{ buffer ->
                val startPos = heap.position()
                while ((heap.size()!! - startPos).toInt() < buffer.size) {
                    val packetHeader = read() ?: return@refill null
                    val packetSize = (packetHeader and 0x7F) + 1
                    val isRLE = packetHeader and 0x80 == 0x80

                    if (isRLE) {
                        val index = readInt16LE() ?: return@refill null
                        repeat(packetSize) { heap.writeInt16LE(index) }
                    } else {
                        repeat(packetSize) {
                            heap.writeInt16LE(readInt16LE() ?: return@refill null)
                        }
                    }
                }

                heap.seek(startPos.toLong(), EnumSeekMode.FROM_BEGINNING)
                heap.readExact(buffer)
            }, null)
            else -> return KorneaResult.errorAsIllegalArgument(-1, "Invalid pixel depth $pixelDepth")
        }
    } else {
        when (pixelDepth) {
            8 -> return KorneaResult.success(refill@{ buffer ->
                val startPos = heap.position()
                while ((heap.size()!! - startPos).toInt() < buffer.size) {
                    val packetHeader = read() ?: return@refill null
                    val packetSize = (packetHeader and 0x7F) + 1
                    val isRLE = packetHeader and 0x80 == 0x80

                    if (isRLE) {
                        val colour = read() ?: return@refill null
                        repeat(packetSize) { heap.write(colour) }
                    } else {
                        repeat(packetSize) {
                            heap.write(read() ?: return@refill null)
                        }
                    }
                }

                heap.seek(startPos.toLong(), EnumSeekMode.FROM_BEGINNING)
                heap.readExact(buffer)
            }, null)
            16 -> return KorneaResult.success(refill@{ buffer ->
                val startPos = heap.position()
                while ((heap.size()!! - startPos).toInt() < buffer.size) {
                    val packetHeader = read() ?: return@refill null
                    val packetSize = (packetHeader and 0x7F) + 1
                    val isRLE = packetHeader and 0x80 == 0x80

                    if (isRLE) {
                        val colour = readInt16LE() ?: return@refill null
                        repeat(packetSize) { heap.writeInt16LE(colour) }
                    } else {
                        repeat(packetSize) {
                            heap.writeInt16LE(readInt16LE() ?: return@refill null)
                        }
                    }
                }

                heap.seek(startPos.toLong(), EnumSeekMode.FROM_BEGINNING)
                heap.readExact(buffer)
            }, null)
            24 -> return KorneaResult.success(refill@{ buffer ->
                val startPos = heap.position()
                while ((heap.size()!! - startPos).toInt() < buffer.size) {
                    val packetHeader = read() ?: return@refill null
                    val packetSize = (packetHeader and 0x7F) + 1
                    val isRLE = packetHeader and 0x80 == 0x80

                    if (isRLE) {
                        val colour = readInt24LE() ?: return@refill null
                        repeat(packetSize) { heap.writeInt24LE(colour) }
                    } else {
                        repeat(packetSize) {
                            heap.writeInt24LE(readInt24LE() ?: return@refill null)
                        }
                    }
                }

                heap.seek(startPos.toLong(), EnumSeekMode.FROM_BEGINNING)
                heap.readExact(buffer)
            }, null)
            32 -> return KorneaResult.success(refill@{ buffer ->
                val startPos = heap.position()
                while ((heap.size()!! - startPos).toInt() < buffer.size) {
                    val packetHeader = read() ?: return@refill null
                    val packetSize = (packetHeader and 0x7F) + 1
                    val isRLE = packetHeader and 0x80 == 0x80

                    if (isRLE) {
                        val colour = readInt32LE() ?: return@refill null
                        repeat(packetSize) { heap.writeInt32LE(colour) }
                    } else {
                        repeat(packetSize) {
                            heap.writeInt32LE(readInt32LE() ?: return@refill null)
                        }
                    }
                }

                heap.seek(startPos.toLong(), EnumSeekMode.FROM_BEGINNING)
                heap.readExact(buffer)
            }, null)
            else -> return KorneaResult.errorAsIllegalArgument(-1, "Invalid pixel depth $pixelDepth")
        }
    }
}

//TODO: Confirm 32 bit true RGBA images
suspend fun InputFlow.readTargaImage(): KorneaResult<TargaImage> {
    val idLength = read() ?: return korneaNotEnoughData()
    val colourMapType = read() ?: return korneaNotEnoughData()
    val imageType = read() ?: return korneaNotEnoughData()

    val imageTypeEncoding = imageType and 0b111
    val imageTypeRunLengthEncoded = imageType and 0b1000 == 0b1000

    val colourMapFirstEntryIndex = readInt16LE() ?: return korneaNotEnoughData()
    val colourMapLength = readInt16LE() ?: return korneaNotEnoughData()
    val colourMapEntrySize = read() ?: return korneaNotEnoughData()

    val xOrigin = readInt16LE() ?: return korneaNotEnoughData()
    val yOrigin = readInt16LE() ?: return korneaNotEnoughData()
    val imageWidth = readInt16LE() ?: return korneaNotEnoughData()
    val imageHeight = readInt16LE() ?: return korneaNotEnoughData()
    val pixelDepth = read() ?: return korneaNotEnoughData()
    val imageDescriptor = read() ?: return korneaNotEnoughData()

    val verticalRange =
        if (imageDescriptor and 0b100000 == 0b100000) (0 until imageHeight) else (0 until imageHeight).reversed()
    val horizontalRange =
        if (imageDescriptor and 0b10000 == 0b10000) (0 until imageWidth).reversed() else (0 until imageWidth)

    val imageID = readExact(idLength) ?: return korneaNotEnoughData()

    val colourMap = if (colourMapType == 0) null else
        when (colourMapEntrySize) {
            8 -> IntArray(colourMapLength) { read() ?: return korneaNotEnoughData() }
            16 -> IntArray(colourMapLength) { readInt16LE() ?: return korneaNotEnoughData() }
            24 -> IntArray(colourMapLength) { readInt24LE() ?: return korneaNotEnoughData() }
            32 -> IntArray(colourMapLength) { readInt32LE() ?: return korneaNotEnoughData() }
            else -> return KorneaResult.errorAsIllegalArgument(
                -1,
                "Invalid colour map entry size $colourMapEntrySize"
            )
        }

    val pixels = IntArray(imageWidth * imageHeight)
    val rowBuffer = ByteArray(imageWidth * (pixelDepth / 8))
    val refillBuffer: suspend (array: ByteArray) -> ByteArray? =
        if (imageTypeRunLengthEncoded) createRLERefill(
            BinaryPipeFlow(),
            pixelDepth,
            colourMap != null
        ).getOrBreak { return it }
        else this::readExact

    when (imageTypeEncoding) {
        //No image data is present
        0 -> {
        }
        //Colour-Mapped Image
        1 -> {
            when (pixelDepth) {
                8 -> verticalRange.forEach { y ->
                    refillBuffer(rowBuffer) ?: return korneaNotEnoughData()

                    horizontalRange.forEachIndexed { i, x ->
                        pixels[x + (y * imageWidth)] = rowBuffer[i].toInt() and 0xFF
                    }
                }
                16 -> verticalRange.forEach { y ->
                    refillBuffer(rowBuffer) ?: return korneaNotEnoughData()

                    horizontalRange.forEachIndexed { i, x ->
                        pixels[x + (y * imageWidth)] = rowBuffer.readInt16LE(i * 2)!!
                    }
                }
                else -> return KorneaResult.errorAsIllegalArgument(-1, "Invalid pixel depth $pixelDepth")
            }
        }
        //True-Colour Image
        2 -> {
            when (pixelDepth) {
                16 -> verticalRange.forEach { y ->
                    refillBuffer(rowBuffer) ?: return korneaNotEnoughData()

                    horizontalRange.forEachIndexed { i, x ->
                        pixels[x + (y * imageWidth)] = rowBuffer.readInt16LE(i * 2)!!
                    }
                }
                24 -> verticalRange.forEach { y ->
                    refillBuffer(rowBuffer) ?: return korneaNotEnoughData()

                    horizontalRange.forEachIndexed { i, x ->
                        pixels[x + (y * imageWidth)] = rowBuffer.readInt24LE(i * 3)!!
                    }
                }
                32 -> verticalRange.forEach { y ->
                    refillBuffer(rowBuffer) ?: return korneaNotEnoughData()

                    horizontalRange.forEachIndexed { i, x ->
                        pixels[x + (y * imageWidth)] = rowBuffer.readInt32LE(i * 4)!!
                    }
                }
                else -> return KorneaResult.errorAsIllegalArgument(-1, "Invalid pixel depth $pixelDepth")
            }
        }

        //Black-And-White (Grayscale) Image
        3 -> {
            when (pixelDepth) {
                8 -> verticalRange.forEach { y ->
                    refillBuffer(rowBuffer) ?: return korneaNotEnoughData()

                    horizontalRange.forEachIndexed { i, x ->
                        pixels[x + (y * imageWidth)] = rowBuffer[i].toInt() and 0xFF
                    }
                }
                16 -> verticalRange.forEach { y ->
                    refillBuffer(rowBuffer) ?: return korneaNotEnoughData()

                    horizontalRange.forEachIndexed { i, x ->
                        pixels[x + (y * imageWidth)] = rowBuffer.readInt16LE(i * 2)!!
                    }
                }
                else -> return KorneaResult.errorAsIllegalArgument(-1, "Invalid pixel depth $pixelDepth")
            }
        }
        else -> return KorneaResult.errorAsIllegalArgument(
            -1,
            "Invalid image type encoding $imageTypeEncoding / $imageType"
        )
    }

    //Read extension data

    val remainingPos = position()
    val remainingData = readBytes(dataSize = 1_000_000) //We don't want to read too much data here!

    //Now we read the extension area
    val extensionArea: TargaImageExtension?

    if (remainingData.size > 26) {
        val fileFooterIndex = remainingData.size - 26

        val extensionOffset = remainingData.readInt32LE(fileFooterIndex + 0)
        val developerAreaOffset = remainingData.readInt32LE(fileFooterIndex + 4)

        require(remainingData.readInt64LE(fileFooterIndex + 8) == 0x4953495645555254)
        require(remainingData.readInt64LE(fileFooterIndex + 16) == 0x454c4946582d4e4f)

        require(remainingData.readInt16LE(fileFooterIndex + 24) == 0x002E)

        if (extensionOffset != null && extensionOffset > 0) {
            val extensionIndex = extensionOffset - remainingPos.toInt()

            val extensionSize = remainingData.readInt16LE(extensionIndex) ?: return korneaNotEnoughData()
            require(extensionSize == 495)

            val authorName =
                remainingData.copyOfRange(extensionIndex + 2, extensionIndex + 2 + 41).decodeToString().trim('\u0000')

            val comments = Array(4) {
                remainingData.copyOfRange(
                    extensionIndex + 2 + 41 + it * 81,
                    extensionIndex + 2 + 41 + (it * 81) + 80
                ).decodeToString().trim('\u0000')
            }

            val monthSaved = remainingData.readInt16LE(extensionIndex + 0x16F) ?: return korneaNotEnoughData()
            val daySaved = remainingData.readInt16LE(extensionIndex + 0x171) ?: return korneaNotEnoughData()
            val yearSaved = remainingData.readInt16LE(extensionIndex + 0x173) ?: return korneaNotEnoughData()
            val hourSaved = remainingData.readInt16LE(extensionIndex + 0x175) ?: return korneaNotEnoughData()
            val minuteSaved = remainingData.readInt16LE(extensionIndex + 0x177) ?: return korneaNotEnoughData()
            val secondSaved = remainingData.readInt16LE(extensionIndex + 0x179) ?: return korneaNotEnoughData()

            val jobID = remainingData.copyOfRange(extensionIndex + 0x17B, extensionIndex + 0x1A4).decodeToString()
                .trim('\u0000')
            val jobHoursTaken = remainingData.readInt16LE(extensionIndex + 0x1A4) ?: return korneaNotEnoughData()
            val jobMinutesTaken = remainingData.readInt16LE(extensionIndex + 0x1A6) ?: return korneaNotEnoughData()
            val jobSecondsTaken = remainingData.readInt16LE(extensionIndex + 0x1A8) ?: return korneaNotEnoughData()

            val softwareID = remainingData.copyOfRange(extensionIndex + 0x1AA, extensionIndex + 0x1D3).decodeToString()
                .trim('\u0000')
            val softwareVersion = remainingData.readInt16LE(extensionIndex + 0x1D3) ?: return korneaNotEnoughData()
            val softwareVersionTag = remainingData[extensionIndex + 0x1D5].toChar()

            val keyColour = remainingData.readInt32LE(extensionIndex + 0x1D6) ?: return korneaNotEnoughData()

            val pixelWidth = remainingData.readInt16LE(extensionIndex + 0x1DA) ?: return korneaNotEnoughData()
            val pixelHeight = remainingData.readInt16LE(extensionIndex + 0x1DC) ?: return korneaNotEnoughData()

            val gammaNumerator = remainingData.readInt16LE(extensionIndex + 0x1DE) ?: return korneaNotEnoughData()
            val gammaDenominator = remainingData.readInt16LE(extensionIndex + 0x1E0) ?: return korneaNotEnoughData()

            val colourCorrectionOffset =
                remainingData.readInt32LE(extensionIndex + 0x1E2) ?: return korneaNotEnoughData()
            val postageStampOffset = remainingData.readInt32LE(extensionIndex + 0x1E6) ?: return korneaNotEnoughData()
            val scanLineOffset = remainingData.readInt32LE(extensionIndex + 0x1EA) ?: return korneaNotEnoughData()

            val attributesType = when (val id = remainingData[extensionIndex + 0x1EE].toInt() and 0xFF) {
                0 -> EnumTargaAttributesType.NO_ALPHA_DATA
                1 -> EnumTargaAttributesType.UNDEFINED_IGNORE
                2 -> EnumTargaAttributesType.UNDEFINED_RETAIN
                3 -> EnumTargaAttributesType.USEFUL_ALPHA
                4 -> EnumTargaAttributesType.PRE_MULTIPLIED_ALPHA
                else -> if (id < 128) EnumTargaAttributesType.RESERVED(id) else EnumTargaAttributesType.UNASSIGNED(id)
            }

            extensionArea = TargaImageExtension(
                authorName,
                comments,
                monthSaved,
                daySaved,
                yearSaved,
                hourSaved,
                minuteSaved,
                secondSaved,

                jobID,
                (jobHoursTaken * 60L * 60L) + (jobMinutesTaken * 60L) + (jobSecondsTaken),

                softwareID,
                softwareVersion,
                softwareVersionTag,

                keyColour,

                pixelWidth,
                pixelHeight,

                gammaNumerator,
                gammaDenominator,

                colourCorrectionOffset,
                postageStampOffset,
                scanLineOffset,
                attributesType
            )
        } else {
            extensionArea = null
        }
    } else {
        extensionArea = null
    }

    if (colourMap != null) {
        when (colourMapEntrySize) {
            8 -> colourMap.forEachIndexed { i, base ->
                colourMap[i] = argb(0xFF, base, base, base)
            }
            16 -> colourMap.forEachIndexed { i, base ->
                //ARRRRRGG GGGBBBBB
                colourMap[i] = argb(
                    if (extensionArea?.attributesType?.keepAlpha == true)
                        if (base and 0x8000 == 0x8000) 0xFF else 0x00
                    else
                        0xFF,
                    (base shr 10 and 0x1F) * 255 / 31,
                    (base shr 5 and 0x1F) * 255 / 31,
                    (base shr 0 and 0x1F) * 255 / 31
                )
            }
            24 -> {
            }
            32 -> {
            }
            else -> return KorneaResult.errorAsIllegalArgument(
                -1,
                "Invalid colour map entry size $colourMapEntrySize"
            )
        }
    }

    when (imageTypeEncoding) {
        1 -> {
            colourMap ?: return korneaNotEnoughData("Colour map missing")

            pixels.forEachIndexed { pixelIndex, colourMapIndex ->
                pixels[pixelIndex] = colourMap[colourMapIndex - colourMapFirstEntryIndex]
            }
        }
        2 -> when (pixelDepth) {
            16 -> pixels.forEachIndexed { index, base ->
                //ARRRRRGG GGGBBBBB
                pixels[index] = argb(
                    if (extensionArea?.attributesType?.keepAlpha == true)
                        if (base and 0x8000 == 0x8000) 0xFF else 0x00
                    else
                        0xFF,
                    (base shr 10 and 0x1F) * 255 / 31,
                    (base shr 5 and 0x1F) * 255 / 31,
                    (base shr 0 and 0x1F) * 255 / 31
                )
            }
            else -> {
                if (extensionArea?.attributesType?.keepAlpha != true) {
                    pixels.forEachIndexed { index, base ->
                        pixels[index] = (0xFF shl 24) or base
                    }
                }
            }
        }

        3 -> when (pixelDepth) {
            8 -> pixels.forEachIndexed { index, base ->
                pixels[index] = argb(0xFF, base, base, base)
            }
            16 -> pixels.forEachIndexed { index, base ->
                pixels[index] = argb(base shr 8 and 0xFF, base and 0xFF, base and 0xFF, base and 0xFF)
            }
        }
    }

    return KorneaResult.success(
        TargaImage(
            imageID,
            colourMapType,
            imageType,
            colourMap,
            xOrigin,
            yOrigin,
            pixelDepth,
            imageDescriptor,
            extensionArea,
            imageWidth,
            imageHeight,
            pixels
        )
    )
}