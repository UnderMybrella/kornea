package dev.brella.kornea.io.common.flow

public interface OffsetInputFlow : InputFlow, KorneaFlowWithBacking {
    public val baseOffset: ULong
}