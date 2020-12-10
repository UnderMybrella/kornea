package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.WrongBytecodeGenerated
import dev.brella.kornea.io.common.EnumSeekMode

@ExperimentalUnsignedTypes
public interface SeekableInputFlow: InputFlow {
    public suspend fun seek(pos: Long, mode: EnumSeekMode): ULong
}

//@ExperimentalUnsignedTypes
//public suspend inline fun <T : SeekableInputFlow, R> T.bookmark(block: () -> R): R = bookmark(this, block)
@ExperimentalUnsignedTypes
@WrongBytecodeGenerated(WrongBytecodeGenerated.STACK_SHOULD_BE_SPILLED, ReplaceWith("bookmarkCrossinline(t, block)", "dev.brella.kornea.io.common.flow.bookmarkCrossinline"))
public suspend inline fun <T : SeekableInputFlow, R> bookmark(t: T, block: () -> R): R {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//    }

    val position = t.position()
    try {
        return block()
    } finally {
        t.seek(position.toLong(), EnumSeekMode.FROM_BEGINNING)
    }
}

@ExperimentalUnsignedTypes
public suspend inline fun <T : SeekableInputFlow, R> bookmarkCrossinline(t: T, crossinline block: suspend () -> R): R {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//    }

    val position = t.position()
    try {
        return block()
    } finally {
        t.seek(position.toLong(), EnumSeekMode.FROM_BEGINNING)
    }
}