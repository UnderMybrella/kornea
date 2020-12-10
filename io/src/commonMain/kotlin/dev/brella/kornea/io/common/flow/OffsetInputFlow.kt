package dev.brella.kornea.io.common.flow

@ExperimentalUnsignedTypes
public interface OffsetInputFlow : InputFlow, InputFlowWithBacking {
    public val baseOffset: ULong
}