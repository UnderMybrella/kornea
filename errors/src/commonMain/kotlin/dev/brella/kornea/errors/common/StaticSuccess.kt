package dev.brella.kornea.errors.common

import dev.brella.kornea.annotations.AvailableSince

@AvailableSince(KorneaErrors.VERSION_3_2_0_INDEV)
public fun KorneaResult.Companion.success(): KorneaResult<StaticSuccess> =
    StaticSuccess

@AvailableSince(KorneaErrors.VERSION_3_2_0_INDEV)
public object StaticSuccess :
    KorneaResult.Success<StaticSuccess> {
    public operator fun invoke(): KorneaResult<StaticSuccess> = this

    override fun get(): StaticSuccess = this

    override fun toString(): String = "[UnitSuccess]"

    override fun <R> mapValue(newValue: R): KorneaResult.Success<R> = KorneaResult.success(newValue, null) as KorneaResult.Success<R>
}