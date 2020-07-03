package dev.brella.kornea.toolkit.common

import dev.brella.kornea.annotations.AvailableSince

@AvailableSince(KorneaToolkit.VERSION_1_2_0_INDEV)
public object KorneaToolkit {
    /**
     * kornea-toolkit 1.2.0-alpha
     * - Change [SharedState] operations to be inline, and refactor the class structure around that
     */
    public const val VERSION_1_2_0_ALPHA: String = "1.2.0-alpha"
    /**
     * kornea-toolkit 1.1.0-alpha
     * - Begin basic work on [transaction] operations
     */
    @AvailableSince(VERSION_1_1_0_ALPHA)
    public const val VERSION_1_1_0_ALPHA: String = "1.1.0-alpha"

    /**
     * kornea-toolkit 1.0.0-alpha
     * - Move to dev.brella as the package + group
     * - [SemanticVersion] is now an inline class
     */
    @AvailableSince(VERSION_1_0_0_ALPHA)
    public const val VERSION_1_0_0_ALPHA: String = "1.0.0-alpha"

    /**
     * kornea-toolkit
     * - Add kornea-errors as dependency
     * - Add [filterToInstance] overloads
     * - Add [SharedState], [SharedStateRWMutability], [SharedStateRWInt], [SharedStateRWLong], [SharedStateRWBoolean], [SharedStateRWString]
     * - Add [KorneaMutability]
     */
    public const val VERSION_1_3_0_INDEV: String = "1.3.0-indev"

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
    @AvailableSince(VERSION_1_2_0_INDEV)
    public const val VERSION_1_2_0_INDEV: String = "1.2.0-indev"
}