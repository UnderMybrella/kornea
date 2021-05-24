package dev.brella.kornea.errors.common

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.base.common.Optional
import dev.brella.kornea.base.common.empty

@AvailableSince(KorneaErrors.VERSION_3_2_0_INDEV)
public fun KorneaResult.Companion.success(): KorneaResult<StaticSuccess> =
    StaticSuccess

@AvailableSince(KorneaErrors.VERSION_3_2_0_INDEV)
public object StaticSuccess :
    KorneaResult.Success<StaticSuccess> {
    public operator fun invoke(): KorneaResult<StaticSuccess> = this

    override fun get(): StaticSuccess = this

    override fun toString(): String = "[UnitSuccess]"

    override fun <R> mapValue(newValue: R): KorneaResult<R> = KorneaResult.success(newValue, null)

    override fun dataHashCode(): Optional<Int> = Optional.empty()
    override fun isAvailable(dataHashCode: Int?): Boolean? = null
    override fun consume(dataHashCode: Int?) {}
}