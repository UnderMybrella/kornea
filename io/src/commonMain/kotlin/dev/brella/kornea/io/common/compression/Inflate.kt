package dev.brella.kornea.io.common.compression

import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.errors.common.korneaNotEnoughData
import dev.brella.kornea.io.common.flow.BitwiseInputFlow
import dev.brella.kornea.io.common.flow.InputFlow
import dev.brella.kornea.io.common.flow.readBytes

private val CODE_LENGTH_ALPHABET = intArrayOf(16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15)

public suspend fun InputFlow.inflate(): KorneaResult<ByteArray> {
    val data: MutableList<Byte> = ArrayList()
    val flow = BitwiseInputFlow(this)

    while (true) {
        val isFinal = flow.readBoolean() ?: return korneaNotEnoughData()
        val compressionType = flow.readNumber(2) ?: return korneaNotEnoughData()

        when (compressionType) {
            // 00 - no compression
            0b00L -> {
                /**
                 * skip any remaining bits in current partially
                 * processed byte
                 * read LEN and NLEN (see next section)
                 * copy LEN bytes of data to output
                 */

                flow.skipRemainingBits()

                val len = flow.read() ?: return korneaNotEnoughData()
                val nlen = flow.read() ?: return korneaNotEnoughData()

                flow.readBytes(dataSize = len).forEach(data::add)
            }
            // 01 - compressed with fixed Huffman codes
            0b01L -> {
                /**
                if compressed with dynamic Huffman codes
                read representation of code trees (see
                subsection below)
                loop (until end of block code recognized)
                decode literal/length value from input stream
                if value < 256
                copy value (literal byte) to output stream
                otherwise
                if value = end of block (256)
                break from loop
                otherwise (value = 257..285)
                decode distance from input stream

                move backwards distance bytes in the output
                stream, and copy length bytes from this
                position to the output stream.
                end loop
                 */
                println()
            }
            // 10 - compressed with dynamic Huffman codes
            0b10L -> {
                println("==Dynamic==")
                val numberOfLiteralOrLengthCodes = flow.readNumber(5)?.plus(257) ?: return korneaNotEnoughData()
                val numberOfDistanceCodes = flow.readNumber(5)?.plus(1) ?: return korneaNotEnoughData()
                val numberOfCodeLengthCodes = flow.readNumber(4)?.plus(4) ?: return korneaNotEnoughData()

                val codeLengths = HashMap<Int, Int>(numberOfCodeLengthCodes.toInt()).apply {
                    repeat(numberOfCodeLengthCodes.toInt()) { i ->
                        this[CODE_LENGTH_ALPHABET[i]] = flow.readNumber(3)?.toInt() ?: return korneaNotEnoughData()
                    }
                }

                /**
                 * 1)   Count the number of codes for each code length.  Let
                        bl_count[N] be the number of codes of length N, N >= 1.
                 */
                val blCount = IntArray(codeLengths.values.maxOrNull()!! + 1) { i -> codeLengths.values.count(i::equals) }

                /**
                 * 2)   Find the numerical value of the smallest code for each
                        code length:
                 */
                var code = 0
                blCount[0] = 0
                val nextCode = IntArray(blCount.size + 1)

                for (bits in 1 .. blCount.size) {
                    code = (code + blCount[bits - 1]) shl 1
                    nextCode[bits] = code
                }

                /**
                 * 3)   Assign numerical values to all codes, using consecutive
                        values for all codes of the same length with the base
                        values determined at step 2. Codes that are never used
                        (which have a bit length of zero) must not be assigned a
                        value.
                 */
                val codeLengthAlphabet = HashMap<Int, Int>()
                for (n in 0 .. nextCode.maxOrNull()!!) {
                    val len = codeLengths[n]
                    if (len != null && len != 0) {
                        codeLengthAlphabet[nextCode[len]] = n
                        nextCode[len]++
                    }
                }

                println(codeLengthAlphabet)

                val codeLengthMax = codeLengths.values.maxOrNull()!!

                val literalLengthAlphabet = IntArray(numberOfLiteralOrLengthCodes.toInt()) { i ->
                    var num = 0
                    for (j in 0 until codeLengthMax) {
                        when (flow.readBoolean()) {
                            true -> num = (num shl 1) or 1
                            false -> {
                                num = num shl 1
                                break
                            }
                            null -> return korneaNotEnoughData()
                        }
                    }

                    val code = codeLengthAlphabet[num]

                    println(code)

                    num
                }
            }
            // 11 - reserved (error)
            0b11L -> {
            }
        }

        if (isFinal) break
    }

    return KorneaResult.success(data.toByteArray())
}