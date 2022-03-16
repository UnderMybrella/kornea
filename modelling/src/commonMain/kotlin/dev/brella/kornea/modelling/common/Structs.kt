package dev.brella.kornea.modelling.common

import kotlin.jvm.JvmInline

data class UV(val u: Float, val v: Float, val w: Float = 1.0f)
data class Vertex(val x: Float, val y: Float, val z: Float, val w: Float = 1.0f)

data class FaceIndex(val vertex: Int, val textureCoordinate: Int?, val normal: Int?)
open class Mesh(val vertices: Array<Vertex>, val faces: Array<TriFace>, val uvs: Array<UV>?, val normals: Array<Vertex>?, val name: String?)

//interface MeshFace
@JvmInline
value class PolyFace(val indices: Array<FaceIndex>)
data class TriFace(val a: FaceIndex, val b: FaceIndex, val c: FaceIndex)

public fun UV.toVertexList(): List<Float> = listOf(u, v)
public fun Vertex.toVertexList(): List<Float> = listOf(x, y, z)
public fun TriFace.toVertexList(): List<Int> = listOf(a.vertex, b.vertex, c.vertex)

public inline fun TriFace.forEachIndexed(action: (index: Int, FaceIndex) -> Unit) {
    action(0, a)
    action(1, b)
    action(2, c)
}