package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.io.common.KorneaIO

@AvailableSince(KorneaIO.VERSION_3_2_0_ALPHA)
public interface PipeFlow<I : InputFlow, O : OutputFlow> : InputFlow, OutputFlowByDelegate<O> {
    public val input: I
    public override val output: O
}