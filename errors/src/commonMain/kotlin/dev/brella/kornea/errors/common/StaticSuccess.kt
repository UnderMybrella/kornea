package dev.brella.kornea.errors.common

import dev.brella.kornea.annotations.AvailableSince

@AvailableSince(KorneaErrors.VERSION_3_2_0_INDEV)
public inline fun KorneaResult.Companion.success(): KorneaResult<Unit> =
    success(Unit)