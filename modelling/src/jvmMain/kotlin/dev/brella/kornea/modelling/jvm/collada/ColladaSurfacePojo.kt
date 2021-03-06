package dev.brella.kornea.modelling.jvm.collada

import com.fasterxml.jackson.annotation.JsonRootName
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

@JsonRootName("surface")
data class ColladaSurfacePojo(
    @JacksonXmlProperty(isAttribute = true)
        val type: ColladaFxSurfaceType,

    val init_from: ColladaInitFromPojo?
)