package org.abimon.kornea.modelling.jvm.collada

import com.fasterxml.jackson.annotation.JsonRootName
import java.time.Instant

@JsonRootName("asset")
data class ColladaAssetPojo(
        val contributor: ColladaContributorPojo?,
        val created: Instant = Instant.now(),
        val modified: Instant = Instant.now(),
        val up_axis: ColladaUpAxis?
)