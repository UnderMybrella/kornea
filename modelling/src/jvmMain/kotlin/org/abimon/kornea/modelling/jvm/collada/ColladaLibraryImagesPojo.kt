package org.abimon.kornea.modelling.jvm.collada

import com.fasterxml.jackson.annotation.JsonRootName

@JsonRootName("library_images")
data class ColladaLibraryImagesPojo(
        val image: List<ColladaImagePojo>
)