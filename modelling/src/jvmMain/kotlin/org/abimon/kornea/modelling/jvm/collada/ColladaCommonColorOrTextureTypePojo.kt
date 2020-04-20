package org.abimon.kornea.modelling.jvm.collada

import java.awt.Color

data class ColladaCommonColorOrTextureTypePojo(
        val color: ColladaColorPojo
) {
    constructor(color: Color) : this(
        ColladaColorPojo()
            .apply { this.profileColour = color })
}