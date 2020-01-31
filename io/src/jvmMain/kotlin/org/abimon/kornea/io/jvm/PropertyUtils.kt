package org.abimon.kornea.io.jvm

import kotlin.reflect.KProperty

operator fun <T> KProperty<T>.getValue(thisRef: Any, property: KProperty<*>): T = this.call()