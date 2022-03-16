package dev.brella.kornea.img

import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.errors.common.cast
import dev.brella.kornea.errors.common.getOrBreak
import dev.brella.kornea.errors.common.korneaNotEnoughData
import dev.brella.kornea.img.bc7.BC7PixelData
import dev.brella.kornea.io.common.flow.InputFlow
import dev.brella.kornea.io.common.isBitSet
import dev.brella.kornea.io.common.flow.extensions.readInt32LE

const val DDS1_MAGIC_NUMBER_LE = 0x31534444
const val DDS_MAGIC_NUMBER_LE = 0x20534444

data class DirectDrawSurfaceHeader(
    val flags: Int,
    val height: Int,
    val width: Int,
    val pitchOrLinearSize: Int,
    val depth: Int,
    val mipMapCount: Int,
    val reserved: IntArray,
    val pixelFormat: DirectDrawSurfacePixelFormat,
    val caps: Int,
    val caps2: Int,
    val caps3: Int,
    val caps4: Int,
    val reserved2: Int
) {
    @ExperimentalUnsignedTypes
    companion object {
        const val DDS_CAPS = 0x1
        const val DDSD_HEIGHT = 0x2
        const val DDSD_WIDTH = 0x4
        const val DDSD_PITCH = 0x8
        const val DDSD_PIXELFORMAT = 0x10_00
        const val DDSD_MIPMAPCOUNT = 0x20_000
        const val DDSD_LINEARSIZE = 0x80_000
        const val DDSD_DEPTH = 0x800_000

        const val DDSCAPS_COMPLEX = 0x8
        const val DDSCAPS_TEXTURE = 0x1000
        const val DDSCAPS_MIPMAP = 0x400_000

        const val DDSCAPS2_CUBEMAP = 0x200
        const val DDSCAPS2_CUBEMAP_POSITIVEX = 0x400
        const val DDSCAPS2_CUBEMAP_NEGATIVEX = 0x800
        const val DDSCAPS2_CUBEMAP_POSITIVEY = 0x1000
        const val DDSCAPS2_CUBEMAP_NEGATIVEY = 0x2000
        const val DDSCAPS2_CUBEMAP_POSITIVEZ = 0x4000
        const val DDSCAPS2_CUBEMAP_NEGATIVEZ = 0x8000
        const val DDSCAPS2_VOLUME = 0x200_000

        suspend operator fun invoke(flow: InputFlow): KorneaResult<DirectDrawSurfaceHeader> {
            val size = flow.readInt32LE() ?: return korneaNotEnoughData()
            val flags = flow.readInt32LE() ?: return korneaNotEnoughData()
            val height = flow.readInt32LE() ?: return korneaNotEnoughData()
            val width = flow.readInt32LE() ?: return korneaNotEnoughData()
            val pitchOrLinearSize = flow.readInt32LE() ?: return korneaNotEnoughData()
            val depth = flow.readInt32LE() ?: return korneaNotEnoughData()
            val mipMapCount = flow.readInt32LE() ?: return korneaNotEnoughData()
            val reserved = IntArray(11) { flow.readInt32LE() ?: return korneaNotEnoughData() }

            val pixelFormat = DirectDrawSurfacePixelFormat(flow)
                .getOrBreak { return it.cast() }

            val caps = flow.readInt32LE() ?: return korneaNotEnoughData()
            val caps2 = flow.readInt32LE() ?: return korneaNotEnoughData()
            val caps3 = flow.readInt32LE() ?: return korneaNotEnoughData()
            val caps4 = flow.readInt32LE() ?: return korneaNotEnoughData()
            val reserved2 = flow.readInt32LE() ?: return korneaNotEnoughData()

            return KorneaResult.success(
                DirectDrawSurfaceHeader(
                    flags,
                    height,
                    width,
                    pitchOrLinearSize,
                    depth,
                    mipMapCount,
                    reserved,
                    pixelFormat,
                    caps,
                    caps2,
                    caps3,
                    caps4,
                    reserved2
                )
            )
        }
    }
}

data class DirectDrawSurfacePixelFormat(
    val flags: Int,
    val fourCC: Int,
    val rgbBitCount: Int,
    val redBitMask: Int,
    val blueBitMask: Int,
    val greenBitMask: Int,
    val alphaBitMask: Int
) {
    @ExperimentalUnsignedTypes
    companion object {
        const val DDPF_ALPHAPIXELS = 0x1
        const val DDPF_ALPHA = 0x2
        const val DDPF_FOURCC = 0x4
        const val DDPF_RGB = 0x40
        const val DDPF_YUV = 0x200
        const val DDPF_LUMINANCE = 0x20_000

        const val DXT1 = 0x31545844
        const val DXT2 = 0x32545844
        const val DXT3 = 0x33545844
        const val DXT4 = 0x34545844
        const val DXT5 = 0x35545844
        const val DX10 = 0x30315844

        const val FORMAT_NOT_IMPLEMENTED = 0x00
        const val UNKNOWN_FORMAT = 0x01

        suspend operator fun invoke(flow: InputFlow): KorneaResult<DirectDrawSurfacePixelFormat> {
            val size = flow.readInt32LE() ?: return korneaNotEnoughData()
            val flags = flow.readInt32LE() ?: return korneaNotEnoughData()
            val fourCC = flow.readInt32LE() ?: return korneaNotEnoughData()
            val rgbBitCount = flow.readInt32LE() ?: return korneaNotEnoughData()
            val redBitMask = flow.readInt32LE() ?: return korneaNotEnoughData()
            val greenBitMask = flow.readInt32LE() ?: return korneaNotEnoughData()
            val blueBitMask = flow.readInt32LE() ?: return korneaNotEnoughData()
            val alphaBitMask = flow.readInt32LE() ?: return korneaNotEnoughData()

            return KorneaResult.success(
                DirectDrawSurfacePixelFormat(
                    flags,
                    fourCC,
                    rgbBitCount,
                    redBitMask,
                    blueBitMask,
                    greenBitMask,
                    alphaBitMask
                )
            )
        }
    }
}

data class DirectDrawSurfaceHeaderDX10(
    val dxgiFormat: Int,
    val resourceDimension: Int,
    val miscFlag: Int,
    val arraySize: Int,
    val miscFlags2: Int
) {
    @ExperimentalUnsignedTypes
    companion object {
        const val DDS_DIMENSION_TEXTURE1D = 2
        const val DDS_DIMENSION_TEXTURE2D = 3
        const val DDS_DIMENSION_TEXTURE3D = 4

        const val DDS_RESOURCE_MISC_TEXTURECUBE = 0x4

        const val DDS_ALPHA_MODE_UNKNOWN = 0x0
        const val DDS_ALPHA_MODE_STRAIGHT = 0x1
        const val DDS_ALPHA_MODE_PREMULTIPLIED = 0x2
        const val DDS_ALPHA_MODE_OPAQUE = 0x3
        const val DDS_ALPHA_MODE_CUSTOM = 0x4

        suspend operator fun invoke(flow: InputFlow): KorneaResult<DirectDrawSurfaceHeaderDX10> {
            val dxgiFormat = flow.readInt32LE() ?: return korneaNotEnoughData()
            val resourceDimension = flow.readInt32LE() ?: return korneaNotEnoughData()
            val miscFlag = flow.readInt32LE() ?: return korneaNotEnoughData()
            val arraySize = flow.readInt32LE() ?: return korneaNotEnoughData()
            val miscFlags2 = flow.readInt32LE() ?: return korneaNotEnoughData()

            return KorneaResult.success(
                DirectDrawSurfaceHeaderDX10(
                    dxgiFormat,
                    resourceDimension,
                    miscFlag,
                    arraySize,
                    miscFlags2
                )
            )
        }
    }
}

class DirectDrawSurfaceImage(
    val header: DirectDrawSurfaceHeader,
    val headerDX10: DirectDrawSurfaceHeaderDX10?,
    rgb: IntArray
) : RgbMatrix(header.width, header.height, rgb)

@Suppress("NAME_SHADOWING")
suspend fun InputFlow.readDDSImage(): KorneaResult<DirectDrawSurfaceImage> {
    var magic = readInt32LE() ?: return korneaNotEnoughData()
    if (magic == DDS1_MAGIC_NUMBER_LE)
        magic = readInt32LE() ?: return korneaNotEnoughData()

    require(magic == DDS_MAGIC_NUMBER_LE) { "Invalid magic number $magic" }

    val header = DirectDrawSurfaceHeader(this)
        .getOrBreak { return it.cast() }
    val header10: DirectDrawSurfaceHeaderDX10? =
        if (header.pixelFormat.flags isBitSet DirectDrawSurfacePixelFormat.DDPF_FOURCC && header.pixelFormat.fourCC == DirectDrawSurfacePixelFormat.DX10) {
            DirectDrawSurfaceHeaderDX10(this)
                .getOrBreak { return it.cast() }
        } else {
            null
        }

    val rgb: IntArray

    if (header.pixelFormat.flags isBitSet DirectDrawSurfacePixelFormat.DDPF_FOURCC) {
        when (header.pixelFormat.fourCC) {
            DirectDrawSurfacePixelFormat.DXT1 -> rgb = DXT1PixelData.read(header.width, header.height, this).rgb
            DirectDrawSurfacePixelFormat.DX10 -> rgb = BC7PixelData.read(header.width, header.height, this).rgb
            else -> return KorneaResult.errorAsIllegalState(
                DirectDrawSurfacePixelFormat.FORMAT_NOT_IMPLEMENTED,
                "Format 0x${header.pixelFormat.fourCC.toString(16).padStart(8, '0')} not implemented"
            )
        }
    } else {
        return KorneaResult.errorAsIllegalState(
            DirectDrawSurfacePixelFormat.UNKNOWN_FORMAT,
            "Unknown format flags ${header.pixelFormat.flags}"
        )
    }

    return KorneaResult.success(DirectDrawSurfaceImage(header, header10, rgb))
}