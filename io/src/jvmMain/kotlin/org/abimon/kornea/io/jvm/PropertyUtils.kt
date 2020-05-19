package org.abimon.kornea.io.jvm

import kotlin.reflect.KProperty

@Deprecated("This will be natively supported in 1.4-M2")
operator fun <T> KProperty<T>.getValue(thisRef: Any, property: KProperty<*>): T = this.call()