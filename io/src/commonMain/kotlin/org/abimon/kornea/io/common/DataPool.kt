package org.abimon.kornea.io.common

import org.abimon.kornea.io.common.flow.InputFlow
import org.abimon.kornea.io.common.flow.OutputFlow

@ExperimentalUnsignedTypes
interface DataPool<I: InputFlow, O: OutputFlow>: DataSource<I>,
    DataSink<O>