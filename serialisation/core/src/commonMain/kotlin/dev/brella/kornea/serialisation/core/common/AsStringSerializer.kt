package dev.brella.kornea.serialisation.core.common

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

public open class AsStringSerializer<T>(serialName: String, private val asString: (T) -> String, private val fromString: (String) -> T) : KSerializer<T> {
    public override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(serialName, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeString(asString(value))
    }

    override fun deserialize(decoder: Decoder): T {
        return fromString(decoder.decodeString())
    }
}