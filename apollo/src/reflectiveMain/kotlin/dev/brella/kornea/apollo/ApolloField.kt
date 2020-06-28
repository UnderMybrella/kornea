@file:Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")

package dev.brella.kornea.apollo

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

public class ApolloField<out T : Any?> private constructor() {
    public companion object {
        private val INSTANCES: MutableMap<KClass<*>, ApolloField<*>> = HashMap()

        public operator fun <T: Any> invoke(klass: KClass<T>): ApolloField<T> =
            INSTANCES.getOrPut(klass) { ApolloField<T?>() } as ApolloField<T>

        public inline operator fun <reified T : Any> invoke(): ApolloField<T> =
            ApolloField(T::class)
    }

    public operator fun <S : Any> provideDelegate(thisRef: S, prop: KProperty<*>): Lazy<T> =
        lazy { reflectiveFieldInstance(thisRef, prop.name) }
}

public inline fun <reified T: Any> field(): ApolloField<T> =
    ApolloField(T::class)
public inline fun <reified T: Any> nullableField(): ApolloField<T?> =
    ApolloField(T::class)
public expect inline fun <S : Any, T> reflectiveField(from: KClass<out S>, fieldName: String): (S) -> T
@Suppress("UNCHECKED_CAST")
public inline fun <S : Any, T> reflectiveFieldInstance(from: S, fieldName: String): T =
    reflectiveField<S, T>(from::class, fieldName)(from)