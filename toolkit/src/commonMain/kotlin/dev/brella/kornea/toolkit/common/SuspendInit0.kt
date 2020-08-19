package dev.brella.kornea.toolkit.common

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.annotations.ChangedSince

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
@ChangedSince(KorneaToolkit.VERSION_3_0_0_ALPHA)
public interface SuspendInit0 {
    public suspend fun init()
}

@AvailableSince(KorneaToolkit.VERSION_3_0_0_ALPHA)
public interface SuspendInit1<in P1> {
    public suspend fun init(p1: P1)
}

@AvailableSince(KorneaToolkit.VERSION_3_0_0_ALPHA)
public interface SuspendInit2<in P1, in P2> {
    public suspend fun init(p1: P1, p2: P2)
}

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public suspend inline fun <T: SuspendInit0> init(obj: T): T {
    obj.init()
    return obj
}

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public suspend inline fun <P1, T: SuspendInit1<P1>> init(obj: T, p1: P1): T {
    obj.init(p1)
    return obj
}

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public suspend inline fun <P1, T: SuspendInit1<P1>> P1.init(obj: T): T {
    obj.init(this)
    return obj
}

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public suspend inline fun <P1, P2, T: SuspendInit2<P1, P2>> init(obj: T, p1: P1, p2: P2): T {
    obj.init(p1, p2)
    return obj
}

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public suspend inline fun <P1, P2, T: SuspendInit2<P1, P2>> P1.init(obj: T, p2: P2): T {
    obj.init(this, p2)
    return obj
}