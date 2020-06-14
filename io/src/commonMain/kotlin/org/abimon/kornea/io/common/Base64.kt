package org.abimon.kornea.io.common


private val BASE64_ALPHABET: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
private val BASE64_MASK: Byte = 0x3f
private val BASE64_PAD: Char = '='
private val BASE64_INVERSE_ALPHABET = IntArray(256) {
    BASE64_ALPHABET.indexOf(it.toChar())
}

private fun Int.toBase64(): Char = BASE64_ALPHABET[this]

public object CommonBase64Encoder {
    private val base64 = IntArray(256)

    public fun encode(src: ByteArray): ByteArray {
        fun ByteArray.getOrZero(index: Int): Int = if (index >= size) 0 else get(index).toInt()
        // 4n / 3 is expected Base64 payload
        val result = ArrayList<Byte>(4 * src.size / 3)
        var index = 0
        while (index < src.size) {
            val symbolsLeft = src.size - index
            val padSize = if (symbolsLeft >= 3) 0 else (3 - symbolsLeft) * 8 / 6
            val chunk = (src.getOrZero(index) shl 16) or (src.getOrZero(index + 1) shl 8) or src.getOrZero(index + 2)
            index += 3

            for (i in 3 downTo padSize) {
                val char = (chunk shr (6 * i)) and BASE64_MASK.toInt()
                result.add(char.toBase64().toByte())
            }
            // Fill the pad with '='
            repeat(padSize) { result.add(BASE64_PAD.toByte()) }
        }

        return result.toByteArray()
    }

    public fun decode(src: ByteArray): ByteArray {
        val dst = ByteArray(src.size * 3 / 4)
        val ret = decode0(src, 0, src.size, dst)
        return dst.sliceArray(0 until ret)
    }
    private fun decode0(src: ByteArray, sp: Int, sl: Int, dst: ByteArray): Int {
        @Suppress("NAME_SHADOWING")
        var sp = sp
        var dp = 0
        var bits = 0
        var shiftTo = 18
        label85@ while (sp < sl) {
            var b: Int
            if (shiftTo == 18 && sp + 4 < sl) {
                b = sp + (sl - sp and -4)
                while (true) {
                    if (sp < b) {
                        val b1 = base64[src[sp++].toInt().and(0xFF)]
                        val b2 = base64[src[sp++].toInt().and(0xFF)]
                        val b3 = base64[src[sp++].toInt().and(0xFF)]
                        val b4 = base64[src[sp++].toInt().and(0xFF)]
                        if (b1 or b2 or b3 or b4 >= 0) {
                            val bits0 = b1 shl 18 or (b2 shl 12) or (b3 shl 6) or b4
                            dst[dp++] = (bits0 shr 16).toByte()
                            dst[dp++] = (bits0 shr 8).toByte()
                            dst[dp++] = bits0.toByte()
                            continue
                        }
                        sp -= 4
                    }
                    if (sp >= sl) {
                        break@label85
                    }
                    break
                }
            }
            b = src[sp++].toInt() and 0xFF
            if (base64[b].also { b = it } >= 0) {
                bits = bits or b shl shiftTo
                shiftTo -= 6
                if (shiftTo < 0) {
                    dst[dp++] = (bits shr 16).toByte()
                    dst[dp++] = (bits shr 8).toByte()
                    dst[dp++] = bits.toByte()
                    shiftTo = 18
                    bits = 0
                }
            } else {
                if (b == -2) {
                    require(!(shiftTo == 6 && (sp == sl || src[sp++].toInt().and(0xFF) != 61) || shiftTo == 18)) { "Input byte array has wrong 4-byte ending unit" }
                    break
                }

                throw IllegalArgumentException("Illegal base64 character " + src[sp - 1].toString(16))
            }
        }
        if (shiftTo == 6) {
            dst[dp++] = (bits shr 16).toByte()
        } else if (shiftTo == 0) {
            dst[dp++] = (bits shr 16).toByte()
            dst[dp++] = (bits shr 8).toByte()
        } else require(shiftTo != 12) { "Last unit does not have enough valid bits" }
        do {
            if (sp >= sl) {
                return dp
            }
        } while (base64[src[sp++].toInt() and 0xFF] < 0)
        throw IllegalArgumentException("Input byte array has incorrect ending byte at $sp")
    }

    init {
        base64.fill(-1)

        for (i in BASE64_ALPHABET.indices) {
            base64[BASE64_ALPHABET[i].toInt()] = i
        }
    }
}