package org.abimon.kornea.img

import org.abimon.kornea.img.bc7.BC7Mode
import org.abimon.kornea.img.bc7.BC7PixelData
import org.abimon.kornea.io.common.flow.InputFlow
import org.abimon.kornea.io.common.isBitSet
import org.abimon.kornea.io.common.readInt32LE

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

        suspend fun invoke(flow: InputFlow): DirectDrawSurfaceHeader? {
            try {
                return unsafe(flow)
            } catch (iae: IllegalArgumentException) {
                return null
            }
        }

        suspend fun unsafe(flow: InputFlow): DirectDrawSurfaceHeader {
            val size = requireNotNull(flow.readInt32LE())
            val flags = requireNotNull(flow.readInt32LE())
            val height = requireNotNull(flow.readInt32LE())
            val width = requireNotNull(flow.readInt32LE())
            val pitchOrLinearSize = requireNotNull(flow.readInt32LE())
            val depth = requireNotNull(flow.readInt32LE())
            val mipMapCount = requireNotNull(flow.readInt32LE())
            val reserved = IntArray(11) { requireNotNull(flow.readInt32LE()) }
            val pixelFormat = DirectDrawSurfacePixelFormat.unsafe(flow)
            val caps = requireNotNull(flow.readInt32LE())
            val caps2 = requireNotNull(flow.readInt32LE())
            val caps3 = requireNotNull(flow.readInt32LE())
            val caps4 = requireNotNull(flow.readInt32LE())
            val reserved2 = requireNotNull(flow.readInt32LE())
            return DirectDrawSurfaceHeader(
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

        suspend fun invoke(flow: InputFlow): DirectDrawSurfacePixelFormat? {
            try {
                return unsafe(flow)
            } catch (iae: IllegalArgumentException) {
                return null
            }
        }

        suspend fun unsafe(flow: InputFlow): DirectDrawSurfacePixelFormat {
            val size = requireNotNull(flow.readInt32LE())
            val flags = requireNotNull(flow.readInt32LE())
            val fourCC = requireNotNull(flow.readInt32LE())
            val rgbBitCount = requireNotNull(flow.readInt32LE())
            val redBitMask = requireNotNull(flow.readInt32LE())
            val greenBitMask = requireNotNull(flow.readInt32LE())
            val blueBitMask = requireNotNull(flow.readInt32LE())
            val alphaBitMask = requireNotNull(flow.readInt32LE())

            return DirectDrawSurfacePixelFormat(
                flags,
                fourCC,
                rgbBitCount,
                redBitMask,
                blueBitMask,
                greenBitMask,
                alphaBitMask
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

        suspend fun invoke(flow: InputFlow): DirectDrawSurfaceHeaderDX10? {
            try {
                return unsafe(flow)
            } catch (iae: IllegalArgumentException) {
                return null
            }
        }

        suspend fun unsafe(flow: InputFlow): DirectDrawSurfaceHeaderDX10 {
            val dxgiFormat = requireNotNull(flow.readInt32LE())
            val resourceDimension = requireNotNull(flow.readInt32LE())
            val miscFlag = requireNotNull(flow.readInt32LE())
            val arraySize = requireNotNull(flow.readInt32LE())
            val miscFlags2 = requireNotNull(flow.readInt32LE())

            return DirectDrawSurfaceHeaderDX10(dxgiFormat, resourceDimension, miscFlag, arraySize, miscFlags2)
        }
    }
}

@ExperimentalUnsignedTypes
suspend fun InputFlow.readDDSImage(): RgbMatrix? {
    var magic = readInt32LE() ?: return null
    if (magic == DDS1_MAGIC_NUMBER_LE)
        magic = readInt32LE() ?: return null

    require(magic == DDS_MAGIC_NUMBER_LE)

    val header = DirectDrawSurfaceHeader.unsafe(this)
    val header10: DirectDrawSurfaceHeaderDX10? =
        if (header.pixelFormat.flags isBitSet DirectDrawSurfacePixelFormat.DDPF_FOURCC && header.pixelFormat.fourCC == DirectDrawSurfacePixelFormat.DX10) {
            DirectDrawSurfaceHeaderDX10.unsafe(this)
        } else {
            null
        }

    if (header.pixelFormat.flags isBitSet DirectDrawSurfacePixelFormat.DDPF_FOURCC) {
        when (header.pixelFormat.fourCC) {
            DirectDrawSurfacePixelFormat.DXT1 -> return DXT1PixelData.read(header.width, header.height, this)
            DirectDrawSurfacePixelFormat.DX10 -> return BC7PixelData.read(header.width, header.height, this)
        }
    }

    return null
}