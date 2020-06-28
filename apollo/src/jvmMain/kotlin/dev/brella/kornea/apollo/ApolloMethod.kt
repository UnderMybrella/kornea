@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")

package dev.brella.kornea.apollo

import dev.brella.kornea.toolkit.common.mapToArray
import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

public inline class ApolloMethod0<in SELF: Any, out RET: Any>(public val method: Method) {
    public class Provider<RET: Any> private constructor(public val klass: KClass<RET>) {
        public companion object {
            private val INSTANCES: MutableMap<KClass<*>, Provider<*>> = HashMap()

            public operator fun <T : Any> invoke(klass: KClass<T>): Provider<T> =
                INSTANCES.computeIfAbsent(klass) { Provider(it) } as Provider<T>

            public inline operator fun <reified RET: Any> invoke(): Provider<RET> = invoke(RET::class)
        }

        public operator fun <S : Any> provideDelegate(thisRef: S, prop: KProperty<*>): Lazy<ApolloMethod0<S, RET>> =
            lazy { reflectiveMethod(thisRef::class, true, prop.name, klass) }
    }
    public operator fun invoke(self: SELF): RET = method.invoke(self) as RET
}

public inline fun <reified T : Any> method(): ApolloMethod0.Provider<T> = ApolloMethod0.Provider()

public inline fun <S : Any, R: Any> reflectiveMethod(from: KClass<out S>, startWithSuperclass: Boolean, methodName: String, returnType: KClass<R>): ApolloMethod0<S, R> =
    ApolloMethod0(reflectiveMethod(from.java.orSupertype(startWithSuperclass), methodName, returnType.java, 0))

public inline fun <S : Any> reflectiveMethod(from: Class<S>, methodName: String, returnType: KClass<*>, argumentCount: Int, vararg argumentTypes: KClass<*>): Method =
    reflectiveMethod(from, methodName, returnType.java, argumentCount, argumentTypes = argumentTypes.mapToArray(KClass<*>::java))


public inline fun <S : Any> reflectiveMethod(from: Class<S>, methodName: String, returnType: Class<*>, argumentCount: Int, vararg argumentTypes: Class<*>): Method {
    var klass: Class<*>? = from
    while (klass != null) {
        val method = klass.declaredMethods.firstOrNull { method ->
            @Suppress("PlatformExtensionReceiverOfInline")
            (method.name == methodName && method.parameterCount == argumentCount && method.returnType == returnType && method.annotations.none { annotation -> annotation is Reflective })
            && ((method.parameterTypes.isEmpty() && argumentTypes.isEmpty()) || method.parameterTypes.contentEquals(argumentTypes))
        }

        if (method != null) {
            method.isAccessible = true
            return method
        }

        klass = klass.superclass
    }

    throw NoSuchMethodException(methodName)
}