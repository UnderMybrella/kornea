package dev.brella.kornea.apollo

public inline fun <T: Any> Class<T>.orSupertype(takeSupertype: Boolean): Class<*> = if (takeSupertype) superclass else this