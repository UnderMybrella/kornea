package dev.brella.kornea.img.bc7

data class BC7Mode(
    val mode: Int,
    val partitions: Int,
    val red: IntArray,
    val green: IntArray,
    val blue: IntArray,
    val alpha: IntArray?,
    val pBits: IntArray?,
    val indices: IntArray,
    val alphaIndices: IntArray?,
    val rotation: Int?,
    val selectionBit: Int?
)