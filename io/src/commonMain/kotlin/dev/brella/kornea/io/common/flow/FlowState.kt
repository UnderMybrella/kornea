package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.annotations.ChangedSince
import dev.brella.kornea.io.common.*

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public interface FlowState

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
@ChangedSince(KorneaIO.VERSION_5_0_0_ALPHA)
public interface InputFlowState: FlowState, InputFlow

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
@ChangedSince(KorneaIO.VERSION_5_0_0_ALPHA)
public interface OutputFlowState : FlowState, OutputFlow

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
@ChangedSince(KorneaIO.VERSION_5_0_0_ALPHA)
public interface Int16FlowState : FlowState {
    public open class Base(override val int16Packet: Int16Packet = Int16Packet()) : Int16FlowState
    public companion object {
        public inline fun base(): Base = Base()
    }

    public val int16Packet: Int16Packet
}

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
@ChangedSince(KorneaIO.VERSION_5_0_0_ALPHA)
public interface Int24FlowState : FlowState {
    public open class Base(override val int24Packet: Int24Packet = Int24Packet()) : Int24FlowState

    public companion object {
        public inline fun base(): Base = Base()
    }

    public val int24Packet: Int24Packet
}

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
@ChangedSince(KorneaIO.VERSION_5_0_0_ALPHA)
public interface Int32FlowState : FlowState {
    public open class Base(override val int32Packet: Int32Packet = Int32Packet()) : Int32FlowState

    public companion object {
        public inline fun base(): Base = Base()
    }

    public val int32Packet: Int32Packet
}

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
@ChangedSince(KorneaIO.VERSION_5_0_0_ALPHA)
public interface Int40FlowState : FlowState {
    public open class Base(override val int40Packet: Int40Packet = Int40Packet()) : Int40FlowState

    public companion object {
        public inline fun base(): Base = Base()
    }

    public val int40Packet: Int40Packet
}

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
@ChangedSince(KorneaIO.VERSION_5_0_0_ALPHA)
public interface Int48FlowState : FlowState {
    public open class Base(override val int48Packet: Int48Packet = Int48Packet()) : Int48FlowState

    public companion object {
        public inline fun base(): Base = Base()
    }

    public val int48Packet: Int48Packet
}

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
@ChangedSince(KorneaIO.VERSION_5_0_0_ALPHA)
public interface Int56FlowState : FlowState {
    public open class Base(override val int56Packet: Int56Packet = Int56Packet()) : Int56FlowState

    public companion object {
        public inline fun base(): Base = Base()
    }

    public val int56Packet: Int56Packet
}

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
@ChangedSince(KorneaIO.VERSION_5_0_0_ALPHA)
public interface Int64FlowState : FlowState {
    public open class Base(override val int64Packet: Int64Packet = Int64Packet()) : Int64FlowState

    public companion object {
        public inline fun base(): Base = Base()
    }

    public val int64Packet: Int64Packet
}

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
@ChangedSince(KorneaIO.VERSION_5_0_0_ALPHA)
public interface IntFlowState :
    Int16FlowState,
    Int24FlowState,
    Int32FlowState,
    Int40FlowState,
    Int48FlowState,
    Int56FlowState,
    Int64FlowState {
    public open class Base(protected val buffer: ByteArray = ByteArray(8)) : IntFlowState {
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

    public companion object {
        public inline fun base(): Base = Base()
    }
}