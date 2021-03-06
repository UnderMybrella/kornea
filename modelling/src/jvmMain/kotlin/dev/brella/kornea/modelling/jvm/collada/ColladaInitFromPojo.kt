package dev.brella.kornea.modelling.jvm.collada

import com.fasterxml.jackson.annotation.JsonRootName
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText

@JsonRootName("init_from")
data class ColladaInitFromPojo(
        @JacksonXmlText
        val value: String
) : ColladaInitialisationPojo