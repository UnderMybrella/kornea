package dev.brella.kornea.img

import dev.brella.kornea.io.common.flow.OutputFlow

expect suspend fun OutputFlow.writePngImage(img: RgbMatrix)