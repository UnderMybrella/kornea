package dev.brella.kornea.toolkit.common

import dev.brella.kornea.annotations.AvailableSince
import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.reflect.safeCast

@AvailableSince(KorneaToolkit.VERSION_1_2_0_INDEV)
public interface KorneaTypeChecker<out T> {
    @AvailableSince(KorneaToolkit.VERSION_1_2_0_INDEV)
    public interface ClassBased<T : Any>: KorneaTypeChecker<T> {
        public companion object {
            public inline operator fun <reified T: Any> invoke(): ClassBased<T> = inline(T::class)
        }

        public val typeClass: KClass<T>

        override fun isInstance(instance: Any?): Boolean = typeClass.isInstance(instance)
        override fun asInstance(instance: Any?): T = typeClass.cast(instance)
        override fun asInstanceSafe(instance: Any?): T? = typeClass.safeCast(instance)
    }


    public fun isInstance(instance: Any?): Boolean
    public fun asInstance(instance: Any?): T
    public fun asInstanceSafe(instance: Any?): T?
}