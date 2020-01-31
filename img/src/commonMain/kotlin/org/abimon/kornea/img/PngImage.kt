package org.abimon.kornea.img

import org.abimon.kornea.io.common.flow.OutputFlow

expect suspend fun OutputFlow.writePngImage(img: RgbMatrix)