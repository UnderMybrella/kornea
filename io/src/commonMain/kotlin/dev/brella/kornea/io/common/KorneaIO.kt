package dev.brella.kornea.io.common

import dev.brella.kornea.annotations.AvailableSince

public object KorneaIO {
    /**
     * kornea-io 2.1.0-alpha
     * - Move PrintFlow to kornea-toolkit
     */
    public const val VERSION_2_1_0_ALPHA: String = "2.1.0-alpha"

    /**
     * kornea-io 2.0.0-alpha
     * - Add PeekableInputFlow#peek(b, off, len)
     * - Implement all read operations for ByteArray, and add variants without an index
     * - Add FlowPacket and FlowState; a way to optimise reads for small packets of data
     */
    public const val VERSION_2_0_0_ALPHA: String = "2.0.0-alpha"

    /**
     * kornea-io 1.3.0-alpha
     * - Add [FannedPrintFlow][dev.brella.kornea.io.common.flow.FannedPrintFlow] and [SequentialPrintFlow][dev.brella.kornea.io.common.flow.SequentialPrintFlow]
     */
    public const val VERSION_1_3_0_ALPHA: String = "1.3.0-alpha"

    /**
     * kornea-io 1.2.0-alpha
     * - Add [StdoutPrintFlow][dev.brella.kornea.io.common.flow.StdoutPrintFlow]
     * - Change [StandardInputFlow][dev.brella.kornea.io.common.flow.StandardInputFlow] implementations, in particular for the JVM.
     * - Add [FannedInputFlow][dev.brella.kornea.io.common.flow.FannedInputFlow]
     * - Add [ConflatingBufferedInputFlow][dev.brella.kornea.io.common.flow.ConflatingBufferedInputFlow]
     */
    @AvailableSince(VERSION_1_2_0_ALPHA)
    public const val VERSION_1_2_0_ALPHA: String = "1.2.0-alpha"

    /**
     * kornea-io 1.1.0-alpha
     * - Add [FannedOutputFlow][dev.brella.kornea.io.common.flow.FannedOutputFlow] and [SequentialOutputFlow][dev.brella.kornea.io.common.flow.SequentialOutputFlow]
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
     * - Add [AppendableAwait], [PrintFlow][dev.brella.kornea.io.common.flow.PrintFlow], and [PrintOutputFlow][dev.brella.kornea.toolkit.common.PrintOutputFlow PrintOutputFlow]
     */
    @AvailableSince(VERSION_4_2_0_INDEV)
    public const val VERSION_4_2_0_INDEV: String = "4.2.0-indev"

    /**
     * kornea-io 4.1.2
     * - Fix [useAndMapInputFlow] not actually mapping the input flow
     */
    @AvailableSince(VERSION_4_1_2_INDEV)
    public const val VERSION_4_1_2_INDEV: String = "4.1.2-indev"


    /**
     * kornea-io 4.1.1
     * - Change [CountingInputStream._mark] and [CountingInputStream._count] to be protected, not private
     * - Add null to [JVMDataSource]'s maximumInstanceCount
     */
    @AvailableSince(VERSION_4_1_1_INDEV)
    public const val VERSION_4_1_1_INDEV: String = "4.1.1-indev"

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
    @AvailableSince(VERSION_4_1_0_INDEV)
    public const val VERSION_4_1_0_INDEV: String = "4.1.0-indev"
}