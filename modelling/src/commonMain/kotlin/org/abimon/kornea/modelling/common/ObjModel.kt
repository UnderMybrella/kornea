package org.abimon.kornea.modelling.common

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.abimon.kornea.erorrs.common.KorneaResult
import org.abimon.kornea.io.common.flow.FlowReader
import org.abimon.kornea.io.common.flow.InputFlow
import org.abimon.kornea.io.common.flow.useEachLine

inline class ObjModel(val meshes: Array<Mesh>) {
    companion object {
        const val MISSING_VERTICES = 0
        const val MISSING_UVS = 1
        const val MISSING_NORMALS = 2
        const val EMPTY_MODEL = 3

        @ExperimentalCoroutinesApi
        @ExperimentalUnsignedTypes
        suspend operator fun invoke(flow: InputFlow, trimVerticesForGroups: Boolean = false): KorneaResult<ObjModel> {
            val reader = FlowReader(flow)
            val meshes: MutableList<Mesh> = ArrayList()
            val vertices: MutableList<Vertex> = ArrayList()
            val uvs: MutableList<UV> = ArrayList()
            val normals: MutableList<Vertex> = ArrayList()
            val parameterSpaceVertices: MutableList<UV> = ArrayList()

            val faces: MutableList<TriFace> = ArrayList()
            var groupName: String = "model"

            fun trimAndAddToMesh() {
                var minVertexIndex: Int? = null
                var minUVIndex: Int? = null
                var minNormalIndex: Int? = null

                var maxVertexIndex: Int? = null
                var maxUVIndex: Int? = null
                var maxNormalIndex: Int? = null

                faces.forEach { face ->
                    face.forEachIndexed { _, index ->
                        if (minVertexIndex == null || index.vertex < minVertexIndex!!)
                            minVertexIndex = index.vertex
                        if (maxVertexIndex == null || index.vertex > maxVertexIndex!!)
                            maxVertexIndex = index.vertex

                        if (index.textureCoordinate != null) {
                            if (minUVIndex == null || index.textureCoordinate < minUVIndex!!)
                                minUVIndex = index.textureCoordinate
                            if (maxUVIndex == null || index.textureCoordinate > maxUVIndex!!)
                                maxUVIndex = index.textureCoordinate
                        }

                        if (index.normal != null) {
                            if (minNormalIndex == null || index.normal < minNormalIndex!!)
                                minNormalIndex = index.normal
                            if (maxNormalIndex == null || index.normal > maxNormalIndex!!)
                                maxNormalIndex = index.normal
                        }
                    }
                }

                meshes.add(
                    Mesh(
                        vertices = vertices.slice(minVertexIndex!!..maxVertexIndex!!).toTypedArray(),
                        uvs = uvs.takeIf { it.isNotEmpty() && minUVIndex != null && maxUVIndex != null }
                            ?.slice(minUVIndex!!..maxUVIndex!!)
                            ?.toTypedArray(),
                        normals = normals.takeIf { it.isNotEmpty() && minNormalIndex != null && maxNormalIndex != null }
                            ?.slice(minNormalIndex!!..maxNormalIndex!!)
                            ?.toTypedArray(),
                        name = groupName,

                        faces = Array(faces.size) { i ->
                            val (a, b, c) = faces[i]

                            TriFace(
                                FaceIndex(
                                    a.vertex - minVertexIndex!!,
                                    a.textureCoordinate?.minus(minUVIndex!!),
                                    a.normal?.minus(minNormalIndex!!)
                                ),
                                FaceIndex(
                                    b.vertex - minVertexIndex!!,
                                    b.textureCoordinate?.minus(minUVIndex!!),
                                    b.normal?.minus(minNormalIndex!!)
                                ),
                                FaceIndex(
                                    c.vertex - minVertexIndex!!,
                                    c.textureCoordinate?.minus(minUVIndex!!),
                                    c.normal?.minus(minNormalIndex!!)
                                )
                            )
                        }
                    )
                )
            }

            reader.useEachLine { line ->
                when {
                    line.startsWith("#") -> {
                    } //Ignore comments
                    line.startsWith("v ") -> {
                        //Geometric vertices, with (x, y, z [,w]) coordinates, w is optional and defaults to 1.0.
                        val components = line.split(" ")
                            .drop(1)
                            .mapNotNull(String::toFloatOrNull)

                        vertices.add(
                            Vertex(
                                components.getOrNull(0) ?: return@useEachLine,
                                components.getOrNull(1) ?: return@useEachLine,
                                components.getOrNull(2) ?: return@useEachLine,
                                components.getOrNull(3) ?: 1f
                            )
                        )
                    }
                    line.startsWith("vt") -> {
                        //Texture coordinates, in (u, [,v ,w]) coordinates, these will vary between 0 and 1. v, w are optional and default to 0.
                        val components = line.split(" ")
                            .drop(1)
                            .mapNotNull(String::toFloatOrNull)

                        uvs.add(
                            UV(
                                components.getOrNull(0) ?: return@useEachLine,
                                components.getOrNull(1) ?: 0f,
                                components.getOrNull(2) ?: 0f
                            )
                        )
                    }
                    line.startsWith("vn") -> {
                        //vertex normals in (x,y,z) form; normals might not be unit vectors.
                        val components = line.split(" ")
                            .drop(1)
                            .mapNotNull(String::toFloatOrNull)

                        normals.add(
                            Vertex(
                                components.getOrNull(0) ?: return@useEachLine,
                                components.getOrNull(1) ?: return@useEachLine,
                                components.getOrNull(2) ?: return@useEachLine
                            )
                        )
                    } //Normal
                    line.startsWith("vp") -> {
                    } //Parameter Space Vertex
                    line.startsWith("f") -> {
                        val components = line.split(" ")
                            .drop(1)
                            .mapNotNull { str ->
                                val components = str.split("/")
                                    .map { c ->
                                        val i = c.toIntOrNull()
                                        if (i != null && i > 0) i - 1
                                        else i
                                    }

                                FaceIndex(
                                    components[0] ?: return@mapNotNull null,
                                    components.getOrNull(1),
                                    components.getOrNull(2)
                                )
                            }

                        when {
                            components.size < 3 ->
                                return KorneaResult.Error(
                                    MISSING_NORMALS,
                                    "Invalid face (Only has ${components.size} elements)"
                                )

                            components.size == 3 ->
                                faces.add(TriFace(components[0], components[1], components[2]))

                            components.size == 4 -> {
                                faces.add(TriFace(components[0], components[1], components[2]))
                                faces.add(TriFace(components[0], components[2], components[3]))
                            }

                            else -> {
                                for (i in 0 until components.size - 2) {
                                    faces.add(TriFace(components[0], components[i + 1], components[i + 2]))
                                }
                            }
                        }
                    } //Polygonal Face Element
                    line.startsWith("l") -> {
                    } //Line Element
                    line.startsWith("g") -> {
                        if (faces.isNotEmpty()) {
                            if (trimVerticesForGroups && faces.none { face -> face.a.vertex < 0 || face.b.vertex < 0 || face.c.vertex < 0 }) trimAndAddToMesh()
                            else {
                                meshes.add(
                                    Mesh(
                                        vertices = vertices.toTypedArray(),
                                        uvs = uvs.takeIf(List<*>::isNotEmpty)?.toTypedArray(),
                                        normals = normals.takeIf(List<*>::isNotEmpty)?.toTypedArray(),
                                        name = groupName,

                                        faces = faces.toTypedArray()
                                    )
                                )
                            }
                            faces.clear()
                            groupName = line.substringAfter("g ")
                        }
                    }
                    else -> {
                    }
                }
            }

            if (faces.isNotEmpty()) {
                if (trimVerticesForGroups && faces.none { face -> face.a.vertex < 0 || face.b.vertex < 0 || face.c.vertex < 0 }) trimAndAddToMesh()
                else {
                    meshes.add(
                        Mesh(
                            vertices = vertices.toTypedArray(),
                            uvs = uvs.takeIf(List<*>::isNotEmpty)?.toTypedArray(),
                            normals = normals.takeIf(List<*>::isNotEmpty)?.toTypedArray(),
                            name = groupName,

                            faces = faces.toTypedArray()
                        )
                    )
                }
            }

            if (vertices.isEmpty() && uvs.isEmpty() && normals.isEmpty() && meshes.isEmpty())
                return KorneaResult.Error(EMPTY_MODEL, "Model was empty")

            meshes.forEach { mesh ->
                mesh.faces.forEachIndexed { i, face ->
                    val indices = arrayOf(face.a, face.b, face.c)
                    indices.forEachIndexed { index, faceIndex ->
                        var remappedIndex = faceIndex

                        if (faceIndex.vertex < 0)
                            remappedIndex = FaceIndex(
                                vertices.size + remappedIndex.vertex,
                                remappedIndex.textureCoordinate,
                                remappedIndex.normal
                            )
                        if (faceIndex.textureCoordinate != null && faceIndex.textureCoordinate < 0)
                            remappedIndex = FaceIndex(
                                remappedIndex.vertex,
                                uvs.size + faceIndex.textureCoordinate,
                                remappedIndex.normal
                            )
                        if (faceIndex.normal != null && faceIndex.normal < 0)
                            remappedIndex = FaceIndex(
                                remappedIndex.vertex,
                                remappedIndex.textureCoordinate,
                                normals.size + faceIndex.normal
                            )

                        if (remappedIndex !== faceIndex) indices[index] = remappedIndex

                        if (remappedIndex.vertex !in vertices.indices)
                            return KorneaResult.Error(
                                MISSING_VERTICES,
                                "Missing Vertex: ${remappedIndex.vertex}"
                            )
                        if (remappedIndex.textureCoordinate != null && remappedIndex.textureCoordinate!! !in uvs.indices)
                            return KorneaResult.Error(
                                MISSING_UVS,
                                "Missing UV: ${remappedIndex.textureCoordinate}"
                            )
                        if (remappedIndex.normal != null && remappedIndex.normal!! !in normals.indices)
                            return KorneaResult.Error(
                                MISSING_NORMALS,
                                "Missing Normal: ${remappedIndex.normal}"
                            )
                    }

                    if (indices[0] !== face.a || indices[1] !== face.b || indices[2] !== face.c) {
                        mesh.faces[i] = TriFace(indices[0], indices[1], indices[2])
                    }
                }
            }

            return KorneaResult.Success(
                ObjModel(
                    meshes.toTypedArray()
                )
            )
        }
    }
}