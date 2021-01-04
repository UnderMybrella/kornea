package dev.brella.kornea.io.common.compression

import dev.brella.kornea.errors.common.KORNEA_ERROR_NOT_ENOUGH_DATA
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.errors.common.korneaNotEnoughData
import dev.brella.kornea.io.common.flow.BitwiseInputFlow
import dev.brella.kornea.io.common.flow.InputFlow
import dev.brella.kornea.io.common.flow.readBytes

private val CODE_LENGTH_ALPHABET = intArrayOf(16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15)

public data class HuffmanCode(val code: Int, val length: Int)
public class HuffmanTree<SYMBOL> {
    private val primaryMap: MutableMap<SYMBOL, HuffmanCode> = HashMap()
    private val codeMap: MutableMap<HuffmanCode, SYMBOL> = HashMap()

    public var minLength: Int = 64
        private set
    public var maxLength: Int = 0
        private set

    public fun getCode(key: SYMBOL): HuffmanCode? = primaryMap[key]
    public fun getSymbol(code: Int, length: Int): SYMBOL? = codeMap[HuffmanCode(code, length)]

    public fun put(symbol: SYMBOL, code: HuffmanCode): HuffmanCode? {
        if (code.length < minLength) minLength = code.length
        if (code.length > maxLength) maxLength = code.length

        val existing = primaryMap.put(symbol, code)
        codeMap[code] = symbol
        return existing
    }
}

public fun <T> notEnoughData(component: String? = null): KorneaResult<T> =
    KorneaResult.errorAsIllegalState(
        KORNEA_ERROR_NOT_ENOUGH_DATA,
        if (component == null) "Not Enough Data" else "Not Enough Data @ $component"
    )

public suspend fun InputFlow.inflate(): KorneaResult<ByteArray> {
    val data: MutableList<Byte> = ArrayList()
    val flow = BitwiseInputFlow(this)

    while (true) {
        val isFinal = flow.readBoolean() ?: return notEnoughData("isFinal")
        val compressionType = flow.readNumber(2) ?: return notEnoughData("compressionType")

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
                val numberOfLiteralOrLengthCodes =
                    flow.readNumber(5)?.plus(257)?.toInt()
                        ?: return korneaNotEnoughData("Not Enough Data @numberOfLiteralOrLengthCodes")
                val numberOfDistanceCodes = flow.readNumber(5)?.plus(1)?.toInt()
                    ?: return korneaNotEnoughData("Not Enoguh Data @ numberOfDistanceCodes")
                val numberOfCodeLengthCodes =
                    flow.readNumber(4)?.plus(4)?.toInt() ?: return notEnoughData("numberOfCodeLengthCodes")

                fun Map<Int, Int>.constructCodesFromCodeLengths(): HuffmanTree<Int> {
                    val codeLengths = this
                    val tree = HuffmanTree<Int>()

                    /**
                     * 1)   Count the number of codes for each code length.  Let
                    bl_count[N] be the number of codes of length N, N >= 1.
                     */
                    val blCount =
                        IntArray(codeLengths.values.maxOrNull()!! + 1) { i -> codeLengths.values.count(i::equals) }

                    /**
                     * 2) Find the numerical value of the smallest code for each
                    code length:
                     */
                    var code = 0
                    blCount[0] = 0
                    val nextCode = IntArray(blCount.size + 1)

                    for (bits in 1..blCount.size) {
                        code = (code + blCount[bits - 1]) shl 1
                        nextCode[bits] = code
                    }

                    /**
                     * 3) Assign numerical values to all codes, using consecutive
                    values for all codes of the same length with the base
                    values determined at step 2. Codes that are never used
                    (which have a bit length of zero) must not be assigned a
                    value.
                     */
                    for (n in 0..nextCode.maxOrNull()!!) {
                        val len = codeLengths[n]
                        if (len != null && len != 0) {
                            tree.put(n, HuffmanCode(nextCode[len], len))
//                            codes[n] = HuffmanCode(nextCode[len], len)
                            nextCode[len]++
                        }
                    }

                    return tree
                }

                val codeLengthAlphabet = HashMap<Int, Int>(numberOfCodeLengthCodes).apply {
                    repeat(numberOfCodeLengthCodes) { i ->
                        this[CODE_LENGTH_ALPHABET[i]] =
                            flow.readNumber(3)?.toInt() ?: return notEnoughData("codeLengthAlphabet[$i]")
                    }
                }.constructCodesFromCodeLengths()

                val baseCodeLengths = HashMap<Int, Int>((numberOfLiteralOrLengthCodes + numberOfDistanceCodes)).apply {
                    var i = 0
                    while (i < (numberOfLiteralOrLengthCodes + numberOfDistanceCodes)) {
                        var num = 0
                        var codeLength: Int? = null
                        for (j in 1..codeLengthAlphabet.maxLength) {
                            num = flow.readBit()?.or(num shl 1) ?: return notEnoughData("baseCodeLengths[$i]")

                            codeLength = codeLengthAlphabet.getSymbol(num, j)
                            if (codeLength != null) break
                        }

                        if (codeLength == null) return KorneaResult.errorAsIllegalState(
                            -1,
                            "Invalid code length $num for alphabet $codeLengthAlphabet"
                        )

                        when (codeLength) {
                            in 0..15 -> this[i++] = codeLength
                            16 -> {
                                val prev = this.getValue(i - 1)
                                val repeatFor =
                                    flow.readNumber(2)?.plus(3)?.toInt() ?: return notEnoughData("repeatFor")

                                repeat(repeatFor) { j ->
                                    this[i + j] = prev
                                }

                                i += repeatFor
                            }
                            17 -> {
                                val repeatFor =
                                    flow.readNumber(3)?.plus(3)?.toInt() ?: return notEnoughData("repeatFor")

                                repeat(repeatFor) { j ->
                                    this[i + j] = 0
                                }

                                i += repeatFor
                            }
                            18 -> {
                                val repeatFor =
                                    flow.readNumber(7)?.plus(11)?.toInt() ?: return notEnoughData("repeatFor")

                                repeat(repeatFor) { j ->
                                    this[i + j] = 0
                                }

                                i += repeatFor
                            }
                            else -> println("Unknown code length $codeLength?")
                        }
                    }
                }

                val literalLengthAlphabet =
                    baseCodeLengths.filter { (k) -> k < numberOfLiteralOrLengthCodes }.constructCodesFromCodeLengths()
                val distanceAlphabet = baseCodeLengths.filter { (k) -> k >= numberOfLiteralOrLengthCodes }
                    .mapKeys { (k) -> k - numberOfLiteralOrLengthCodes }.constructCodesFromCodeLengths()

                var huffmanCode: Int
                var symbol: Int?

                mainLoop@ while (true) {
                    huffmanCode = 0
                    symbol = null

                    for (j in 1..literalLengthAlphabet.maxLength) {
                        huffmanCode = flow.readBit()?.or(huffmanCode shl 1) ?: run {
                            return notEnoughData("huffmanCode[$j]")
                        }

                        symbol = literalLengthAlphabet.getSymbol(huffmanCode, j)
                        if (symbol != null) break
                    }

                    if (symbol == null) return KorneaResult.errorAsIllegalState(
                        -1,
                        "Invalid symbol $huffmanCode"
                    )

                    if (symbol < 256) {
                        data.add(symbol.toByte())
                    } else if (symbol == 256) {
                        break
                    } else {
                        val len: Int
                        val dist: Int

                        len = when (symbol) {
                            256 -> break
                            257 -> 3
                            258 -> 4
                            259 -> 5
                            260 -> 6
                            261 -> 7
                            262 -> 8
                            263 -> 9
                            264 -> 10
                            265 -> flow.readNumber(1)?.plus(11)?.toInt() ?: return korneaNotEnoughData()
                            266 -> flow.readNumber(1)?.plus(13)?.toInt() ?: return korneaNotEnoughData()
                            267 -> flow.readNumber(1)?.plus(15)?.toInt() ?: return korneaNotEnoughData()
                            268 -> flow.readNumber(1)?.plus(17)?.toInt() ?: return korneaNotEnoughData()
                            269 -> flow.readNumber(2)?.plus(19)?.toInt() ?: return korneaNotEnoughData()
                            270 -> flow.readNumber(2)?.plus(23)?.toInt() ?: return korneaNotEnoughData()
                            271 -> flow.readNumber(2)?.plus(27)?.toInt() ?: return korneaNotEnoughData()
                            272 -> flow.readNumber(2)?.plus(31)?.toInt() ?: return korneaNotEnoughData()
                            273 -> flow.readNumber(3)?.plus(35)?.toInt() ?: return korneaNotEnoughData()
                            274 -> flow.readNumber(3)?.plus(43)?.toInt() ?: return korneaNotEnoughData()
                            275 -> flow.readNumber(3)?.plus(51)?.toInt() ?: return korneaNotEnoughData()
                            276 -> flow.readNumber(3)?.plus(59)?.toInt() ?: return korneaNotEnoughData()
                            277 -> flow.readNumber(4)?.plus(67)?.toInt() ?: return korneaNotEnoughData()
                            278 -> flow.readNumber(4)?.plus(83)?.toInt() ?: return korneaNotEnoughData()
                            279 -> flow.readNumber(4)?.plus(99)?.toInt() ?: return korneaNotEnoughData()
                            280 -> flow.readNumber(4)?.plus(115)?.toInt() ?: return korneaNotEnoughData()
                            281 -> flow.readNumber(5)?.plus(131)?.toInt() ?: return korneaNotEnoughData()
                            282 -> flow.readNumber(5)?.plus(163)?.toInt() ?: return korneaNotEnoughData()
                            283 -> flow.readNumber(5)?.plus(195)?.toInt() ?: return korneaNotEnoughData()
                            284 -> flow.readNumber(5)?.plus(227)?.toInt() ?: return korneaNotEnoughData()
                            285 -> 258

                            else -> return KorneaResult.errorAsIllegalState(
                                -1,
                                "Invalid symbol distance $symbol"
                            )
                        }

                        var distanceCode: Int = 0
                        var distanceSymbol: Int? = null

                        for (j in 1..distanceAlphabet.maxLength) {
                            distanceCode = flow.readBit()?.or(distanceCode shl 1)
                                ?: return notEnoughData("distance / huffmanCode[$j]")

                            distanceSymbol = distanceAlphabet.getSymbol(distanceCode, j)
                            if (distanceSymbol != null) break
                        }

                        dist = when (distanceSymbol) {
                            0 -> 1
                            1 -> 2
                            2 -> 3
                            3 -> 4
                            4 -> flow.readBit()?.plus(5) ?: return notEnoughData("Dist / 4")
                            5 -> flow.readBit()?.plus(7) ?: return notEnoughData("Dist / 5")
                            6 -> flow.readNumber(2)?.plus(9)?.toInt() ?: return notEnoughData("Dist / 6")
                            7 -> flow.readNumber(2)?.plus(13)?.toInt() ?: return notEnoughData("Dist / 7")
                            8 -> flow.readNumber(3)?.plus(17)?.toInt() ?: return notEnoughData("Dist / 8")
                            9 -> flow.readNumber(3)?.plus(25)?.toInt() ?: return notEnoughData("Dist / 9")
                            10 -> flow.readNumber(4)?.plus(33)?.toInt() ?: return notEnoughData("Dist / 10")
                            11 -> flow.readNumber(4)?.plus(49)?.toInt() ?: return notEnoughData("Dist / 11")
                            12 -> flow.readNumber(5)?.plus(65)?.toInt() ?: return notEnoughData("Dist / 12")
                            13 -> flow.readNumber(5)?.plus(97)?.toInt() ?: return notEnoughData("Dist / 13")
                            14 -> flow.readNumber(6)?.plus(129)?.toInt() ?: return notEnoughData("Dist / 14")
                            15 -> flow.readNumber(6)?.plus(193)?.toInt() ?: return notEnoughData("Dist / 15")
                            16 -> flow.readNumber(7)?.plus(257)?.toInt() ?: return notEnoughData("Dist / 16")
                            17 -> flow.readNumber(7)?.plus(385)?.toInt() ?: return notEnoughData("Dist / 17")
                            18 -> flow.readNumber(8)?.plus(513)?.toInt() ?: return notEnoughData("Dist / 18")
                            19 -> flow.readNumber(8)?.plus(769)?.toInt() ?: return notEnoughData("Dist / 19")
                            20 -> flow.readNumber(9)?.plus(1025)?.toInt() ?: return notEnoughData("Dist / 20")
                            21 -> flow.readNumber(9)?.plus(1537)?.toInt() ?: return notEnoughData("Dist / 21")
                            22 -> flow.readNumber(10)?.plus(2049)?.toInt() ?: return notEnoughData("Dist / 22")
                            23 -> flow.readNumber(10)?.plus(3073)?.toInt() ?: return notEnoughData("Dist / 23")
                            24 -> flow.readNumber(11)?.plus(4097)?.toInt() ?: return notEnoughData("Dist / 24")
                            25 -> flow.readNumber(11)?.plus(6145)?.toInt() ?: return notEnoughData("Dist / 25")
                            26 -> flow.readNumber(12)?.plus(8193)?.toInt() ?: return notEnoughData("Dist / 26")
                            27 -> flow.readNumber(12)?.plus(12289)?.toInt() ?: return notEnoughData("Dist / 27")
                            28 -> flow.readNumber(13)?.plus(16385)?.toInt() ?: return notEnoughData("Dist / 28")
                            29 -> flow.readNumber(13)?.plus(24577)?.toInt() ?: return notEnoughData("Dist / 29")
                            else -> return KorneaResult.errorAsIllegalState(
                                -1,
                                "Invalid distance symbol $distanceCode"
                            )
                        }

//                        println("Encoding $len,$dist")

                        for (j in 0 until len) {
                            data.add(data[data.size - dist])
                        }
                    }
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