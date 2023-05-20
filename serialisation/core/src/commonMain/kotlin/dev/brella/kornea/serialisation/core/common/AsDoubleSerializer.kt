package dev.brella.kornea.serialisation.core.common

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

public open class AsDoubleSerializer<T>(serialName: String, private val asDouble: (T) -> Double, private val fromDouble: (Double) -> T) : KSerializer<T> {
    public override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(serialName, PrimitiveKind.DOUBLE)

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeDouble(asDouble(value))
    }

    override fun deserialize(decoder: Decoder): T {
        return fromDouble(decoder.decodeDouble())
    }
}