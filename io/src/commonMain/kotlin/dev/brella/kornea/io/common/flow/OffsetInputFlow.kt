package dev.brella.kornea.io.common.flow

public interface OffsetInputFlow : InputFlow, InputFlowWithBacking {
    public val baseOffset: ULong
}