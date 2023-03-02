package dev.brella.kornea.composite.common

import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.errors.common.flatMap
import dev.brella.kornea.errors.common.map

/**
 * A [Composite] object is an object that is made up of zero or more [Constituent]s.
 */
public interface Composite {
    public interface Empty: Composite {
        override fun hasConstituent(key: Constituent.Key<*>): Boolean = false
        override fun <T : Constituent> getConstituent(key: Constituent.Key<T>): KorneaResult<T> =
            KorneaResult.empty()
    }
    public fun hasConstituent(key: Constituent.Key<*>): Boolean
    public fun <T : Constituent> getConstituent(key: Constituent.Key<T>): KorneaResult<T>
}

public inline fun <T: Constituent, R> Composite.withConstituent(key: Constituent.Key<T>, block: T.() -> R): KorneaResult<R> =
    getConstituent(key).map(block)

public inline fun <T: Constituent, R> Composite.withFlatConstituent(key: Constituent.Key<T>, block: T.() -> KorneaResult<R>): KorneaResult<R> =
    getConstituent(key).flatMap(block)