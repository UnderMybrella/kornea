package dev.brella.kornea.io.common.flow

import dev.brella.kornea.base.common.ObservableDataCloseable
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.Uri

public interface KorneaFlow : ObservableDataCloseable {
    public val location: String?

    public suspend fun position(): ULong
    public fun locationAsUri(): KorneaResult<Uri>
}

public interface KorneaFlowWithBacking: KorneaFlow {
    public suspend fun globalOffset(): ULong
    public suspend fun absPosition(): ULong
}

public suspend fun KorneaFlow.globalOffset(): ULong = if (this is KorneaFlowWithBacking) this.globalOffset() else 0u
public suspend fun KorneaFlow.offsetPosition(): ULong = globalOffset() + position()