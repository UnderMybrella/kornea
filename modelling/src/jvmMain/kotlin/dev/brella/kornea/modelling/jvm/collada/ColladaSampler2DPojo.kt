package dev.brella.kornea.modelling.jvm.collada

import com.fasterxml.jackson.annotation.JsonRootName

@JsonRootName("sampler2D")
data class ColladaSampler2DPojo(
        val source: String
)