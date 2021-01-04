package dev.brella.kornea.io.common

public object CyclicRedundancyChecks {
    public data class CRCAlgorithm(
        val width: Int,
        val polynomial: Long,
        val init: Long,
        val lsb: Boolean,
        val reflectOutput: Boolean,
        val xorOut: Long,
        val check: Long,
        val residue: Long,
        val name: String
    )

    public val CRC_3_GSM: CRCAlgorithm by lazy { CRCAlgorithm(3, 0x3, 0x0, false, false, 0x7, 0x4, 0x2, "CRC-3/GSM") }
    public val CRC_3_ROHC: CRCAlgorithm by lazy { CRCAlgorithm(3, 0x3, 0x7, true, true, 0x0, 0x6, 0x0, "CRC-3/ROHC") }
    public val CRC_4_G_704: CRCAlgorithm by lazy { CRCAlgorithm(4, 0x3, 0x0, true, true, 0x0, 0x7, 0x0, "CRC-4/G-704") }
    public val CRC_4_INTERLAKEN: CRCAlgorithm by lazy {
        CRCAlgorithm(
            4,
            0x3,
            0xF,
            false,
            false,
            0xF,
            0xB,
            0x2,
            "CRC-4/INTERLAKEN"
        )
    }
    public val CRC_5_EPC_C1G2: CRCAlgorithm by lazy {
        CRCAlgorithm(
            5,
            0x09,
            0x09,
            false,
            false,
            0x00,
            0x00,
            0x00,
            "CRC-5/EPC-C1G2"
        )
    }
    public val CRC_5_G_704: CRCAlgorithm by lazy {
        CRCAlgorithm(
            5,
            0x15,
            0x00,
            true,
            true,
            0x00,
            0x07,
            0x00,
            "CRC-5/G-704"
        )
    }
    public val CRC_5_USB: CRCAlgorithm by lazy {
        CRCAlgorithm(
            5,
            0x05,
            0x1F,
            true,
            true,
            0x1F,
            0x19,
            0x06,
            "CRC-5/USB"
        )
    }

    public val CRC_32: CRCAlgorithm by lazy {
        CRCAlgorithm(
            32,
            0x04C11DB7,
            0xFFFFFFFF,
            true,
            true,
            0xFFFFFFFF,
            0xCBF43926,
            0x00,
            "CRC-32"
        )
    }

    public const val CRC_3_GSM_POLYNOMIAL: Long = 0x3
    public const val CRC_4_ITU_POLYNOMIAL: Long = 0x3
    public const val CRC_5_EPC_POLYNOMIAL: Long = 0x09
    public const val CRC_5_ITU_POLYNOMIAL: Long = 0x15
    public const val CRC_5_USB_POLYNOMIAL: Long = 0x05
    public const val CRC_6_CDMA2000_A_POLYNOMIAL: Long = 0x27
    public const val CRC_6_CDMA2000_B_POLYNOMIAL: Long = 0x07
    public const val CRC_6_DARC_POLYNOMIAL: Long = 0x19
    public const val CRC_6_GSM_POLYNOMIAL: Long = 0x2F
    public const val CRC_6_ITU_POLYNOMIAL: Long = 0x03
    public const val CRC_7_POLYNOMIAL: Long = 0x09
    public const val CRC_7_MVB_POLYNOMIAL: Long = 0x65
    public const val CRC_8_POLYNOMIAL: Long = 0xD5
    public const val CRC_8_AUTOSAR_POLYNOMIAL: Long = 0x2F
    public const val CRC_8_BLUETOOTH_POLYNOMIAL: Long = 0xA7
    public const val CRC_8_CCITT_POLYNOMIAL: Long = 0x07
    public const val CRC_8_DALLAS_MAXIM_POLYNOMIAL: Long = 0x31
    public const val CRC_8_DARC_POLYNOMIAL: Long = 0x39
    public const val CRC_8_GSM_B_POLYNOMIAL: Long = 0x49
    public const val CRC_8_SAE_J1850_POLYNOMIAL: Long = 0x1D
    public const val CRC_8_WCDMA_POLYNOMIAL: Long = 0x9B
    public const val CRC_10_POLYNOMIAL: Long = 0x233
    public const val CRC_10_CDMA2000_POLYNOMIAL: Long = 0x3D9
    public const val CRC_10_GSM_POLYNOMIAL: Long = 0x175
    public const val CRC_11_POLYNOMIAL: Long = 0x385
    public const val CRC_12_POLYNOMIAL: Long = 0x80F
    public const val CRC_12_CDMA2000_POLYNOMIAL: Long = 0xF13
    public const val CRC_12_GSM_POLYNOMIAL: Long = 0xD31
    public const val CRC_13_BBC_POLYNOMIAL: Long = 0x1CF5
    public const val CRC_14_DARC_POLYNOMIAL: Long = 0x0805
    public const val CRC_14_GSM_POLYNOMIAL: Long = 0x202D
    public const val CRC_15_CAN_POLYNOMIAL: Long = 0xC599
    public const val CRC_15_MPT1327_POLYNOMIAL: Long = 0x6815
    public const val CRC_16_CHAKRAVARTY_POLYNOMIAL: Long = 0x2F15
    public const val CRC_16_ARINC_POLYNOMIAL: Long = 0xA02B
    public const val CRC_16_CCITT_POLYNOMIAL: Long = 0x1021
    public const val CRC_16_CDMA2000_POLYNOMIAL: Long = 0xC867
    public const val CRC_16_DECT_POLYNOMIAL: Long = 0x0589
    public const val CRC_16_T100_DIF_POLYNOMIAL: Long = 0x8BB7
    public const val CRC_16_DNP_POLYNOMIAL: Long = 0x3D65
    public const val CRC_16_IBM_POLYNOMIAL: Long = 0x8005
    public const val CRC_16_OPENSAFETY_A_POLYNOMIAL: Long = 0x5935
    public const val CRC_16_OPENSAFETY_B_POLYNOMIAL: Long = 0x755B
    public const val CRC_16_PROFIBUS_POLYNOMIAL: Long = 0x1DCF

    /** FLETCHER_16 */
    public const val CRC_17_CAN_POLYNOMIAL: Long = 0x1685B
    public const val CRC_21_CAN_POLYNOMIAL: Long = 0x102899
    public const val CRC_24_POLYNOMIAL: Long = 0x5D6DCB
    public const val CRC_24_RADIX_64_POLYNOMIAL: Long = 0x864CFB
    public const val CRC_24_WCDMA_POLYNOMIAL: Long = 0x800063
    public const val CRC_30_POLYNOMIAL: Long = 0x2030B9C7
    public const val CRC_32_POLYNOMIAL: Long = 0x04C11DB7
    public const val CRC_32C_POLYNOMIAL: Long = 0x1EDC6F41
    public const val CRC_32K_POLYNOMIAL: Long = 0x741B8CD7
    public const val CRC_32K2_POLYNOMIAL: Long = 0x32583499
    public const val CRC_32Q_POLYNOMIAL: Long = 0x814141AB

    /** ADLER 32 */
    public const val CRC_40_GSM_POLYNOMIAL: Long = 0x0004820009
    public const val CRC_64_ECMA_POLYNOMIAL: Long = 0x42F0E1EBA9EA3693
    public const val CRC_64_ISO_POLYNOMIAL: Long = 0x000000000000001B

    private inline fun reflect(num: ULong, width: Int): ULong {
        var i = 1uL shl (width - 1)
        var j = 1uL
        var crcout = 0uL

        while (i > 0uL) {
            if (num and i > 0u) crcout = crcout or j

            j = j shl 1
            i = i shr 1
        }

        return crcout
    }

    public fun calculateCRC(
        packet: ByteArray,
        algorithm: CRCAlgorithm
    ): ULong {
        /**
         * unsigned long crcbitbybitfast(unsigned char* p, unsigned long len) {

        // fast bit by bit algorithm without augmented zero bytes.
        // does not use lookup table, suited for polynom orders between 1...32.

        unsigned long i, j, c, bit;
        unsigned long crc = crcinit_direct;

        for (i=0; i<len; i++) {

        c = (unsigned long)*p++;
        if (refin) c = reflect(c, 8);

        for (j=0x80; j; j>>=1) {

        bit = crc & crchighbit;
        crc<<= 1;
        if (c & j) bit^= crchighbit;
        if (bit) crc^= polynom;
        }
        }

        if (refout) crc=reflect(crc, order);
        crc^= crcxor;
        crc&= crcmask;

        return(crc);
        }
         */

        var i = 0uL
        var j = 0uL
        var c = 0uL
        var bit = 0uL

        var crc = algorithm.init.toULong()

        val crcMask = (((1uL shl (algorithm.width - 1)) - 1u) shl 1) or 1u
        val crcHighBit = 1uL shl (algorithm.width - 1)

        packet.forEach { byte ->
            if (algorithm.lsb) c = reflect(byte.toULong(), 8)
            else c = byte.toULong()

            j = 0x80uL
            while (j > 0u) {
                bit = crc and crcHighBit
                crc = crc shl 1
                if (c and j > 0u) bit = bit xor crcHighBit
                if (bit > 0u) crc = crc xor algorithm.polynomial.toULong()

                j = j shr 1
            }
        }

        if (algorithm.reflectOutput) crc = reflect(crc, algorithm.width)
        crc = crc xor algorithm.xorOut.toULong()
        crc = crc and crcMask

        return crc
    }
}

public inline fun ByteArray.cyclicRedundancyCheck(algorithm: CyclicRedundancyChecks.CRCAlgorithm): ULong =
    CyclicRedundancyChecks.calculateCRC(this, algorithm)

public fun ByteArray.crc32(): ULong =
    CyclicRedundancyChecks.calculateCRC(this, CyclicRedundancyChecks.CRC_32)