package dev.brella.kornea.config.common

import dev.brella.kornea.annotations.AvailableSince

@AvailableSince(KorneaConfig.VERSION_1_0_0_INDEV)
public object KorneaConfig {
    /**
     * kornea-config 1.0.1-indev
     * - Allow you to retrieve a config with a provided coroutineContext
     */
    @AvailableSince(VERSION_1_0_1_INDEV)
    public const val VERSION_1_0_1_INDEV: String = "1.0.1-indev"

    /**
     * kornea-config 1.0.0-indev
     * - Begin working on a way to store configuration in a coroutine context
     */
    @AvailableSince(VERSION_1_0_0_INDEV)
    public const val VERSION_1_0_0_INDEV: String = "1.0.0-indev"
}