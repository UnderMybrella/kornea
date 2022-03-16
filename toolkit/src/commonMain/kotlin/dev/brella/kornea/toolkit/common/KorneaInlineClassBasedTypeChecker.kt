package dev.brella.kornea.toolkit.common

import kotlin.jvm.JvmInline
import kotlin.reflect.KClass

@JvmInline
internal value class KorneaInlineClassBasedTypeChecker<T : Any>(override val typeClass: KClass<T>): KorneaTypeChecker.ClassBased<T> {
    companion object {}
}

public fun <T: Any> KorneaTypeChecker.ClassBased.Companion.inline(typeClass: KClass<T>): KorneaTypeChecker.ClassBased<T> = KorneaInlineClassBasedTypeChecker(typeClass)