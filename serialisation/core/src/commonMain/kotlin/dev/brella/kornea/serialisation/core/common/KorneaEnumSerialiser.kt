package dev.brella.kornea.serialisation.core.common

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

public interface KorneaEnum {
    public val type: Int
}

public sealed class KorneaEnumSerialiser<T : KorneaEnum>(
    public val serialName: String,
    public val default: (Int) -> T,
) : KSerializer<T> {
    public sealed class MapBased<T : KorneaEnum>(
        public val values: Lazy<Map<Int, T>>,
        serialName: String,
        default: (Int) -> T,
    ) : KorneaEnumSerialiser<T>(serialName, default) {
        public open class AsByte<T : KorneaEnum>(values: Lazy<Map<Int, T>>, serialName: String, default: (Int) -> T) :
            MapBased<T>(values, serialName, default) {
            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor(serialName, PrimitiveKind.BYTE)

            override fun serialize(encoder: Encoder, value: T) {
                encoder.encodeByte(value.type.toByte())
            }

            override fun deserialize(decoder: Decoder): T {
                val type = decoder.decodeByte().toInt() and 0xFF

                return values.value[type] ?: default(type)
            }
        }

        public open class AsInt<T : KorneaEnum>(values: Lazy<Map<Int, T>>, serialName: String, default: (Int) -> T) :
            MapBased<T>(values, serialName, default) {
            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor(serialName, PrimitiveKind.INT)

            override fun serialize(encoder: Encoder, value: T) {
                encoder.encodeInt(value.type)
            }

            override fun deserialize(decoder: Decoder): T {
                val type = decoder.decodeInt()

                return values.value[type] ?: default(type)
            }
        }
    }

    public sealed class ArrayBased<T : KorneaEnum>(
        public val values: Lazy<Array<T?>>,
        serialName: String,
        default: (Int) -> T,
    ) : KorneaEnumSerialiser<T>(serialName, default) {
        public open class AsByte<T : KorneaEnum>(values: Lazy<Array<T?>>, serialName: String, default: (Int) -> T) :
            ArrayBased<T>(values, serialName, default) {
            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor(serialName, PrimitiveKind.BYTE)

            override fun serialize(encoder: Encoder, value: T) {
                encoder.encodeByte(value.type.toByte())
            }

            override fun deserialize(decoder: Decoder): T {
                val type = decoder.decodeByte().toInt() and 0xFF

                return values.value.getOrNull(type) ?: default(type)
            }
        }

        public open class AsInt<T : KorneaEnum>(values: Lazy<Array<T?>>, serialName: String, default: (Int) -> T) :
            ArrayBased<T>(values, serialName, default) {
            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor(serialName, PrimitiveKind.INT)

            override fun serialize(encoder: Encoder, value: T) {
                encoder.encodeInt(value.type)
            }

            override fun deserialize(decoder: Decoder): T {
                val type = decoder.decodeInt()

                return values.value.getOrNull(type) ?: default(type)
            }
        }
    }
}

public sealed class KorneaEnumLikeSerialiser<T>(
    public val serialName: String,
    public val typeToDefault: (Int) -> T,
    public val unknownToType: (T) -> Int,
) : KSerializer<T> {
    public sealed class MapBased<T>(
        public val values: Map<Int, T>,
        serialName: String,
        typeToDefault: (Int) -> T,
        unknownToType: (T) -> Int,
    ) : KorneaEnumLikeSerialiser<T>(serialName, typeToDefault, unknownToType) {
        public open class AsByte<T>(
            values: Map<Int, T>,
            serialName: String,
            typeToDefault: (Int) -> T,
            unknownToType: (T) -> Int,
        ) : MapBased<T>(values, serialName, typeToDefault, unknownToType) {
            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor(serialName, PrimitiveKind.BYTE)

            override fun serialize(encoder: Encoder, value: T) {
                encoder.encodeByte((values.firstNotNullOfOrNull { (k, v) -> if (v == value) k else null }
                    ?: unknownToType(value)).toByte())
            }

            override fun deserialize(decoder: Decoder): T {
                val type = decoder.decodeByte().toInt() and 0xFF

                return values[type] ?: typeToDefault(type)
            }
        }

        public open class AsInt<T>(
            values: Map<Int, T>,
            serialName: String,
            typeToDefault: (Int) -> T,
            unknownToType: (T) -> Int,
        ) : MapBased<T>(values, serialName, typeToDefault, unknownToType) {
            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor(serialName, PrimitiveKind.INT)

            override fun serialize(encoder: Encoder, value: T) {
                encoder.encodeInt(values.firstNotNullOfOrNull { (k, v) -> if (v == value) k else null }
                    ?: unknownToType(value))
            }

            override fun deserialize(decoder: Decoder): T {
                val type = decoder.decodeInt()

                return values[type] ?: typeToDefault(type)
            }
        }
    }

    public sealed class ArrayBased<T>(
        public val values: Array<T?>,
        serialName: String,
        typeToDefault: (Int) -> T,
        unknownToType: (T) -> Int,
    ) : KorneaEnumLikeSerialiser<T>(serialName, typeToDefault, unknownToType) {
        public open class AsByte<T>(
            values: Array<T?>,
            serialName: String,
            typeToDefault: (Int) -> T,
            unknownToType: (T) -> Int,
        ) : ArrayBased<T>(values, serialName, typeToDefault, unknownToType) {
            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor(serialName, PrimitiveKind.BYTE)

            override fun serialize(encoder: Encoder, value: T) {
                encoder.encodeByte((values.indexOf(value).takeUnless { it == -1 } ?: unknownToType(value)).toByte())
            }

            override fun deserialize(decoder: Decoder): T {
                val type = decoder.decodeByte().toInt() and 0xFF

                return values.getOrNull(type) ?: typeToDefault(type)
            }
        }

        public open class AsInt<T>(
            values: Array<T?>,
            serialName: String,
            typeToDefault: (Int) -> T,
            unknownToType: (T) -> Int,
        ) : ArrayBased<T>(values, serialName, typeToDefault, unknownToType) {
            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor(serialName, PrimitiveKind.INT)

            override fun serialize(encoder: Encoder, value: T) {
                encoder.encodeInt(values.indexOf(value).takeUnless { it == -1 } ?: unknownToType(value))
            }

            override fun deserialize(decoder: Decoder): T {
                val type = decoder.decodeInt()

                return values.getOrNull(type) ?: typeToDefault(type)
            }
        }
    }
}

public inline fun <reified T> lazyArray(size: Int, noinline block: Array<T?>.() -> Unit): Lazy<Array<T?>> = lazy { arrayOfNulls<T>(size).apply(block) }
public inline fun <reified K, reified V> lazyMap(noinline block: MutableMap<K, V>.() -> Unit): Lazy<Map<K, V>> = lazy { buildMap(block) }

public inline fun <reified T> buildArray(size: Int, block: Array<T?>.() -> Unit): Array<T?> =
    arrayOfNulls<T>(size).apply(block)

public inline fun <T: KorneaEnum> Array<T?>.with(value: T): Array<T?> {
    this[value.type] = value
    return this
}