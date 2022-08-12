package dev.brella.kornea.serialisation.core.common

import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure

public interface ClassSerialisationStrategy<T>: SerializationStrategy<T> {
    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeStructure(descriptor) { serialize(value) }
    }

    public fun CompositeEncoder.serialize(value: T)
}