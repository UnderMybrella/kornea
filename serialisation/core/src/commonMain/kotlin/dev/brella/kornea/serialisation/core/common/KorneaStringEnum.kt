package dev.brella.kornea.serialisation.core.common

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

public interface KorneaStringEnum {
    public val type: String
}

public enum class EnumCaseSensitivity(public val transform: (String) -> String) {
    AS_IS(String::toString),
    LOWER_CASE(String::lowercase),
    UPPER_CASE(String::uppercase)
}

public open class KorneaStringEnumSerialiser<T : KorneaStringEnum>(
    public val values: Lazy<Map<String, T>>,
    public val serialName: String,
    public val deserialisingCaseSensitivity: EnumCaseSensitivity = EnumCaseSensitivity.AS_IS,
    public val serialisingCaseSensitivity: EnumCaseSensitivity = EnumCaseSensitivity.AS_IS,
    public val default: (String) -> T,
) : KSerializer<T> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(serialName, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeString(serialisingCaseSensitivity.transform(value.type))
    }

    override fun deserialize(decoder: Decoder): T {
        val type = deserialisingCaseSensitivity.transform(decoder.decodeString())

        return values.value[type] ?: default(type)
    }
}

public open class KorneaEnumLikeSerialiser<T>(
    public val values: Map<String, T>,
    public val serialName: String,
    public val deserialisingCaseSensitivity: EnumCaseSensitivity = EnumCaseSensitivity.AS_IS,
    public val serialisingCaseSensitivity: EnumCaseSensitivity = EnumCaseSensitivity.AS_IS,
    public val typeToDefault: (String) -> T,
    public val unknownToType: (T) -> String,
) : KSerializer<T> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(serialName, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeString(serialisingCaseSensitivity.transform(
            (values.firstNotNullOfOrNull { (k, v) -> if (v == value) k else null } ?: unknownToType(value))
        ))
    }

    override fun deserialize(decoder: Decoder): T {
        val type = deserialisingCaseSensitivity.transform(decoder.decodeString())

        return values[type] ?: typeToDefault(type)
    }
}