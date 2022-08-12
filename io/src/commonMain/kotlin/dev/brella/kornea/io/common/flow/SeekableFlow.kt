package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.WrongBytecodeGenerated
import dev.brella.kornea.io.common.EnumSeekMode
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public interface SeekableFlow: KorneaFlow {
    public suspend fun seek(pos: Long, mode: EnumSeekMode): ULong
}

//public suspend inline fun <T : SeekableInputFlow, R> T.bookmark(block: () -> R): R = bookmark(this, block)
@OptIn(ExperimentalContracts::class)
@WrongBytecodeGenerated(WrongBytecodeGenerated.STACK_SHOULD_BE_SPILLED, ReplaceWith("bookmarkCrossinline(t, block)", "dev.brella.kornea.io.common.flow.bookmarkCrossinline"))
public suspend inline fun <T : SeekableFlow, R> bookmark(t: T, block: () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val position = t.position()
    try {
        return block()
    } finally {
        t.seek(position.toLong(), EnumSeekMode.FROM_BEGINNING)
    }
}

public suspend inline fun <T : SeekableFlow, R> T.bookmark(seeking: ULong, block: T.() -> R): R =
    bookmark(seeking.toLong(), EnumSeekMode.FROM_BEGINNING, block)

@OptIn(ExperimentalContracts::class)
@WrongBytecodeGenerated(WrongBytecodeGenerated.STACK_SHOULD_BE_SPILLED, ReplaceWith("bookmarkCrossinline(t, block)", "dev.brella.kornea.io.common.flow.bookmarkCrossinline"))
public suspend inline fun <T : SeekableFlow, R> T.bookmark(seeking: Long, mode: EnumSeekMode, block: T.() -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val position = position()
    try {
        seek(seeking, mode)
        return block()
    } finally {
        seek(position.toLong(), EnumSeekMode.FROM_BEGINNING)
    }
}

public suspend inline fun <T : SeekableFlow, R> bookmarkCrossinline(t: T, crossinline block: suspend () -> R): R {
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