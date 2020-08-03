package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.annotations.WrongBytecodeGenerated
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.errors.common.asType
import dev.brella.kornea.errors.common.flatMap
import dev.brella.kornea.errors.common.map
import dev.brella.kornea.io.common.*
import dev.brella.kornea.toolkit.common.*
import kotlin.reflect.KClass

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public interface FlowState

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public interface InputFlowState : FlowState, InputFlow {
    public companion object {
        public val REGISTRAR: MutableMap<KClass<*>, (InputFlow) -> InputFlowState> = HashMap()
    }

    public val flow: InputFlow
}

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public interface OutputFlowState : FlowState, OutputFlow {
    public companion object {
        public val REGISTRAR: MutableMap<KClass<*>, (OutputFlow) -> OutputFlowState> = HashMap()
    }

    public val flow: OutputFlow
}

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public interface Int16FlowState : FlowState {
    public abstract class Base(override val int16Packet: Int16Packet = Int16Packet()) : Int16FlowState
    public open class BaseInput(override val flow: InputFlow) : Base(), InputFlowState, InputFlow by flow
    public open class BaseOutput(override val flow: OutputFlow) : Base(), OutputFlowState, OutputFlow by flow

    public companion object {
        public inline fun input(flow: InputFlow): BaseInput = BaseInput(flow)
        public inline fun output(flow: OutputFlow): BaseOutput = BaseOutput(flow)
    }

    public val int16Packet: Int16Packet
}

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public interface Int32FlowState : FlowState {
    public abstract class Base(override val int32Packet: Int32Packet = Int32Packet()) : Int32FlowState
    public open class BaseInput(override val flow: InputFlow) : Base(), InputFlowState, InputFlow by flow
    public open class BaseOutput(override val flow: OutputFlow) : Base(), OutputFlowState, OutputFlow by flow

    public companion object {
        public inline fun input(flow: InputFlow): BaseInput = BaseInput(flow)
        public inline fun output(flow: OutputFlow): BaseOutput = BaseOutput(flow)
    }

    public val int32Packet: Int32Packet
}

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public interface Int64FlowState : FlowState {
    public abstract class Base(override val int64Packet: Int64Packet = Int64Packet()) : Int64FlowState
    public open class BaseInput(override val flow: InputFlow) : Base(), InputFlowState, InputFlow by flow
    public open class BaseOutput(override val flow: OutputFlow) : Base(), OutputFlowState, OutputFlow by flow

    public companion object {
        public inline fun input(flow: InputFlow): BaseInput = BaseInput(flow)
        public inline fun output(flow: OutputFlow): BaseOutput = BaseOutput(flow)
    }

    public val int64Packet: Int64Packet
}

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public interface IntFlowState : Int16FlowState, Int32FlowState, Int64FlowState {
    public abstract class Base(
        override val int16Packet: Int16Packet = Int16Packet(),
        override val int32Packet: Int32Packet = Int32Packet(),
        override val int64Packet: Int64Packet = Int64Packet()
    ) : IntFlowState

    public open class BaseInput(override val flow: InputFlow) : Base(), InputFlowState, InputFlow by flow
    public open class BaseOutput(override val flow: OutputFlow) : Base(), OutputFlowState, OutputFlow by flow

    public companion object {
        public inline fun input(flow: InputFlow): BaseInput = BaseInput(flow)
        public inline fun output(flow: OutputFlow): BaseOutput = BaseOutput(flow)
    }
}

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public object FlowStateSelector {
    public inline fun int(flow: InputFlow): IntFlowState.BaseInput =
        IntFlowState.input(flow)

    public inline fun int(flow: OutputFlow): IntFlowState.BaseOutput =
        IntFlowState.output(flow)

    public inline fun int16(flow: InputFlow): Int16FlowState.BaseInput =
        Int16FlowState.input(flow)

    public inline fun int16(flow: OutputFlow): Int16FlowState.BaseOutput =
        Int16FlowState.output(flow)

    public inline fun int32(flow: InputFlow): Int32FlowState.BaseInput =
        Int32FlowState.input(flow)

    public inline fun int32(flow: OutputFlow): Int32FlowState.BaseOutput =
        Int32FlowState.output(flow)

    public inline fun int64(flow: InputFlow): Int64FlowState.BaseInput =
        Int64FlowState.input(flow)

    public inline fun int64(flow: OutputFlow): Int64FlowState.BaseOutput =
        Int64FlowState.output(flow)
}

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public inline fun <T> withState(select: FlowStateSelector.() -> T): T =
    FlowStateSelector.select()

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_2_2_0_ALPHA)
@WrongBytecodeGenerated(WrongBytecodeGenerated.STACK_SHOULD_BE_SPILLED, ReplaceWith("useBlockCrossinline(t, block)", "dev.brella.kornea.toolkit.common.useBlockCrossinline"))
public suspend inline fun <T : DataCloseable?, R> use(select: FlowStateSelector.() -> T, block: (T) -> R): R =
    FlowStateSelector.select().use(block)

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_2_2_0_ALPHA)
public suspend inline fun <T : DataCloseable?, R> useCrossinline(select: FlowStateSelector.() -> T, crossinline block: suspend (T) -> R): R =
    FlowStateSelector.select().useCrossinline(block)

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_2_2_0_ALPHA)
@WrongBytecodeGenerated(WrongBytecodeGenerated.STACK_SHOULD_BE_SPILLED, ReplaceWith("useCrossinline(select, block)", "dev.brella.kornea.io.common.flow.useCrossinline"))
public suspend inline fun <T : DataCloseable?, R> useSuspending(select: FlowStateSelector.() -> T, @Suppress("REDUNDANT_INLINE_SUSPEND_FUNCTION_TYPE") block: suspend (T) -> R): R =
    FlowStateSelector.select().useSuspending(block)

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_2_2_0_ALPHA)
public suspend inline fun <T : DataCloseable?, R> useBlockCrossinline(select: FlowStateSelector.() -> T, crossinline block: (T) -> R): R =
    FlowStateSelector.select().useBlockCrossinline(block)