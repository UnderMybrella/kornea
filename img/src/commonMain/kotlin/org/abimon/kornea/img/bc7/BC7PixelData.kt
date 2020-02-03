package org.abimon.kornea.img.bc7

import org.abimon.kornea.img.RgbColour
import org.abimon.kornea.img.RgbMatrix
import org.abimon.kornea.img.rgba
import org.abimon.kornea.io.common.flow.BitwiseInputFlow
import org.abimon.kornea.io.common.flow.InputFlow

object BC7PixelData {
    private val NUMBER_OF_SUBSETS = arrayOf(3, 2, 3, 2, 1, 1, 1, 2)
    private val UNIQUE_PBITS = arrayOf(true, false, false, true, false, false, true, true)

    private val COLOUR_PRECISION_PLUS_PBIT = arrayOf(5, 7, 5, 8, 5, 7, 8, 6)
    private val ALPHA_PRECISION_PLUS_PBIT = arrayOf(0, 0, 0, 0, 6, 8, 8, 6)

    private val COLOUR_INDEX_BITCOUNT = arrayOf(3, 3, 2, 2, 2, 2, 4, 2)
    private val ALPHA_INDEX_BITCOUNT = arrayOf(3, 3, 2, 2, 3, 2, 4, 2)

    private val PARTITION_TABLE_P2 = arrayOf(
        0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1,
        0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1,
        0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1,
        0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 1, 1, 1,
        0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 1,
        0, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1,
        0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1,
        0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 1, 1,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1,
        0, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1,
        0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1,
        0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1,
        0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1,
        0, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 0,
        0, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0,
        0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0,
        0, 1, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 1,
        0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0,
        0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0,
        0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0,
        0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 1, 0, 0,
        0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0,
        0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0,
        0, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 0,
        0, 0, 1, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 0,
        0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1,
        0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1,
        0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0,
        0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0,
        0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0,
        0, 1, 0, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 1, 0,
        0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1,
        0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1,
        0, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 0,
        0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1, 0, 0, 0,
        0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 0, 0,
        0, 0, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0,
        0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0,
        0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1,
        0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1,
        0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0,
        0, 1, 0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0,
        0, 0, 1, 0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 0, 0, 1, 0,
        0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 0, 0, 1, 0, 0,
        0, 1, 1, 0, 1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1,
        0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0, 1, 0, 0, 1,
        0, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0,
        0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 0,
        0, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1,
        0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 0, 1,
        0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1,
        0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1,
        0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1,
        0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0,
        0, 0, 1, 0, 0, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0,
        0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1
    )

    private val PARTITION_TABLE_P3 = arrayOf(
        0, 0, 1, 1, 0, 0, 1, 1, 0, 2, 2, 1, 2, 2, 2, 2,
        0, 0, 0, 1, 0, 0, 1, 1, 2, 2, 1, 1, 2, 2, 2, 1,
        0, 0, 0, 0, 2, 0, 0, 1, 2, 2, 1, 1, 2, 2, 1, 1,
        0, 2, 2, 2, 0, 0, 2, 2, 0, 0, 1, 1, 0, 1, 1, 1,
        0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 2, 1, 1, 2, 2,
        0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 2, 2, 0, 0, 2, 2,
        0, 0, 2, 2, 0, 0, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1,
        0, 0, 1, 1, 0, 0, 1, 1, 2, 2, 1, 1, 2, 2, 1, 1,
        0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2,
        0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2,
        0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2,
        0, 0, 1, 2, 0, 0, 1, 2, 0, 0, 1, 2, 0, 0, 1, 2,
        0, 1, 1, 2, 0, 1, 1, 2, 0, 1, 1, 2, 0, 1, 1, 2,
        0, 1, 2, 2, 0, 1, 2, 2, 0, 1, 2, 2, 0, 1, 2, 2,
        0, 0, 1, 1, 0, 1, 1, 2, 1, 1, 2, 2, 1, 2, 2, 2,
        0, 0, 1, 1, 2, 0, 0, 1, 2, 2, 0, 0, 2, 2, 2, 0,
        0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 1, 2, 1, 1, 2, 2,
        0, 1, 1, 1, 0, 0, 1, 1, 2, 0, 0, 1, 2, 2, 0, 0,
        0, 0, 0, 0, 1, 1, 2, 2, 1, 1, 2, 2, 1, 1, 2, 2,
        0, 0, 2, 2, 0, 0, 2, 2, 0, 0, 2, 2, 1, 1, 1, 1,
        0, 1, 1, 1, 0, 1, 1, 1, 0, 2, 2, 2, 0, 2, 2, 2,
        0, 0, 0, 1, 0, 0, 0, 1, 2, 2, 2, 1, 2, 2, 2, 1,
        0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 2, 2, 0, 1, 2, 2,
        0, 0, 0, 0, 1, 1, 0, 0, 2, 2, 1, 0, 2, 2, 1, 0,
        0, 1, 2, 2, 0, 1, 2, 2, 0, 0, 1, 1, 0, 0, 0, 0,
        0, 0, 1, 2, 0, 0, 1, 2, 1, 1, 2, 2, 2, 2, 2, 2,
        0, 1, 1, 0, 1, 2, 2, 1, 1, 2, 2, 1, 0, 1, 1, 0,
        0, 0, 0, 0, 0, 1, 1, 0, 1, 2, 2, 1, 1, 2, 2, 1,
        0, 0, 2, 2, 1, 1, 0, 2, 1, 1, 0, 2, 0, 0, 2, 2,
        0, 1, 1, 0, 0, 1, 1, 0, 2, 0, 0, 2, 2, 2, 2, 2,
        0, 0, 1, 1, 0, 1, 2, 2, 0, 1, 2, 2, 0, 0, 1, 1,
        0, 0, 0, 0, 2, 0, 0, 0, 2, 2, 1, 1, 2, 2, 2, 1,
        0, 0, 0, 0, 0, 0, 0, 2, 1, 1, 2, 2, 1, 2, 2, 2,
        0, 2, 2, 2, 0, 0, 2, 2, 0, 0, 1, 2, 0, 0, 1, 1,
        0, 0, 1, 1, 0, 0, 1, 2, 0, 0, 2, 2, 0, 2, 2, 2,
        0, 1, 2, 0, 0, 1, 2, 0, 0, 1, 2, 0, 0, 1, 2, 0,
        0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 0, 0, 0, 0,
        0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1, 2, 0,
        0, 1, 2, 0, 2, 0, 1, 2, 1, 2, 0, 1, 0, 1, 2, 0,
        0, 0, 1, 1, 2, 2, 0, 0, 1, 1, 2, 2, 0, 0, 1, 1,
        0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 0, 0, 0, 0, 1, 1,
        0, 1, 0, 1, 0, 1, 0, 1, 2, 2, 2, 2, 2, 2, 2, 2,
        0, 0, 0, 0, 0, 0, 0, 0, 2, 1, 2, 1, 2, 1, 2, 1,
        0, 0, 2, 2, 1, 1, 2, 2, 0, 0, 2, 2, 1, 1, 2, 2,
        0, 0, 2, 2, 0, 0, 1, 1, 0, 0, 2, 2, 0, 0, 1, 1,
        0, 2, 2, 0, 1, 2, 2, 1, 0, 2, 2, 0, 1, 2, 2, 1,
        0, 1, 0, 1, 2, 2, 2, 2, 2, 2, 2, 2, 0, 1, 0, 1,
        0, 0, 0, 0, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1,
        0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 2, 2, 2, 2,
        0, 2, 2, 2, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1,
        0, 0, 0, 2, 1, 1, 1, 2, 0, 0, 0, 2, 1, 1, 1, 2,
        0, 0, 0, 0, 2, 1, 1, 2, 2, 1, 1, 2, 2, 1, 1, 2,
        0, 2, 2, 2, 0, 1, 1, 1, 0, 1, 1, 1, 0, 2, 2, 2,
        0, 0, 0, 2, 1, 1, 1, 2, 1, 1, 1, 2, 0, 0, 0, 2,
        0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 2, 2, 2, 2,
        0, 0, 0, 0, 0, 0, 0, 0, 2, 1, 1, 2, 2, 1, 1, 2,
        0, 1, 1, 0, 0, 1, 1, 0, 2, 2, 2, 2, 2, 2, 2, 2,
        0, 0, 2, 2, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 2, 2,
        0, 0, 2, 2, 1, 1, 2, 2, 1, 1, 2, 2, 0, 0, 2, 2,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1, 1, 2,
        0, 0, 0, 2, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 1,
        0, 2, 2, 2, 1, 2, 2, 2, 0, 2, 2, 2, 1, 2, 2, 2,
        0, 1, 0, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
        0, 1, 1, 1, 2, 0, 1, 1, 2, 2, 0, 1, 2, 2, 2, 0
    )

    private val ANCHOR_INDEX_SECOND_SUBSET = arrayOf(
        15, 15, 15, 15, 15, 15, 15, 15,
        15, 15, 15, 15, 15, 15, 15, 15,
        15, 2, 8, 2, 2, 8, 8, 15,
        2, 8, 2, 2, 8, 8, 2, 2,
        15, 15, 6, 8, 2, 8, 15, 15,
        2, 8, 2, 2, 2, 15, 15, 6,
        6, 2, 6, 8, 15, 15, 2, 2,
        15, 15, 15, 15, 15, 2, 2, 15
    )

    private val ANCHOR_INDEX_SECOND_SUBSET_OF_THREE = arrayOf(
        3, 3, 15, 15, 8, 3, 15, 15,
        8, 8, 6, 6, 6, 5, 3, 3,
        3, 3, 8, 15, 3, 3, 6, 10,
        5, 8, 8, 6, 8, 5, 15, 15,
        8, 15, 3, 5, 6, 10, 8, 15,
        15, 3, 15, 5, 15, 15, 15, 15,
        3, 15, 5, 5, 5, 8, 5, 10,
        5, 10, 8, 13, 15, 12, 3, 3
    )

    private val ANCHOR_INDEX_THIRD_SUBSET = arrayOf(
        15, 8, 8, 3, 15, 15, 3, 8,
        15, 15, 15, 15, 15, 15, 15, 8,
        15, 8, 15, 3, 15, 8, 15, 8,
        3, 15, 6, 10, 15, 15, 10, 8,
        15, 3, 15, 10, 10, 8, 9, 10,
        6, 15, 8, 15, 3, 6, 6, 8,
        15, 3, 15, 15, 15, 15, 15, 15,
        15, 15, 15, 15, 3, 15, 15, 8
    )

    private val A_WEIGHT_2 = arrayOf(0, 21, 43, 64)
    private val A_WEIGHT_3 = arrayOf(0, 9, 18, 27, 37, 46, 55, 64)
    private val A_WEIGHT_4 = arrayOf(0, 4, 9, 13, 17, 21, 26, 30, 34, 38, 43, 47, 51, 55, 60, 64)

    @ExperimentalUnsignedTypes
    suspend fun read(width: Int, height: Int, flow: InputFlow): RgbMatrix {
        val rgb = RgbMatrix(width, height)

        @Suppress("NAME_SHADOWING")
        val flow = BitwiseInputFlow(flow)
        var startingPos: Int

        flow.checkAfter() //Make the pos work

        loop@ for (supposedIndex in 0 until ((height * width) / 16)) {
            val mode: BC7Mode
            var modeBit = 0
            startingPos = flow.position().toInt() - 1

            for (i in 0 until 8) {
                if (requireNotNull(flow.readBit()) == 1)
                    break
                modeBit++
            }

            when (modeBit) {
                0 -> {
                    val partition = requireNotNull(flow.readNumber(4)).toInt()
                    val red = IntArray(6) { requireNotNull(flow.readNumber(4)).toInt() }
                    val green = IntArray(6) { requireNotNull(flow.readNumber(4)).toInt() }
                    val blue = IntArray(6) { requireNotNull(flow.readNumber(4)).toInt() }
                    val p = IntArray(6) { requireNotNull(flow.readBit()) }
                    val indices =
                        IntArray(16) { i ->
                            if (isAnchorIndex(partition, modeBit, i))
                                requireNotNull(flow.readNumber(2)).toInt()
                            else
                                requireNotNull(flow.readNumber(3)).toInt()
                        }

                    mode = BC7Mode(modeBit, partition, red, green, blue, null, p, indices, null, null, null)
                }
                1 -> {
                    val partition = requireNotNull(flow.readNumber(6)).toInt()
                    val red = IntArray(4) { requireNotNull(flow.readNumber(6)).toInt() }
                    val green = IntArray(4) { requireNotNull(flow.readNumber(6)).toInt() }
                    val blue = IntArray(4) { requireNotNull(flow.readNumber(6)).toInt() }
                    val p = IntArray(2) { requireNotNull(flow.readBit()) }
                    val indices =
                        IntArray(16) { i ->
                            if (isAnchorIndex(partition, modeBit, i))
                                requireNotNull(flow.readNumber(2)).toInt()
                            else
                                requireNotNull(flow.readNumber(3)).toInt()
                        }

                    mode = BC7Mode(modeBit, partition, red, green, blue, null, p, indices, null, null, null)
                }
                2 -> {
                    val partition = requireNotNull(flow.readNumber(6)).toInt()
                    val red = IntArray(6) { requireNotNull(flow.readNumber(5)).toInt() }
                    val green = IntArray(6) { requireNotNull(flow.readNumber(5)).toInt() }
                    val blue = IntArray(6) { requireNotNull(flow.readNumber(5)).toInt() }
                    val indices =
                        IntArray(16) { i ->
                            if (isAnchorIndex(partition, modeBit, i))
                                requireNotNull(flow.readBit()).toInt()
                            else
                                requireNotNull(flow.readNumber(2)).toInt()
                        }

                    mode = BC7Mode(modeBit, partition, red, green, blue, null, null, indices, null, null, null)
                }
                3 -> {
                    val partition = requireNotNull(flow.readNumber(6)).toInt()
                    val red = IntArray(4) { requireNotNull(flow.readNumber(7)).toInt() }
                    val green = IntArray(4) { requireNotNull(flow.readNumber(7)).toInt() }
                    val blue = IntArray(4) { requireNotNull(flow.readNumber(7)).toInt() }
                    val p = IntArray(4) { requireNotNull(flow.readBit()) }

                    val indices = IntArray(16) { i ->
                        if (isAnchorIndex(partition, modeBit, i))
                            requireNotNull(flow.readBit()).toInt()
                        else
                            requireNotNull(flow.readNumber(2)).toInt()
                    }

                    mode = BC7Mode(modeBit, partition, red, green, blue, null, p, indices, null, null, null)
                }
                4 -> {
                    val rotation = requireNotNull(flow.readNumber(2)).toInt()
                    val idxMode = requireNotNull(flow.readBit()).toInt()

                    val red = IntArray(2) { requireNotNull(flow.readNumber(5)).toInt() }
                    val green = IntArray(2) { requireNotNull(flow.readNumber(5)).toInt() }
                    val blue = IntArray(2) { requireNotNull(flow.readNumber(5)).toInt() }
                    val alpha = IntArray(2) { requireNotNull(flow.readNumber(6)).toInt() }

                    val twoBitIndices = IntArray(16) { i ->
                        if (isAnchorIndex(0, modeBit, i))
                            requireNotNull(flow.readBit()).toInt()
                        else
                            requireNotNull(flow.readNumber(2)).toInt()
                    }
                    val threeBitIndices = IntArray(16) { i ->
                        if (isAnchorIndex(0, modeBit, i))
                            requireNotNull(flow.readNumber(2)).toInt()
                        else
                            requireNotNull(flow.readNumber(3)).toInt()
                    }

                    mode = BC7Mode(
                        modeBit,
                        0,
                        red,
                        green,
                        blue,
                        alpha,
                        null,
                        twoBitIndices,
                        threeBitIndices,
                        rotation,
                        idxMode
                    )
                }
                5 -> {
                    val rotation = requireNotNull(flow.readNumber(2)).toInt()

                    val red = IntArray(2) { requireNotNull(flow.readNumber(7)).toInt() }
                    val green = IntArray(2) { requireNotNull(flow.readNumber(7)).toInt() }
                    val blue = IntArray(2) { requireNotNull(flow.readNumber(7)).toInt() }
                    val alpha = IntArray(2) { requireNotNull(flow.readNumber(8)).toInt() }

                    val rgbIndices = IntArray(16) { i ->
                        if (isAnchorIndex(0, modeBit, i))
                            requireNotNull(flow.readBit()).toInt()
                        else
                            requireNotNull(flow.readNumber(2)).toInt()
                    }

                    val alphaIndices = IntArray(16) { i ->
                        if (isAnchorIndex(0, modeBit, i))
                            requireNotNull(flow.readBit()).toInt()
                        else
                            requireNotNull(flow.readNumber(2)).toInt()
                    }

                    mode = BC7Mode(modeBit, 0, red, green, blue, alpha, null, rgbIndices, alphaIndices, rotation, null)
                }
                6 -> {
                    val red = IntArray(2) { requireNotNull(flow.readNumber(7)).toInt() }
                    val green = IntArray(2) { requireNotNull(flow.readNumber(7)).toInt() }
                    val blue = IntArray(2) { requireNotNull(flow.readNumber(7)).toInt() }
                    val alpha = IntArray(2) { requireNotNull(flow.readNumber(7)).toInt() }
                    val p = IntArray(2) { requireNotNull(flow.readBit()).toInt() }
                    val indices = IntArray(16) { i ->
                        if (isAnchorIndex(0, modeBit, i))
                            requireNotNull(flow.readNumber(3)).toInt()
                        else
                            requireNotNull(flow.readNumber(4)).toInt()
                    }

                    mode = BC7Mode(modeBit, 0, red, green, blue, alpha, p, indices, null, null, null)
                }
                7 -> {
                    val partition = requireNotNull(flow.readNumber(6)).toInt()
                    val red = IntArray(4) { requireNotNull(flow.readNumber(5)).toInt() }
                    val green = IntArray(4) { requireNotNull(flow.readNumber(5)).toInt() }
                    val blue = IntArray(4) { requireNotNull(flow.readNumber(5)).toInt() }
                    val alpha = IntArray(4) { requireNotNull(flow.readNumber(5)).toInt() }
                    val p = IntArray(4) { requireNotNull(flow.readBit()).toInt() }
                    val indices = IntArray(16) { i ->
                        if (isAnchorIndex(partition, modeBit, i))
                            requireNotNull(flow.readBit()).toInt()
                        else
                            requireNotNull(flow.readNumber(2)).toInt()
                    }

                    mode = BC7Mode(modeBit, partition, red, green, blue, alpha, p, indices, null, null, null)
                }
                else -> {
                    println("Mode: $modeBit")
                    flow.skipBits(127 - modeBit)
//                    val buffer = block.read(127 - modeBit)
                    continue@loop
                }
            }

            val numberOfSubsets = NUMBER_OF_SUBSETS[modeBit]
            val endpoints = getEndpoints(mode)

            for (index in 0 until 16) {
                val rgbPalette = mode.indices[index]
                val alphaPalette = (mode.alphaIndices ?: mode.indices)[index]

                val subset = getSubset(mode.partitions, index, numberOfSubsets)
                val endpoint = endpoints[subset]
                val interpolated = interpolate(endpoint, rgbPalette, alphaPalette, mode.selectionBit, mode.mode)

                val x = (supposedIndex % (width / 4)) * 4 + (index % 4)
                val y = (supposedIndex / (width / 4)) * 4 + (index / 4)

                when (mode.rotation) {
                    1 -> rgb[x, y] = rgba(interpolated.alpha, interpolated.green, interpolated.blue, interpolated.red)
                    2 -> rgb[x, y] = rgba(interpolated.red, interpolated.alpha, interpolated.blue, interpolated.green)
                    3 -> rgb[x, y] = rgba(interpolated.red, interpolated.green, interpolated.alpha, interpolated.blue)
                    else -> rgb[x, y] = interpolated.rgb
                }
            }

            val currentPosition = flow.position().toInt()
            val bytesRead = (currentPosition - startingPos)
            val bitsUnread = (8 - flow.currentPos)
            val bitsToSkip = 128 - (bytesRead * 8) + bitsUnread
            flow.skipBits(bitsToSkip)
        }

        return rgb
    }

    private fun getSubset(partitions: Int, index: Int, numberOfSubsets: Int): Int =
        when (numberOfSubsets) {
            1 -> 0
            2 -> PARTITION_TABLE_P2[partitions * 16 + index]
            else -> PARTITION_TABLE_P3[partitions * 16 + index]
        }

    private fun isAnchorIndex(partitions: Int, mode: Int, index: Int): Boolean =
        when {
            index == 0 -> true
            NUMBER_OF_SUBSETS[mode] == 2 -> ANCHOR_INDEX_SECOND_SUBSET[partitions] == index
            NUMBER_OF_SUBSETS[mode] == 3 -> ANCHOR_INDEX_SECOND_SUBSET_OF_THREE[partitions] == index || ANCHOR_INDEX_THIRD_SUBSET[partitions] == index
            else -> false
        }

    private fun extractEndpoints(mode: BC7Mode): Array<Pair<IntArray, IntArray>> {
        val numSubsets = NUMBER_OF_SUBSETS[mode.mode]
        return Array(numSubsets) { subset ->
            intArrayOf(
                mode.red[subset * 2],
                mode.green[subset * 2],
                mode.blue[subset * 2],
                mode.alpha?.get(subset * 2) ?: -1
            ) to intArrayOf(
                mode.red[subset * 2 + 1],
                mode.green[subset * 2 + 1],
                mode.blue[subset * 2 + 1],
                mode.alpha?.get(subset * 2 + 1) ?: -1
            )
        }
    }

    private fun getEndpoints(mode: BC7Mode): Array<Pair<RgbColour, RgbColour>> {
        val endpoints = extractEndpoints(mode)

        val colourPrecision = COLOUR_PRECISION_PLUS_PBIT[mode.mode]
        val alphaPrecision = ALPHA_PRECISION_PLUS_PBIT[mode.mode]

        for (i in endpoints.indices) {
            if (mode.pBits != null) {
                if (UNIQUE_PBITS[mode.mode]) {
                    with(endpoints[i].first) {
                        for (j in indices) {
                            this[j] = this[j] shl 1
                            this[j] = this[j] or mode.pBits[i * 2]
                        }
                    }
                    with(endpoints[i].second) {
                        for (j in indices) {
                            this[j] = this[j] shl 1
                            this[j] = this[j] or mode.pBits[i * 2 + 1]
                        }
                    }
                } else {
                    with(endpoints[i].first) {
                        for (j in indices) {
                            this[j] = this[j] shl 1
                            this[j] = this[j] or mode.pBits[i]
                        }
                    }
                    with(endpoints[i].second) {
                        for (j in indices) {
                            this[j] = this[j] shl 1
                            this[j] = this[j] or mode.pBits[i]
                        }
                    }
                }
            }
            fun shiftEndpoint(endpoint: IntArray) {
                for (ei in 0 until 3) endpoint[ei] = endpoint[ei] shl (8 - colourPrecision)
                endpoint[3] = endpoint[3] shl (8 - alphaPrecision)

                for (ei in 0 until 3) endpoint[ei] = endpoint[ei] or (endpoint[ei] shr colourPrecision)
                endpoint[3] = endpoint[3] or (endpoint[3] shr alphaPrecision)
            }

            shiftEndpoint(endpoints[i].first)
            shiftEndpoint(endpoints[i].second)
        }

        if (mode.mode <= 3) {
            for (i in endpoints.indices) {
                endpoints[i].first[3] = 0xFF
                endpoints[i].second[3] = 0xFF
            }
        }

        return Array(endpoints.size) { index ->
            val (a, b) = endpoints[index]
            RgbColour.rgba(a[0], a[1], a[2], a[3]) to RgbColour.rgba(b[0], b[1], b[2], b[3])
        }
    }

    private fun interpolate(
        endpoints: Pair<RgbColour, RgbColour>,
        index: Int,
        alphaIndex: Int,
        selectionBit: Int?,
        mode: Int
    ): RgbColour {
        if (selectionBit == 1) {
            val r = interpolate(endpoints.first.red, endpoints.second.red, alphaIndex, ALPHA_INDEX_BITCOUNT[mode])
            val g = interpolate(endpoints.first.green, endpoints.second.green, alphaIndex, ALPHA_INDEX_BITCOUNT[mode])
            val b = interpolate(endpoints.first.blue, endpoints.second.blue, alphaIndex, ALPHA_INDEX_BITCOUNT[mode])
            val a = interpolate(endpoints.first.alpha, endpoints.second.alpha, index, COLOUR_INDEX_BITCOUNT[mode])

            return RgbColour.rgba(r, g, b, a)
        } else {
            val r = interpolate(endpoints.first.red, endpoints.second.red, index, COLOUR_INDEX_BITCOUNT[mode])
            val g = interpolate(endpoints.first.green, endpoints.second.green, index, COLOUR_INDEX_BITCOUNT[mode])
            val b = interpolate(endpoints.first.blue, endpoints.second.blue, index, COLOUR_INDEX_BITCOUNT[mode])
            val a = interpolate(endpoints.first.alpha, endpoints.second.alpha, alphaIndex, ALPHA_INDEX_BITCOUNT[mode])

            return RgbColour.rgba(r, g, b, a)
        }
    }
//= Color(endpoints.first[0], endpoints.first[1], endpoints.first[2], endpoints.first[3])

    private fun interpolate(e0: Int, e1: Int, index: Int, indexPrecision: Int): Int =
        when (indexPrecision) {
            2 -> (((64 - A_WEIGHT_2[index]) * e0 + A_WEIGHT_2[index] * e1 + 32) shr 6)
            3 -> (((64 - A_WEIGHT_3[index]) * e0 + A_WEIGHT_3[index] * e1 + 32) shr 6)
            else -> (((64 - A_WEIGHT_4[index]) * e0 + A_WEIGHT_4[index] * e1 + 32) shr 6)
        }
}