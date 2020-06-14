package org.abimon.kornea.annotations

@Suppress("unused")
@AvailableSince(KorneaAnnotations.VERSION_1_2_0)
public object KorneaAnnotations {

    /**
     * kornea-annotations 1.3.0
     * - Add message parameter to [ExperimentalKorneaErrors], [ExperimentalKorneaImg], [ExperimentalKorneaIO], [ExperimentalKorneaModelling], and [ExperimentalKorneaToolkit]
     */
    @AvailableSince(VERSION_1_3_0)
    public const val VERSION_1_3_0: String = "1.3.0"
    /**
     * kornea-annotations 1.2.0
     *
     * - Created [ChangedSince]
     * - Created [ExperimentalKorneaToolkit]
     * - Created [KorneaAnnotations]
     */
    @AvailableSince(VERSION_1_2_0)
    public const val VERSION_1_2_0: String = "1.2.0"

    /**
     * kornea-annotations 1.1.0
     *
     * - Created [WrongBytecodeGenerated]
     */
    @AvailableSince(VERSION_1_2_0)
    public const val VERSION_1_1_0: String = "1.1.0"

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
    @AvailableSince(VERSION_1_2_0)
    public const val VERSION_1_0_0: String = "1.0.0"
}