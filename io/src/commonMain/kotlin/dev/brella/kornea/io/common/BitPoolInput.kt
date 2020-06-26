package dev.brella.kornea.io.common

public class BitPoolInput(public val bytes: ByteArray, size: Int = bytes.size) {
    public val maxIndex: Int = size
    private var _index: Int = 0
    public val index: Int
        get() = _index
    private var bitpool: Int
    private var bitsLeft: Int

    public val isEmpty: Boolean
        get() = _index == maxIndex && bitsLeft == 0

    public fun read(numBits: Int): Int {
        var outBits = 0
        var bitsProduced = 0

        while (bitsProduced < numBits) {
            if (bitsLeft == 0) {
                bitpool = if (_index >= maxIndex) 0 else bytes[_index++].toInt().and(0xFF)
                bitsLeft = 8
            }

            val bitsThisRound = minOf(bitsLeft, numBits - bitsProduced)

            outBits = outBits shl bitsThisRound
            outBits = outBits or ((bitpool shr (bitsLeft - bitsThisRound)) and ((1 shl bitsThisRound) - 1))

            bitsLeft -= bitsThisRound
            bitsProduced += bitsThisRound
        }

        return outBits
    }

    public fun peek(numBits: Int): Int {
        var outBits = 0
        var bitsProduced = 0

        var shadowedBitpool = bitpool
        var shadowedIndex = _index
        var shadowedBitsLeft = bitsLeft

        while (bitsProduced < numBits) {
            if (shadowedBitsLeft == 0) {
                shadowedBitpool = if (shadowedIndex >= maxIndex) 0 else bytes[shadowedIndex++].toInt().and(0xFF)
                shadowedBitsLeft = 8
            }

            val bitsThisRound = minOf(shadowedBitsLeft, numBits - bitsProduced)

            outBits = outBits shl bitsThisRound
            outBits = outBits or ((shadowedBitpool shr (shadowedBitsLeft - bitsThisRound)) and ((1 shl bitsThisRound) - 1))

            shadowedBitsLeft -= bitsThisRound
            bitsProduced += bitsThisRound
        }

        return outBits
    }

    init {
        if (_index < maxIndex) {
            bitpool = bytes[_index++].toInt().and(0xFF)
            bitsLeft = 8
        } else {
            bitpool = 0
            bitsLeft = 0
        }
    }
}