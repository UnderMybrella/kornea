package dev.brella.kornea.serialisation.core.common

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

public open class AsLongSerializer<T>(serialName: String, private val asLong: (T) -> Long, private val fromLong: (Long) -> T) : KSerializer<T> {
    public override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(serialName, PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeLong(asLong(value))
    }

    override fun deserialize(decoder: Decoder): T {
        return fromLong(decoder.decodeLong())
    }
}