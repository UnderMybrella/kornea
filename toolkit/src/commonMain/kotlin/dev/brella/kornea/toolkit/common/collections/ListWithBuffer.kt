package dev.brella.kornea.toolkit.common.collections

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.toolkit.common.KorneaToolkit

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public interface ListWithBuffer<T>: List<T> {
    override var size: Int
}