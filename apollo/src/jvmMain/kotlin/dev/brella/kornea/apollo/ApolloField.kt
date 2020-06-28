@file:Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")

package dev.brella.kornea.apollo

import java.lang.reflect.Field
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

public class ApolloField<out T : Any?> private constructor() {
    public companion object {
        private val INSTANCES: MutableMap<KClass<*>, ApolloField<*>> = HashMap()

        public operator fun <T: Any> invoke(klass: KClass<T>): ApolloField<T> =
            INSTANCES.computeIfAbsent(klass) { ApolloField<T?>() } as ApolloField<T>

        public inline operator fun <reified T : Any> invoke(): ApolloField<T> = ApolloField(T::class)
    }

    public operator fun <S : Any> provideDelegate(thisRef: S, prop: KProperty<*>): Lazy<T> =
        lazy { reflectiveFieldInstance(thisRef, prop.name) }
}

public inline fun <reified T: Any> field(): ApolloField<T> = ApolloField(T::class)
public inline fun <reified T: Any> nullableField(): ApolloField<T?> = ApolloField(T::class)
public inline fun <S> reflectiveField(from: Class<S>, fieldName: String): Field {
    var klass: Class<*>? = from
    while (klass != null) {
        val field = klass.declaredFields.firstOrNull { field -> field.name == fieldName && field.annotations.none { annotation -> annotation is Reflective } }
        if (field != null) {
            field.isAccessible = true
            return field
        }

        klass = klass.superclass
    }

    throw NoSuchFieldException(fieldName)
}

@Suppress("UNCHECKED_CAST")
public inline fun <S : Any, T> reflectiveFieldInstance(from: S, fieldName: String): T =
    reflectiveField(from::class.java, fieldName)[from] as T