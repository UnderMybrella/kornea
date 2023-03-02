package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.WrongBytecodeGenerated
import dev.brella.kornea.composite.common.Constituent
import dev.brella.kornea.composite.common.withConstituent
import dev.brella.kornea.composite.common.withFlatConstituent
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.EnumSeekMode
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public interface SeekableFlow : KorneaFlowConstituent {
    public companion object Key : Constituent.Key<SeekableFlow>

    public suspend fun seek(pos: Long, mode: EnumSeekMode): ULong
}

public inline fun KorneaFlow.isSeekable(): Boolean =
    hasConstituent(SeekableFlow.Key)

public inline fun KorneaFlow.seekable(): KorneaResult<SeekableFlow> =
    getConstituent(SeekableFlow.Key)

public inline fun <T> KorneaFlow.seekable(block: SeekableFlow.() -> T): KorneaResult<T> =
    withConstituent(SeekableFlow.Key, block)

public inline fun <T> KorneaFlow.flatSeekable(block: SeekableFlow.() -> KorneaResult<T>): KorneaResult<T> =
    withFlatConstituent(SeekableFlow.Key, block)

@OptIn(ExperimentalContracts::class)
@WrongBytecodeGenerated(
    WrongBytecodeGenerated.STACK_SHOULD_BE_SPILLED,
    ReplaceWith("bookmarkCrossinline(t, block)", "dev.brella.kornea.io.common.flow.bookmarkCrossinline")
)
public suspend inline fun <R> SeekableFlow.bookmark(block: () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val position = flow.position()
    try {
        return block()
    } finally {
        seek(position.toLong(), EnumSeekMode.FROM_BEGINNING)
    }
}

public suspend inline fun <T : SeekableFlow, R> T.bookmark(seeking: ULong, block: T.() -> R): R =
    bookmark(seeking.toLong(), EnumSeekMode.FROM_BEGINNING, block)

@OptIn(ExperimentalContracts::class)
@WrongBytecodeGenerated(
    WrongBytecodeGenerated.STACK_SHOULD_BE_SPILLED,
    ReplaceWith("bookmarkCrossinline(t, block)", "dev.brella.kornea.io.common.flow.bookmarkCrossinline")
)
public suspend inline fun <T : SeekableFlow, R> T.bookmark(seeking: Long, mode: EnumSeekMode, block: T.() -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val position = flow.position()
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

    val position = t.flow.position()
    try {
        return block()
    } finally {
        t.seek(position.toLong(), EnumSeekMode.FROM_BEGINNING)
    }
}