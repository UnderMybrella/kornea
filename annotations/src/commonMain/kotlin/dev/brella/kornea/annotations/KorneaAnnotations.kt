package dev.brella.kornea.annotations

@Suppress("unused")
@AvailableSince(KorneaAnnotations.VERSION_1_2_0_INDEV)
public object KorneaAnnotations {
    /**
     * kornea-annotations 1.3.0-alpha
     * - Add [DangerousApiUsage]
     */
    public const val VERSION_1_3_0_ALPHA: String = "1.3.0-alpha"

    /**
     * kornea-annotations 1.0.0-alpha
     * - Move to dev.brella
     */
    public const val VERSION_1_0_0_ALPHA: String = "1.0.0-alpha"

    /**
     * kornea-annotations 1.3.0
     * - Add message parameter to [ExperimentalKorneaErrors], [ExperimentalKorneaImg], [ExperimentalKorneaIO], [ExperimentalKorneaModelling], and [ExperimentalKorneaToolkit]
     */
    @AvailableSince(VERSION_1_3_0_INDEV)
    public const val VERSION_1_3_0_INDEV: String = "1.3.0"
    /**
     * kornea-annotations 1.2.0
     *
     * - Created [ChangedSince]
     * - Created [ExperimentalKorneaToolkit]
     * - Created [KorneaAnnotations]
     */
    @AvailableSince(VERSION_1_2_0_INDEV)
    public const val VERSION_1_2_0_INDEV: String = "1.2.0"

    /**
     * kornea-annotations 1.1.0
     *
     * - Created [WrongBytecodeGenerated]
     */
    @AvailableSince(VERSION_1_2_0_INDEV)
    public const val VERSION_1_1_0_INDEV: String = "1.1.0"

    /**
     * kornea-annotations 1.0.0
     *
     * Initial version:
     *  - Created [BlockingOperation]
     *  - Created [ExperimentalKorneaErrors]
     *  - Created [ExperimentalKorneaImg]
     *  - Created [ExperimentalKorneaIO]
     *  - Created [ExperimentalKorneaModelling]
     *  - Created [ReactiveContext]
     *  - Created [Suspendable]
     */
    @AvailableSince(VERSION_1_2_0_INDEV)
    public const val VERSION_1_0_0_INDEV: String = "1.0.0"
}