package org.abimon.kornea.errors.common

public const val KORNEA_ERROR_NOT_ENOUGH_DATA: Int = 0xF000
public const val KORNEA_ERROR_NOT_FOUND: Int = 0xF001

public inline fun <reified T> korneaNotEnoughData(message: String = "Not enough data"): KorneaResult<T> =
    KorneaResult.errorAsIllegalState(
        KORNEA_ERROR_NOT_ENOUGH_DATA,
        message
    )

public inline fun <reified T> korneaNotFound(message: String = "Not found"): KorneaResult<T> =
    KorneaResult.errorAsIllegalArgument(
        KORNEA_ERROR_NOT_FOUND,
        message
    )