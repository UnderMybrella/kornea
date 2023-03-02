package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.io.common.KorneaIO

@AvailableSince(KorneaIO.VERSION_3_2_0_ALPHA)
@Deprecated("Deprecating PipeFlow until further notice", level = DeprecationLevel.ERROR)
public interface SeekablePipeFlow<I, O> : InputFlow, OutputFlowByDelegate<O>,
    SeekableFlow where I : InputFlow, I : SeekableFlow, O : OutputFlow, O : SeekableFlow {
    public val input: I
    public override val output: O
}