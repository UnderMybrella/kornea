package org.abimon.kornea.erorrs.common

const val KORNEA_NOT_ENOUGH_DATA = 0x00

inline fun <reified T> notEnoughData(message: String = "Not enough data"): KorneaResult.Failure<T, Unit> = KorneaResult.Failure(KORNEA_NOT_ENOUGH_DATA, message)