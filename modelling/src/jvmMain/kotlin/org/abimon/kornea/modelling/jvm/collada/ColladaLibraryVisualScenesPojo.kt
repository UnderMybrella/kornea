package org.abimon.kornea.modelling.jvm.collada

import com.fasterxml.jackson.annotation.JsonRootName
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

@JsonRootName("library_visual_scenes")
data class ColladaLibraryVisualScenesPojo(
        @JacksonXmlProperty(isAttribute = true)
        val id: String? = null,
        @JacksonXmlProperty(isAttribute = true)
        val name: String? = null,

        val visual_scene: List<ColladaVisualScenePojo>
)