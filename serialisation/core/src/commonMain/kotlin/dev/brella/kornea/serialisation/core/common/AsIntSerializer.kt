package dev.brella.kornea.serialisation.core.common

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

public open class AsIntSerializer<T>(serialName: String, private val asInt: (T) -> Int, private val fromInt: (Int) -> T) : KSerializer<T> {
    public override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(serialName, PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeInt(asInt(value))
    }

    override fun deserialize(decoder: Decoder): T {
        return fromInt(decoder.decodeInt())
    }
}