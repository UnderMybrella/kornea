package dev.brella.kornea.toolkit.common

import dev.brella.kornea.annotations.AvailableSince

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public interface SuspendInit {
    public suspend fun init()
}

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public suspend inline fun <T: SuspendInit> init(obj: T): T {
    obj.init()
    return obj
}