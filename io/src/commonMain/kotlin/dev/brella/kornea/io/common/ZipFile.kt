package dev.brella.kornea.io.common

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.errors.common.asType
import dev.brella.kornea.errors.common.getOrBreak
import dev.brella.kornea.errors.common.korneaNotEnoughData
import dev.brella.kornea.io.common.flow.InputFlow
import dev.brella.kornea.io.common.flow.extensions.readInt16LE
import dev.brella.kornea.io.common.flow.extensions.readInt32LE
import dev.brella.kornea.io.common.flow.extensions.readNumBytes
import dev.brella.kornea.io.common.flow.readExact
import dev.brella.kornea.toolkit.common.appendLineAwait
import dev.brella.kornea.toolkit.common.logger
import dev.brella.kornea.toolkit.common.toHexString

@AvailableSince(KorneaIO.VERSION_5_1_0_ALPHA)
public enum class EnumZipCompression(public val flag: Int) {
    /** The file is stored (no compression) */
    STORE(0),

    /** The file is Shrunk */
    @Deprecated("Methods 1-6 are legacy algorithms and are no longer recommended for use when compressing files.")
    SHRUNK(1),

    /** The file is Reduced with compression factor 1 */
    @Deprecated("Methods 1-6 are legacy algorithms and are no longer recommended for use when compressing files.")
    REDUCED_1(2),

    /** The file is Reduced with compression factor 2 */
    @Deprecated("Methods 1-6 are legacy algorithms and are no longer recommended for use when compressing files.")
    REDUCED_2(3),

    /** The file is Reduced with compression factor 3 */
    @Deprecated("Methods 1-6 are legacy algorithms and are no longer recommended for use when compressing files.")
    REDUCED_3(4),

    /** The file is Reduced with compression factor 4 */
    @Deprecated("Methods 1-6 are legacy algorithms and are no longer recommended for use when compressing files.")
    REDUCED_4(5),

    /** The file is Imploded */
    @Deprecated("Methods 1-6 are legacy algorithms and are no longer recommended for use when compressing files.")
    IMPLODED(6),

    /** Reserved for Tokenizing compression algorithm */
    RESERVED_TOKENIZING(7),

    /** The file is Deflated */
    DEFLATED(8),

    /** Enhanced Deflating using Deflate64(tm) */
    DEFLATE64(9),

    /** PKWARE Data Compression Library Imploding (old IBM TERSE) */
    PKWARE_IMPLODING(10),

    /** Reserved by PKWARE */
    RESERVED(11),

    /** File is compressed using BZIP2 algorithm */
    BZIP2(12),

    /** Reserved by PKWARE */
    RESERVED_13(13),

    /** LZMA */
    LZMA(14),

    /** Reserved by PKWARE */
    RESERVED_15(15),

    /** IBM z/OS CMPSC Compression */
    CMPSC(16),

    /** Reserved by PKWARE */
    RESERVED_17(17),

    /** File is compressed using IBM TERSE (new) */
    TERSE(18),

    /** IBM LZ77 z Architecture */
    LZ77(19),

    /** deprecated (use method 93 for zstd) */
    DEPRECATED_ZSTD(20),

    /** Zstandard (zstd) Compression */
    ZSTD(93),

    /** MP3 Compression */
    MP3(94),

    /** XZ Compression */
    XZ(95),

    /** JPEG variant */
    JPEG(96),

    /** WavPack compressed data */
    WAVPACK(97),

    /** PPMd version I, Rev 1 */
    PPMD(98),

    /** AE-x encryption marker (see APPENDIX E) */
    AEX(99)
}

@AvailableSince(KorneaIO.VERSION_5_1_0_ALPHA)
public data class ZipLocalFileHeader(
    val versionNeeded: Int,
    val bitFlag: Int,
    val compressionMethod: EnumZipCompression,
    val fileLastModificationTime: Int,
    val fileLastModificationDate: Int,
    val crc32: Int,
    val compressedSize: Int,
    val uncompressedSize: Int,
    val fileName: String
)

@AvailableSince(KorneaIO.VERSION_5_1_0_ALPHA)
public data class ZipCentralDirectoryFileHeader(
    val versionMadeBy: Int,
    val versionNeeded: Int,
    val bitFlag: Int,
    val compressionMethod: EnumZipCompression,
    val fileLastModificationTime: Int,
    val fileLastModificationDate: Int,
    val crc32: Int,
    val compressedSize: Int,
    val uncompressedSize: Int,
    val diskNumberWhereFileStarts: Int,
    val internalFileAttributes: Int,
    val externalFileAttributes: Int,
    val relativeOffsetOfLocalHeader: Int,
    val fileName: String,
    val extraField: ByteArray,
    val fileComment: String
)

@AvailableSince(KorneaIO.VERSION_5_1_0_ALPHA)
public data class ZipEndOfCentralDirectoryRecord(
    val numberOfThisDisk: Int,
    val diskWhereCentralDirectoryStarts: Int,
    val numberOfCentralDirectoryRecordsOnThisDisk: Int,
    val totalNumberOfCentralDirectoryRecords: Int,
    val sizeOfCentralDirectory: Int,
    val offsetOfCentralDirectory: Int,
    val comment: String?
)

@AvailableSince(KorneaIO.VERSION_5_1_0_ALPHA)
public data class ZipStructure(
    val localHeaders: List<ZipLocalFileHeader>,
    val centralDirectoryHeaders: List<ZipCentralDirectoryFileHeader>,
    val endOfCentralDirectoryRecord: ZipEndOfCentralDirectoryRecord
)

@AvailableSince(KorneaIO.VERSION_5_1_0_ALPHA)
public suspend fun InputFlow.readZipLocalFileHeader(shouldReadMagic: Boolean = true): KorneaResult<ZipLocalFileHeader> {
    if (shouldReadMagic) {
        val magicNumber = readInt32LE() ?: return korneaNotEnoughData()
        if (magicNumber != 0x04034b50) return KorneaResult.errorAsIllegalArgument(
            -1,
            "Magic number is not valid (Expected 0x04034b50, Received 0x${magicNumber.toHexString(8)}"
        )
    }

    val versionNeeded = readInt16LE() ?: return korneaNotEnoughData()
    val generalPurposeBitFlag = readInt16LE() ?: return korneaNotEnoughData()
    val compressionMethod = readInt16LE() ?: return korneaNotEnoughData()

    val fileLastModificationTime = readInt16LE() ?: return korneaNotEnoughData()
    val fileLastModificationDate = readInt16LE() ?: return korneaNotEnoughData()

    val crc32OfUncompressedData = readInt32LE() ?: return korneaNotEnoughData()
    val compressedSize = readInt32LE() ?: return korneaNotEnoughData()
    val uncompressedSize = readInt32LE() ?: return korneaNotEnoughData()
    val fileNameLength = readInt16LE() ?: return korneaNotEnoughData()
    val extraFieldLength = readInt16LE() ?: return korneaNotEnoughData()

    val fileName = readExact(fileNameLength)?.decodeToString() ?: return korneaNotEnoughData()
    val extraField = readExact(extraFieldLength)?.decodeToString() ?: return korneaNotEnoughData()

    return KorneaResult.success(
        ZipLocalFileHeader(
            versionNeeded,
            generalPurposeBitFlag,
            EnumZipCompression.values().firstOrNull { it.flag == compressionMethod } ?: return KorneaResult.errorAsIllegalArgument(-1, "$compressionMethod is an invalid compression method"),
            fileLastModificationTime,
            fileLastModificationDate,
            crc32OfUncompressedData,
            compressedSize,
            uncompressedSize,
            fileName
        )
    )
}

@AvailableSince(KorneaIO.VERSION_5_1_0_ALPHA)
public suspend fun InputFlow.readZipFile(): KorneaResult<ZipStructure> {
    val localHeaders: MutableList<ZipLocalFileHeader> = ArrayList()
    val centralHeaders: MutableList<ZipCentralDirectoryFileHeader> = ArrayList()
    var eocd: ZipEndOfCentralDirectoryRecord? = null
    val logger = logger()

    while (true) {
        val magic = readInt32LE() ?: break

        when (magic) {
            0x02014b50 -> {
                val versionMadeBy = readInt16LE() ?: return korneaNotEnoughData()
                val versionNeededToExtract = readInt16LE() ?: return korneaNotEnoughData()
                val generalPurposeBitFlag = readInt16LE() ?: return korneaNotEnoughData()
                val compressionMethod = readInt16LE() ?: return korneaNotEnoughData()
                val fileLastModificationTime = readInt16LE() ?: return korneaNotEnoughData()
                val fileLastModificationDate = readInt16LE() ?: return korneaNotEnoughData()
                val crc32 = readInt32LE() ?: return korneaNotEnoughData()
                val compressedSize = readInt32LE() ?: return korneaNotEnoughData()
                val uncompressedSize = readInt32LE() ?: return korneaNotEnoughData()

                val fileNameLength = readInt16LE() ?: return korneaNotEnoughData()
                val extraFieldLength = readInt16LE() ?: return korneaNotEnoughData()
                val fileCommentLength = readInt16LE() ?: return korneaNotEnoughData()

                val diskNumberWhereFileStarts = readInt16LE() ?: return korneaNotEnoughData()
                val internalFileAttributes = readInt16LE() ?: return korneaNotEnoughData()
                val externalFileAttributes = readInt32LE() ?: return korneaNotEnoughData()
                val relativeOffsetOfLocalHeader = readInt32LE() ?: return korneaNotEnoughData()

                val fileName = readExact(fileNameLength)?.decodeToString() ?: return korneaNotEnoughData()
                val extraField = readExact(extraFieldLength) ?: return korneaNotEnoughData()
                val fileComment = readExact(fileCommentLength)?.decodeToString() ?: return korneaNotEnoughData()

                centralHeaders.add(
                    ZipCentralDirectoryFileHeader(
                        versionMadeBy,
                        versionNeededToExtract,
                        generalPurposeBitFlag,
                        EnumZipCompression.values().firstOrNull { it.flag == compressionMethod } ?: return KorneaResult.errorAsIllegalArgument(-1, "$compressionMethod is an invalid compression method"),
                        fileLastModificationTime,
                        fileLastModificationDate,
                        crc32,
                        compressedSize,
                        uncompressedSize,
                        diskNumberWhereFileStarts,
                        internalFileAttributes,
                        externalFileAttributes,
                        relativeOffsetOfLocalHeader,
                        fileName,
                        extraField,
                        fileComment
                    )
                )
            }
            0x04034b50 -> {
                val localFile = readZipLocalFileHeader(shouldReadMagic = false).getOrBreak { return it.asType() }
                localHeaders.add(localFile)
                skip(localFile.compressedSize.toULong())
            }
            0x06054b50 -> {
                val numberOfThisDisk = readInt16LE() ?: return korneaNotEnoughData()
                val diskWhereCentralDirectoryStarts = readInt16LE() ?: return korneaNotEnoughData()
                val numberOfCentralDirectoryRecordsOnThisDisk = readInt16LE() ?: return korneaNotEnoughData()
                val totalNumberOfCentralDirectoryRecords = readInt16LE() ?: return korneaNotEnoughData()
                val sizeOfCentralDirectory = readInt32LE() ?: return korneaNotEnoughData()
                val offsetOfCentralDirectory = readInt32LE() ?: return korneaNotEnoughData()

                eocd = ZipEndOfCentralDirectoryRecord(
                    numberOfThisDisk,
                    diskWhereCentralDirectoryStarts,
                    numberOfCentralDirectoryRecordsOnThisDisk,
                    totalNumberOfCentralDirectoryRecords,
                    sizeOfCentralDirectory,
                    offsetOfCentralDirectory,
                    null
                )

                val commentLength = readInt16LE() ?: break
                val comment = readNumBytes(commentLength).decodeToString()

                eocd = eocd.copy(comment = comment)
                break
            }
            else -> {
                if (localHeaders.isEmpty()) break

                val hasSignature = magic == 0x08074b50
                val crc32 = if (hasSignature) readInt32LE() ?: break else magic
                val compressedSize = readInt32LE() ?: break
                val uncompressedSize = readInt32LE() ?: break

                if (!hasSignature && logger != null) {
                    logger.appendLineAwait("CRC32: $crc32")
                    logger.appendLineAwait("Compressed Size: $compressedSize")
                    logger.appendLineAwait("Uncompressed Size: $uncompressedSize")
                }

                localHeaders[localHeaders.lastIndex] = localHeaders.last().copy(
                    crc32 = crc32,
                    compressedSize = compressedSize,
                    uncompressedSize = uncompressedSize
                )
            }
        }
    }

    return KorneaResult.success(ZipStructure(localHeaders, centralHeaders, eocd ?: return korneaNotEnoughData()))
}