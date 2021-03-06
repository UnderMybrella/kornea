package dev.brella.kornea.modelling.jvm.collada

import com.fasterxml.jackson.annotation.JsonRootName
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

@JsonRootName("input")
data class ColladaInputUnsharedPojo(
        @JacksonXmlProperty(isAttribute = true)
        val semantic: String,
        @JacksonXmlProperty(isAttribute = true)
        val source: String
)