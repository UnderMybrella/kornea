package org.abimon.kornea.io.common

import org.abimon.kornea.annotations.AvailableSince

public object KorneaIO {
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