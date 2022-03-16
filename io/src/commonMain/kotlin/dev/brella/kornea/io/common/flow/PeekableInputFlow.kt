package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.io.common.FlowPacket
import dev.brella.kornea.io.common.KorneaIO

public interface PeekableInputFlow : InputFlow {
    public suspend fun peek(forward: Int): Int?

    @AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
    public suspend fun peek(forward: Int, b: ByteArray): Int? = peek(forward, b, 0, b.size)

    @AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
    public suspend fun peek(forward: Int, b: ByteArray, off: Int, len: Int): Int?
}

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public suspend inline fun PeekableInputFlow.peek(): Int? = peek(1)

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public suspend inline fun PeekableInputFlow.peek(b: ByteArray): Int? = peek(1, b)

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public suspend inline fun PeekableInputFlow.peek(b: ByteArray, off: Int, len: Int): Int? = peek(1, b, off, len)

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public suspend inline fun PeekableInputFlow.peekPacket(forward: Int, packet: FlowPacket): ByteArray? =
    if (peek(forward, packet.buffer, 0, packet.size) == packet.size) packet.buffer else null

@AvailableSince(KorneaIO.VERSION_2_0_0_ALPHA)
public suspend inline fun PeekableInputFlow.peekPacket(packet: FlowPacket): ByteArray? =
    if (peek(packet.buffer, 0, packet.size) == packet.size) packet.buffer else null