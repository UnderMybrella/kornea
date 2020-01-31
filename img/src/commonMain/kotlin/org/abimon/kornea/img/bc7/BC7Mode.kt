package org.abimon.kornea.img.bc7

data class BC7Mode(
    val mode: Int,
    val partitions: Int,
    val red: List<Int>,
    val green: List<Int>,
    val blue: List<Int>,
    val alpha: List<Int>?,
    val pBits: List<Int>?,
    val indices: List<Int>,
    val alphaIndices: List<Int>?,
    val rotation: Int?,
    val selectionBit: Int?
)