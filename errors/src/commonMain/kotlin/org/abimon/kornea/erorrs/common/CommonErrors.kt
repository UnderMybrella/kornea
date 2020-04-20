package org.abimon.kornea.erorrs.common

const val KORNEA_ERROR_NOT_ENOUGH_DATA = 0xF000
const val KORNEA_ERROR_NOT_FOUND = 0xF001

inline fun <reified T> korneaNotEnoughData(message: String = "Not enough data"): KorneaResult.Failure<T, Unit> =
    KorneaResult.Failure(KORNEA_ERROR_NOT_ENOUGH_DATA, message)

inline fun <reified T> korneaNotFound(message: String = "Not found"): KorneaResult.Failure<T, Unit> =
    KorneaResult.Failure(KORNEA_ERROR_NOT_FOUND, message)