@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")

package dev.brella.kornea.apollo

import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

public inline class ApolloMethod0<in SELF: Any, out RET: Any>(public val method: Function1<SELF, RET>) {
    public class Provider<RET: Any> private constructor(public val klass: KClass<RET>) {
        public companion object {
            private val INSTANCES: MutableMap<KClass<*>, Provider<*>> = HashMap()

            public operator fun <T : Any> invoke(klass: KClass<T>): Provider<T> =
                INSTANCES.computeIfAbsent(klass) { Provider(it) } as Provider<T>

            public inline operator fun <reified RET: Any> invoke(): Provider<RET> = invoke(RET::class)
        }

        public operator fun <S : Any> provideDelegate(thisRef: S, prop: KProperty<*>): Lazy<ApolloMethod0<S, RET>> =
            lazy { reflectiveMethod0(thisRef::class, true, prop.name, klass) }
    }
    public operator fun invoke(self: SELF): RET = method.invoke(self) as RET
}

public inline fun <reified T : Any> method(): ApolloMethod0.Provider<T> = ApolloMethod0.Provider()

public expect inline fun <S : Any, R: Any> reflectiveMethod0(from: KClass<out S>, startWithSuperclass: Boolean, methodName: String, returnType: KClass<R>): ApolloMethod0<S, R>
//public expect inline fun <S : Any, R: Any> reflectiveMethod(from: KClass<out S>, methodName: String, returnType: KClass<R>, argumentCount: Int, vararg argumentTypes: KClass<*>): Function<R>

public expect inline fun <S : Any> reflectiveMethod(from: KClass<S>, methodName: String, returnType: KClass<*>, argumentCount: Int, vararg argumentTypes: KClass<*>): Function<*>