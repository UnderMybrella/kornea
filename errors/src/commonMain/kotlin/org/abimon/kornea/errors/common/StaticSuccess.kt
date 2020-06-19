package org.abimon.kornea.errors.common

import org.abimon.kornea.annotations.AvailableSince

@AvailableSince(KorneaErrors.VERSION_3_2_0)
public fun KorneaResult.Companion.success(): KorneaResult<StaticSuccess> = StaticSuccess

@AvailableSince(KorneaErrors.VERSION_3_2_0)
public object StaticSuccess : KorneaResult.Success<StaticSuccess> {
    public operator fun invoke(): KorneaResult<StaticSuccess> = this

    override fun get(): StaticSuccess = this

    override fun toString(): String = "[UnitSuccess]"

    override fun <R> mapValue(newValue: R): KorneaResult.Success<R> = KorneaResult.success(newValue) as KorneaResult.Success<R>
}