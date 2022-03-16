package dev.brella.kornea.io.common.flow

public interface InputFlowWithBacking: InputFlow {
    public suspend fun globalOffset(): ULong
    public suspend fun absPosition(): ULong
}