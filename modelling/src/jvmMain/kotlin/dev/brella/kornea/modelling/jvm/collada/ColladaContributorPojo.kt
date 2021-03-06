package dev.brella.kornea.modelling.jvm.collada

import com.fasterxml.jackson.annotation.JsonRootName

@JsonRootName("contributor")
data class ColladaContributorPojo(
        val author: String? = null,
        val authoring_tool: String? = null,
        val comments: String? = null,
        val copyright: String? = null
)