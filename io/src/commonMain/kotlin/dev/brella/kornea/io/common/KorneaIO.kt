package dev.brella.kornea.io.common

import dev.brella.kornea.annotations.AvailableSince

public object KorneaIO {
    /**
     * kornea-io 1.1.0-alpha
     * - Add [dev.brella.kornea.io.common.flow.FannedOutputFlow] and [dev.brella.kornea.io.common.flow.SequentialOutputFlow]
     * - Change IO methods to use runInterruptible rather than withContext
     */
    @AvailableSince(VERSION_1_1_0_ALPHA)
    public const val VERSION_1_1_0_ALPHA: String = "1.1.0-alpha"

    /**
     * kornea-io 1.0.0-alpha
     * -
     */
    @AvailableSince(VERSION_1_0_0_ALPHA)
    public const val VERSION_1_0_0_ALPHA: String = "1.0.0-alpha"

    /**
     * kornea-io 4.2.0
     * - Add [AppendableAwait], [PrintFlow][dev.brella.kornea.io.common.flow.PrintFlow], and [PrintOutputFlow][dev.brella.kornea.io.common.flow.PrintOutputFlow PrintOutputFlow]
     */
    @AvailableSince(VERSION_4_2_0)
    public const val VERSION_4_2_0: String = "4.2.0"

    /**
     * kornea-io 4.1.2
     * - Fix [useAndMapInputFlow] not actually mapping the input flow
     */
    @AvailableSince(VERSION_4_1_2)
    public const val VERSION_4_1_2: String = "4.1.2"


    /**
     * kornea-io 4.1.1
     * - Change [CountingInputStream._mark] and [CountingInputStream._count] to be protected, not private
     * - Add null to [JVMDataSource]'s maximumInstanceCount
     */
    @AvailableSince(VERSION_4_1_1)
    public const val VERSION_4_1_1: String = "4.1.1"

    /**
     * kornea-io 4.1.0
     *  - Add atomicfu plugin
     *  - Add kornea-toolkit as a dependency
     *  - Add [KorneaResult][useAndMap], [KorneaResult][useAndFlatMap]
     *  - Add [KorneaResult][useInputFlow], [KorneaResult][useInputFlowForResult], [KorneaResult][useAndMapInputFlow], [KorneaResult][useAndFlatMapInputFlow]
     *  - Add [LimitedInstanceDataSource] and change data sources to use that as a parent
     *  - Add [MultiViewOutputFlow]
     *  - Add [FlowOutputStream] and [FlowInputStream] to jvmMain
     *  - Add [BaseDataCloseable], and change instances to use that as a parent
     */
    @AvailableSince(VERSION_4_1_0)
    public const val VERSION_4_1_0: String = "4.1.0"
}