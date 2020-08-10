package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.annotations.ChangedSince
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
@ChangedSince(KorneaIO.VERSION_3_0_0_ALPHA)
public interface InputFlowState<out F : InputFlow> : FlowState, InputFlow {
    public val flow: F
}

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
@ChangedSince(KorneaIO.VERSION_3_0_0_ALPHA)
public interface OutputFlowState<out F : OutputFlow> : FlowState, OutputFlow {
    public val flow: F
}

@AvailableSince(KorneaIO.VERSION_3_0_0_ALPHA)
public suspend inline fun InputFlowState<PeekableInputFlow>.peek(forward: Int): Int? = flow.peek(forward)

@AvailableSince(KorneaIO.VERSION_3_0_0_ALPHA)
public suspend inline fun InputFlowState<PeekableInputFlow>.peek(forward: Int, b: ByteArray): Int? =
    flow.peek(forward, b)

@AvailableSince(KorneaIO.VERSION_3_0_0_ALPHA)
public suspend inline fun InputFlowState<PeekableInputFlow>.peek(forward: Int, b: ByteArray, off: Int, len: Int): Int? =
    flow.peek(forward, b, off, len)

@AvailableSince(KorneaIO.VERSION_3_0_0_ALPHA)
public suspend inline fun InputFlowState<PeekableInputFlow>.peek(): Int? = flow.peek()

@AvailableSince(KorneaIO.VERSION_3_0_0_ALPHA)
public suspend inline fun InputFlowState<PeekableInputFlow>.peek(b: ByteArray): Int? = flow.peek(b)

@AvailableSince(KorneaIO.VERSION_3_0_0_ALPHA)
public suspend inline fun InputFlowState<PeekableInputFlow>.peek(b: ByteArray, off: Int, len: Int): Int? =
    flow.peek(b, off, len)

@AvailableSince(KorneaIO.VERSION_3_0_0_ALPHA)
public suspend inline fun InputFlowState<PeekableInputFlow>.peekPacket(forward: Int, packet: FlowPacket): ByteArray? =
    flow.peekPacket(forward, packet)

@AvailableSince(KorneaIO.VERSION_3_0_0_ALPHA)
public suspend inline fun InputFlowState<PeekableInputFlow>.peekPacket(packet: FlowPacket): ByteArray? =
    flow.peekPacket(packet)

@AvailableSince(KorneaIO.VERSION_3_0_0_ALPHA)
public suspend inline fun InputFlowState<SeekableInputFlow>.seek(pos: Long, mode: EnumSeekMode): ULong =
    flow.seek(pos, mode)

//@ExperimentalUnsignedTypes
//public suspend inline fun <T : SeekableInputFlow, R> T.bookmark(block: () -> R): R = bookmark(this, block)
@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_3_0_0_ALPHA)
@WrongBytecodeGenerated(
    WrongBytecodeGenerated.STACK_SHOULD_BE_SPILLED,
    ReplaceWith("bookmarkCrossinline(t, block)", "dev.brella.kornea.io.common.flow.bookmarkCrossinline")
)
public suspend inline fun <T : InputFlowState<SeekableInputFlow>, R> bookmark(t: T, block: () -> R): R {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//    }

    val position = t.flow.position()
    try {
        return block()
    } finally {
        t.flow.seek(position.toLong(), EnumSeekMode.FROM_BEGINNING)
    }
}

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_3_0_0_ALPHA)
public suspend inline fun <T : InputFlowState<SeekableInputFlow>, R> bookmarkCrossinline(
    t: T,
    crossinline block: suspend () -> R
): R {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//    }

    val position = t.position()
    try {
        return block()
    } finally {
        t.flow.seek(position.toLong(), EnumSeekMode.FROM_BEGINNING)
    }
}

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public interface Int16FlowState : FlowState {
    public abstract class Base(override val int16Packet: Int16Packet = Int16Packet()) : Int16FlowState
    public open class BaseInput<F : InputFlow>(override val flow: F) : Base(), InputFlowState<F>, InputFlow by flow
    public open class BaseOutput<F : OutputFlow>(override val flow: F) : Base(), OutputFlowState<F>, OutputFlow by flow

    public companion object {
        public inline fun <F : InputFlow> input(flow: F): BaseInput<F> = BaseInput(flow)
        public inline fun <F : OutputFlow> output(flow: F): BaseOutput<F> = BaseOutput(flow)
    }

    public val int16Packet: Int16Packet
}

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public interface Int24FlowState : FlowState {
    public abstract class Base(override val int24Packet: Int24Packet = Int24Packet()) : Int24FlowState
    public open class BaseInput<F : InputFlow>(override val flow: F) : Base(), InputFlowState<F>, InputFlow by flow
    public open class BaseOutput<F : OutputFlow>(override val flow: F) : Base(), OutputFlowState<F>, OutputFlow by flow

    public companion object {
        public inline fun <F : InputFlow> input(flow: F): BaseInput<F> = BaseInput(flow)
        public inline fun <F : OutputFlow> output(flow: F): BaseOutput<F> = BaseOutput(flow)
    }

    public val int24Packet: Int24Packet
}

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public interface Int32FlowState : FlowState {
    public abstract class Base(override val int32Packet: Int32Packet = Int32Packet()) : Int32FlowState
    public open class BaseInput<F : InputFlow>(override val flow: F) : Base(), InputFlowState<F>, InputFlow by flow
    public open class BaseOutput<F : OutputFlow>(override val flow: F) : Base(), OutputFlowState<F>, OutputFlow by flow

    public companion object {
        public inline fun <F : InputFlow> input(flow: F): BaseInput<F> = BaseInput(flow)
        public inline fun <F : OutputFlow> output(flow: F): BaseOutput<F> = BaseOutput(flow)
    }

    public val int32Packet: Int32Packet
}

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public interface Int40FlowState : FlowState {
    public abstract class Base(override val int40Packet: Int40Packet = Int40Packet()) : Int40FlowState
    public open class BaseInput<F : InputFlow>(override val flow: F) : Base(), InputFlowState<F>, InputFlow by flow
    public open class BaseOutput<F : OutputFlow>(override val flow: F) : Base(), OutputFlowState<F>, OutputFlow by flow

    public companion object {
        public inline fun <F : InputFlow> input(flow: F): BaseInput<F> = BaseInput(flow)
        public inline fun <F : OutputFlow> output(flow: F): BaseOutput<F> = BaseOutput(flow)
    }

    public val int40Packet: Int40Packet
}

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public interface Int48FlowState : FlowState {
    public abstract class Base(override val int48Packet: Int48Packet = Int48Packet()) : Int48FlowState
    public open class BaseInput<F : InputFlow>(override val flow: F) : Base(), InputFlowState<F>, InputFlow by flow
    public open class BaseOutput<F : OutputFlow>(override val flow: F) : Base(), OutputFlowState<F>, OutputFlow by flow

    public companion object {
        public inline fun <F : InputFlow> input(flow: F): BaseInput<F> = BaseInput(flow)
        public inline fun <F : OutputFlow> output(flow: F): BaseOutput<F> = BaseOutput(flow)
    }

    public val int48Packet: Int48Packet
}

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public interface Int56FlowState : FlowState {
    public abstract class Base(override val int56Packet: Int56Packet = Int56Packet()) : Int56FlowState
    public open class BaseInput<F : InputFlow>(override val flow: F) : Base(), InputFlowState<F>, InputFlow by flow
    public open class BaseOutput<F : OutputFlow>(override val flow: F) : Base(), OutputFlowState<F>, OutputFlow by flow

    public companion object {
        public inline fun <F : InputFlow> input(flow: F): BaseInput<F> = BaseInput(flow)
        public inline fun <F : OutputFlow> output(flow: F): BaseOutput<F> = BaseOutput(flow)
    }

    public val int56Packet: Int56Packet
}

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public interface Int64FlowState : FlowState {
    public abstract class Base(override val int64Packet: Int64Packet = Int64Packet()) : Int64FlowState
    public open class BaseInput<F : InputFlow>(override val flow: F) : Base(), InputFlowState<F>, InputFlow by flow
    public open class BaseOutput<F : OutputFlow>(override val flow: F) : Base(), OutputFlowState<F>, OutputFlow by flow

    public companion object {
        public inline fun <F : InputFlow> input(flow: F): BaseInput<F> = BaseInput(flow)
        public inline fun <F : OutputFlow> output(flow: F): BaseOutput<F> = BaseOutput(flow)
    }

    public val int64Packet: Int64Packet
}

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public interface IntFlowState :
    Int16FlowState,
    Int24FlowState,
    Int32FlowState,
    Int40FlowState,
    Int48FlowState,
    Int56FlowState,
    Int64FlowState {
    public abstract class Base(protected val buffer: ByteArray = ByteArray(8)) : IntFlowState {
        override val int16Packet: Int16Packet
            get() = Int16Packet(buffer)
        override val int24Packet: Int24Packet
            get() = Int24Packet(buffer)
        override val int32Packet: Int32Packet
            get() = Int32Packet(buffer)
        override val int40Packet: Int40Packet
            get() = Int40Packet(buffer)
        override val int48Packet: Int48Packet
            get() = Int48Packet(buffer)
        override val int56Packet: Int56Packet
            get() = Int56Packet(buffer)
        override val int64Packet: Int64Packet
            get() = Int64Packet(buffer)
    }

    public open class BaseInput<F : InputFlow>(override val flow: F) : Base(), InputFlowState<F>, InputFlow by flow
    public open class BaseOutput<F : OutputFlow>(override val flow: F) : Base(), OutputFlowState<F>, OutputFlow by flow

    public companion object {
        public inline fun <F : InputFlow> input(flow: F): BaseInput<F> = BaseInput(flow)
        public inline fun <F : OutputFlow> output(flow: F): BaseOutput<F> = BaseOutput(flow)
    }
}

public interface InputFlowStateSelector {
    public companion object : InputFlowStateSelector
}

public interface OutputFlowStateSelector {
    public companion object : OutputFlowStateSelector
}

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public interface FlowStateSelector : InputFlowStateSelector, OutputFlowStateSelector {
    public companion object : FlowStateSelector
}

public inline fun <F : InputFlow> InputFlowStateSelector.int(flow: F): IntFlowState.BaseInput<F> =
    IntFlowState.input(flow)

public inline fun <F : OutputFlow> OutputFlowStateSelector.int(flow: F): IntFlowState.BaseOutput<F> =
    IntFlowState.output(flow)

public inline fun <F : InputFlow> InputFlowStateSelector.int16(flow: F): Int16FlowState.BaseInput<F> =
    Int16FlowState.input(flow)

public inline fun <F : OutputFlow> OutputFlowStateSelector.int16(flow: F): Int16FlowState.BaseOutput<F> =
    Int16FlowState.output(flow)

public inline fun <F : InputFlow> InputFlowStateSelector.int32(flow: F): Int32FlowState.BaseInput<F> =
    Int32FlowState.input(flow)

public inline fun <F : OutputFlow> OutputFlowStateSelector.int32(flow: F): Int32FlowState.BaseOutput<F> =
    Int32FlowState.output(flow)

public inline fun <F : InputFlow> InputFlowStateSelector.int64(flow: F): Int64FlowState.BaseInput<F> =
    Int64FlowState.input(flow)

public inline fun <F : OutputFlow> OutputFlowStateSelector.int64(flow: F): Int64FlowState.BaseOutput<F> =
    Int64FlowState.output(flow)

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public inline fun <T> withState(select: FlowStateSelector.() -> T): T =
    FlowStateSelector.select()

@AvailableSince(KorneaIO.VERSION_2_3_0_ALPHA)
public inline fun <T : DataCloseable, R> KorneaResult<T>.mapWithState(select: FlowStateSelector.(T) -> R): KorneaResult<R> =
    map { FlowStateSelector.select(it) }

@AvailableSince(KorneaIO.VERSION_3_1_0_ALPHA)
public suspend inline fun <T : DataCloseable, reified R> KorneaResult<T>.useMapWithState(select: FlowStateSelector.(T) -> R): KorneaResult<R> =
    useAndMap { FlowStateSelector.select(it) }

@AvailableSince(KorneaIO.VERSION_3_1_0_ALPHA)
public suspend inline fun <T : DataCloseable, reified R> KorneaResult<T>.useFlatMapWithState(select: FlowStateSelector.(T) -> KorneaResult<R>): KorneaResult<R> =
    useAndFlatMap { FlowStateSelector.select(it) }

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_2_2_0_ALPHA)
@WrongBytecodeGenerated(
    WrongBytecodeGenerated.STACK_SHOULD_BE_SPILLED,
    ReplaceWith("useBlockCrossinline(t, block)", "dev.brella.kornea.toolkit.common.useBlockCrossinline")
)
public suspend inline fun <T : DataCloseable?, R> use(select: FlowStateSelector.() -> T, block: (T) -> R): R =
    FlowStateSelector.select().use(block)

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_2_2_0_ALPHA)
public suspend inline fun <T : DataCloseable?, R> useCrossinline(
    select: FlowStateSelector.() -> T,
    crossinline block: suspend (T) -> R
): R =
    FlowStateSelector.select().useCrossinline(block)

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_2_2_0_ALPHA)
@WrongBytecodeGenerated(
    WrongBytecodeGenerated.STACK_SHOULD_BE_SPILLED,
    ReplaceWith("useCrossinline(select, block)", "dev.brella.kornea.io.common.flow.useCrossinline")
)
public suspend inline fun <T : DataCloseable?, R> useSuspending(
    select: FlowStateSelector.() -> T,
    @Suppress("REDUNDANT_INLINE_SUSPEND_FUNCTION_TYPE") block: suspend (T) -> R
): R =
    FlowStateSelector.select().useSuspending(block)

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_2_2_0_ALPHA)
public suspend inline fun <T : DataCloseable?, R> useBlockCrossinline(
    select: FlowStateSelector.() -> T,
    crossinline block: (T) -> R
): R =
    FlowStateSelector.select().useBlockCrossinline(block)