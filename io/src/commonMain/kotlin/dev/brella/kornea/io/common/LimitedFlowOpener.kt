package dev.brella.kornea.io.common

import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.io.common.flow.InputFlow
import dev.brella.kornea.toolkit.common.KorneaTypeChecker
import dev.brella.kornea.toolkit.common.inline
import kotlin.reflect.KClass

internal typealias OPEN_LIMITED_INPUT_FLOW<S, I> = suspend S.(location: String?) -> KorneaResult<I>
internal typealias OPEN_BARE_INPUT_FLOW<S, I> = suspend S.(location: String?) -> I

public interface LimitedFlowOpener<I : InputFlow, S> {
    public suspend fun S.openLimitedInputFlow(location: String?): KorneaResult<I>
}

public interface BareFlowOpener<I : InputFlow, S> {
    public suspend fun S.openBareInputFlow(location: String?): I
}

public sealed class LimitedInstanceOpenerHolder<I : InputFlow, S : Any> private constructor(
    typeClass: KClass<S>
) : KorneaTypeChecker<S> by KorneaTypeChecker.ClassBased.inline(typeClass) {

    private class LimitedLambdaHolder<I : InputFlow, S : Any>(
        val openLimitedInputFlow: OPEN_LIMITED_INPUT_FLOW<S, I>,
        typeClass: KClass<S>
    ) : LimitedInstanceOpenerHolder<I, S>(typeClass) {
        /**
         * Opens an input flow after all values have been processed (source is not closed, there is available space, and we can open it).
         * The resulting input flow will be registered to open instances if successful, and will have a close handler added.
         */
        public override suspend fun openLimitedInputFlow(self: S, location: String?): KorneaResult<I> =
            openLimitedInputFlow.invoke(self, location)
    }

    private class BareLambdaHolder<I : InputFlow, S : Any>(
        val openBareInputFlow: OPEN_BARE_INPUT_FLOW<S, I>,
        typeClass: KClass<S>
    ) : LimitedInstanceOpenerHolder<I, S>(typeClass) {
        /**
         * Opens an input flow after all values have been processed (source is not closed, there is available space, and we can open it).
         * The resulting input flow will be wrapped with a default call to [KorneaResult.success], registered to open instances, and will have a close handler added.
         */
        public override suspend fun openBareInputFlow(self: S, location: String?): I =
            openBareInputFlow.invoke(self, location)
    }

    private class LimitedDelegateHolder<I : InputFlow, S : Any>(
        val delegate: LimitedFlowOpener<I, S>,
        typeClass: KClass<S>
    ) : LimitedInstanceOpenerHolder<I, S>(typeClass) {
        override suspend fun openLimitedInputFlow(self: S, location: String?): KorneaResult<I> =
            delegate.openLimitedInputFlow(self, location)
    }

    private class BareDelegateHolder<I : InputFlow, S : Any>(
        val delegate: BareFlowOpener<I, S>,
        typeClass: KClass<S>
    ) : LimitedInstanceOpenerHolder<I, S>(typeClass) {
        override suspend fun openBareInputFlow(self: S, location: String?): I =
            delegate.openBareInputFlow(self, location)
    }

    public companion object {
        public fun <I : InputFlow, S : Any> openLimitedInputFlow(
            openLimitedInputFlow: OPEN_LIMITED_INPUT_FLOW<S, I>,
            typeClass: KClass<S>
        ): LimitedInstanceOpenerHolder<I, S> = LimitedLambdaHolder(openLimitedInputFlow, typeClass)

        public fun <I : InputFlow, S : Any> openBareInputFlow(
            openBareInputFlow: OPEN_BARE_INPUT_FLOW<S, I>,
            typeClass: KClass<S>
        ): LimitedInstanceOpenerHolder<I, S> = BareLambdaHolder(openBareInputFlow, typeClass)

        public fun <I : InputFlow, S : Any> openLimitedInputFlow(
            delegate: LimitedFlowOpener<I, S>,
            typeClass: KClass<S>
        ): LimitedInstanceOpenerHolder<I, S> = LimitedDelegateHolder(delegate, typeClass)

        public fun <I : InputFlow, S : Any> openBareInputFlow(
            delegate: BareFlowOpener<I, S>,
            typeClass: KClass<S>
        ): LimitedInstanceOpenerHolder<I, S> = BareDelegateHolder(delegate, typeClass)

        public inline fun <I : InputFlow, reified S : Any> openLimitedInputFlow(noinline openLimitedInputFlow: OPEN_LIMITED_INPUT_FLOW<S, I>)
                : LimitedInstanceOpenerHolder<I, S> =
            openLimitedInputFlow(openLimitedInputFlow, S::class)

        public inline fun <I : InputFlow, reified S : Any> openBareInputFlow(noinline openBareInputFlow: OPEN_BARE_INPUT_FLOW<S, I>)
                : LimitedInstanceOpenerHolder<I, S> =
            openBareInputFlow(openBareInputFlow, S::class)
    }

    /**
     * Opens an input flow after all values have been processed (source is not closed, there is available space, and we can open it).
     * The resulting input flow will be registered to open instances if successful, and will have a close handler added.
     */
    public open suspend fun openLimitedInputFlow(self: S, location: String?): KorneaResult<I> =
        KorneaResult.success(openBareInputFlow(self, location))

    /**
     * Opens an input flow after all values have been processed (source is not closed, there is available space, and we can open it).
     * The resulting input flow will be wrapped with a default call to [KorneaResult.success], registered to open instances, and will have a close handler added.
     */
    public open suspend fun openBareInputFlow(self: S, location: String?): I = throw AssertionError()
}


/**
 * Opens an input flow after all values have been processed (source is not closed, there is available space, and we can open it).
 * The resulting input flow will be registered to open instances if successful, and will have a close handler added.
 */
public suspend inline fun <I : InputFlow, S> LimitedFlowOpener<I, S>.openLimitedInputFlow(
    self: S,
    location: String?
): KorneaResult<I> = self.openLimitedInputFlow(location)

/**
 * Opens an input flow after all values have been processed (source is not closed, there is available space, and we can open it).
 * The resulting input flow will be wrapped with a default call to [KorneaResult.success], registered to open instances, and will have a close handler added.
 */
public suspend inline fun <I : InputFlow, S> BareFlowOpener<I, S>.openBareInputFlow(
    self: S,
    location: String?
): I = self.openBareInputFlow(location)