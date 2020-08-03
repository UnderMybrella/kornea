package dev.brella.kornea.toolkit.common

import dev.brella.kornea.annotations.AvailableSince

@AvailableSince(KorneaToolkit.VERSION_1_2_0_INDEV)
public object KorneaToolkit {
    /**
     * kornea-toolkit 3.0.0-alpha
     * - Change [DataCloseable] to have an isClosed property
     * - Change [ProgressBar] to extend [DataCloseable] and use suspending methods
     * - Add [ChannelBasedProgressBar] and have AsciiProgressBar extend that
     * - Change [SuspendInit0] (was SuspendInit), and add [SuspendInit1], [SuspendInit2]
     * - Allow progress bars to be styled more easily
     */
    public const val VERSION_3_0_0_ALPHA: String = "3.0.0-alpha"

    /**
     * kornea-toolkit 2.4.0-alpha
     * - Move [PrintFlow] from kornea-io to kornea-toolkit
     * - Add AsciiProgressBar and AsciiArbitraryProgressBar
     */
    @AvailableSince(VERSION_2_4_0_ALPHA)
    public const val VERSION_2_4_0_ALPHA: String = "2.4.0-alpha"

    /**
     * kornea-toolkit 2.3.0-alpha
     * - Add: ArrayBackedList (Just ByteArrayBackedList at this time)
     * - Change ChainNode to use covariance for node
     * - Add: KorneaPool / Poolable
     * - Add: loopAtMostOnce, loopAtMostTwice
     * - Add: KorneaWaiter
     * - Add: ListWithBuffer
     * - Add: Byte#asInt, and other Numerical Extensions
     * - Move ObservableDataCloseable to be in Toolkit
     * - Add: SuspendInit
     */
    @AvailableSince(VERSION_2_3_0_ALPHA)
    public const val VERSION_2_3_0_ALPHA: String = "2.3.0-alpha"
    /**
     * kornea-toolkit 2.2.0-alpha
     * - Change ChainLink's to take a generic parameter, and use properties
     */
    public const val VERSION_2_2_0_ALPHA: String = "2.2.0-alpha"

    public const val VERSION_2_1_0_ALPHA: String = "2.1.0-alpha"
    /**
     * kornea-toolkit 1.2.0-alpha
     * - Change [SharedState] operations to be inline, and refactor the class structure around that
     */
    public const val VERSION_2_0_0_ALPHA: String = "2.0.0-alpha"
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