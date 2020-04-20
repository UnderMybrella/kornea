package org.abimon.kornea.modelling.jvm.collada

import com.fasterxml.jackson.annotation.JsonRootName
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import org.abimon.kornea.modelling.common.*
import java.io.File

@JsonRootName("COLLADA")
data class ColladaPojo(
    @JacksonXmlProperty(isAttribute = true)
    val version: String = "1.4.1",
    @JacksonXmlProperty(isAttribute = true)
    val xmlns: String = "http://www.collada.org/2005/11/COLLADASchema",

    val asset: ColladaAssetPojo,
    val library_images: ColladaLibraryImagesPojo? = null,
    val library_effects: ColladaLibraryEffectsPojo? = null,
    val library_geometries: ColladaLibraryGeometriesPojo,
    val library_materials: ColladaLibraryMaterialsPojo? = null,
    val library_visual_scenes: ColladaLibraryVisualScenesPojo? = null,
    val scene: ColladaScenePojo? = null
) {
    companion object {
        @ExperimentalUnsignedTypes
        operator fun invoke(
            meshes: Array<Mesh>,
            flipUVs: Boolean = false,
            invertXAxis: Boolean = false,
            name: String? = null
        ): ColladaPojo {
            val colladaMeshes = meshes.mapIndexed { index, mesh ->
                val vertices: List<Vertex> = if (invertXAxis) mesh.vertices.map { (x, y, z) ->
                    Vertex(x * -1, y, z)
                } else mesh.vertices.toList()
                val uvs: List<UV> = (if (flipUVs) mesh.uvs?.map { (u, v) ->
                    UV(u, 1.0f - v)
                } else mesh.uvs?.toList()) ?: emptyList()
                val normals: List<Vertex> = (if (invertXAxis) mesh.normals?.map { (x, y, z) ->
                    Vertex(x * -1, y, z)
                } else mesh.normals?.toList()) ?: emptyList()

                val verticeSource = ColladaSourcePojo(
                    "vertices_source_mesh_${index}", "vertices_array_${mesh.name ?: "mesh_$index"}",
                    ColladaFloatArrayPojo(
                        "vertices_array_mesh_${index}",
                        vertices.flatMap(Vertex::toVertexList)
                            .toFloatArray()
                    ),
                    ColladaTechniqueCommonPojo.vertexAccessorFor(
                        vertices.size,
                        "#vertices_array_mesh_${index}"
                    )
                )

                val textureSource = ColladaSourcePojo(
                    "uv_source_mesh_${index}", "uv_array_${mesh.name ?: "mesh_$index"}",
                    ColladaFloatArrayPojo(
                        "uv_array_mesh_${index}",
                        uvs.flatMap(UV::toVertexList)
                            .toFloatArray()
                    ),
                    ColladaTechniqueCommonPojo.uvAccessorFor(
                        uvs.size,
                        "#uv_array_mesh_${index}"
                    )
                )

                val normalSource = ColladaSourcePojo(
                    "normals_source_mesh_${index}", "normals_array_${mesh.name ?: "mesh_$index"}",
                    ColladaFloatArrayPojo(
                        "normals_array_mesh_${index}",
                        normals.flatMap(Vertex::toVertexList)
                            .toFloatArray()
                    ),
                    ColladaTechniqueCommonPojo.vertexAccessorFor(
                        normals.size,
                        "#normals_array_mesh_${index}"
                    )
                )

                val verticesPojo = ColladaVerticesPojo(
                    "vertices_mesh_${index}",
                    "vertices_${mesh.name ?: "mesh_$index"}",
                    listOf(
                        ColladaInputUnsharedPojo(
                            "POSITION",
                            "#vertices_source_mesh_${index}"
                        )
                    )
                )

                val triangles: ColladaTrianglesPojo

                if (mesh.faces.all { (a, b, c) -> a.textureCoordinate in uvs.indices && b.textureCoordinate in uvs.indices && c.textureCoordinate in uvs.indices }) {
                    if (mesh.faces.all { (a, b, c) -> a.normal in normals.indices && b.normal in normals.indices && c.normal in normals.indices }) {
                        triangles = ColladaTrianglesPojo(
                            listOf(
                                ColladaInputSharedPojo(
                                    "VERTEX",
                                    "#vertices_mesh_${index}",
                                    0
                                ),
                                ColladaInputSharedPojo(
                                    "TEXCOORD",
                                    "#uv_source_mesh_${index}",
                                    1
                                ),
                                ColladaInputSharedPojo(
                                    "NORMAL",
                                    "#normals_source_mesh_${index}",
                                    2
                                )
                            ),
                            mesh.faces.flatMap { (a, b, c) -> listOf(a.vertex, a.textureCoordinate!!, a.normal!!, b.vertex, b.textureCoordinate!!, b.normal!!, c.vertex, c.textureCoordinate!!, c.normal!!) }.toIntArray()
                        )
                    } else {
                        triangles = ColladaTrianglesPojo(
                            listOf(
                                ColladaInputSharedPojo(
                                    "VERTEX",
                                    "#vertices_mesh_${index}",
                                    0
                                ),
                                ColladaInputSharedPojo(
                                    "TEXCOORD",
                                    "#uv_source_mesh_${index}",
                                    1
                                )
                            ),
                            mesh.faces.flatMap { (a, b, c) -> listOf(a.vertex, a.textureCoordinate!!, b.vertex, b.textureCoordinate!!, c.vertex, c.textureCoordinate!!) }.toIntArray()
                        )
                    }
                } else {
                    triangles = ColladaTrianglesPojo(
                        listOf(
                            ColladaInputSharedPojo(
                                "VERTEX",
                                "#vertices_mesh_${index}",
                                0
                            )
                        ),
                        mesh.faces.flatMap(TriFace::toVertexList)
                            .toIntArray()
                    )
                }

                return@mapIndexed ColladaGeometryPojo(
                    id = "mesh_$index",
                    name = mesh.name
                        ?: "mesh_$index",
                    mesh = ColladaMeshPojo(
                        listOf(
                            verticeSource,
                            textureSource,
                            normalSource
                        ), verticesPojo, listOf(triangles)
                    )
                )
            }

            val collada = ColladaPojo(
                asset = ColladaAssetPojo(
                    contributor = ColladaContributorPojo(
                        authoring_tool = "Kornea-Modelling",
                        comments = "Autogenerated from $name"
                    ), up_axis = ColladaUpAxis.Y_UP
                ),
                library_geometries = ColladaLibraryGeometriesPojo(
                    geometry = colladaMeshes
                ),
                library_visual_scenes = ColladaLibraryVisualScenesPojo(
                    visual_scene = listOf(
                        ColladaVisualScenePojo(
                            id = "Scene",
                            node = colladaMeshes.map { mesh ->
                                return@map ColladaNodePojo(
                                    type = "NODE",
                                    instance_geometry = listOf(
                                        ColladaInstanceGeometryPojo(
                                            url = "#${mesh.id}"
                                        )
                                    )
                                )
                            })
                    )
                ),

                scene = ColladaScenePojo(
                    instance_visual_scene = ColladaInstanceVisualScenePojo(
                        url = "#Scene"
                    )
                )
            )

            return collada
        }
    }
}