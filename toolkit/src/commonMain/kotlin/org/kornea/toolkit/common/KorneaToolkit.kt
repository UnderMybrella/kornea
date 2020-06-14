package org.kornea.toolkit.common

import org.abimon.kornea.annotations.AvailableSince

@AvailableSince(KorneaToolkit.VERSION_1_2_0)
public object KorneaToolkit {
    /**
     * kornea-toolkit
     *
     * - Add kornea-annotations as dependency
     * - Add [KorneaTypeChecker] and [KorneaInlineClassBasedTypeChecker]
     * - Add [KorneaToolkit]
     * - Add [ImmutableListView]
     * - Add [SharedState] and [ReadWriteSemaphore]
     * - Add [CoroutineScope][exchange]
     */
    @AvailableSince(VERSION_1_2_0)
    public const val VERSION_1_2_0: String = "1.2.0"
}