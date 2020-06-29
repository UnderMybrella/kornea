package dev.brella.kornea.io.common

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.errors.common.doOnSuccessAsync
import dev.brella.kornea.io.common.DataSource.Companion.korneaSourceClosed
import dev.brella.kornea.io.common.DataSource.Companion.korneaSourceUnknown
import dev.brella.kornea.io.common.DataSource.Companion.korneaTooManySourcesOpen
import dev.brella.kornea.io.common.flow.InputFlow
import dev.brella.kornea.toolkit.common.KorneaTypeChecker
import dev.brella.kornea.toolkit.common.inline
import kotlin.reflect.KClass

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_4_1_0_INDEV)
public abstract class LimitedInstanceDataSource<I : InputFlow, S : Any>(public val opener: LimitedInstanceOpenerHolder<I, S>) :
    BaseDataCloseable(), DataSource<I>, KorneaTypeChecker<I> {

    public abstract class Typed<I : InputFlow, S : Any>(
        private val typeChecker: KorneaTypeChecker<I>,
        opener: LimitedInstanceOpenerHolder<I, S>
    ) : LimitedInstanceDataSource<I, S>(opener), KorneaTypeChecker<I> by typeChecker {
        public companion object {
            public inline fun <reified I : InputFlow> withType(): KorneaTypeChecker<I> =
                KorneaTypeChecker.ClassBased.inline(I::class)

            public inline fun <reified I : InputFlow, reified S : Any> withLimitedOpener(noinline openLimitedInputFlow: OPEN_LIMITED_INPUT_FLOW<S, I>): Pair<KorneaTypeChecker<I>, LimitedInstanceOpenerHolder<I, S>> =
                Pair(
                    KorneaTypeChecker.ClassBased.inline(I::class),
                    LimitedInstanceOpenerHolder.openLimitedInputFlow(openLimitedInputFlow, S::class)
                )

            public inline fun <reified I : InputFlow, reified S : Any> withBareOpener(noinline openBareInputFlow: OPEN_BARE_INPUT_FLOW<S, I>): Pair<KorneaTypeChecker<I>, LimitedInstanceOpenerHolder<I, S>> =
                Pair(
                    KorneaTypeChecker.ClassBased.inline(I::class),
                    LimitedInstanceOpenerHolder.openBareInputFlow(openBareInputFlow, S::class)
                )

            public inline fun <reified LI : LimitedFlowOpener<I, S>, reified S : Any, reified I : InputFlow> LI.withLimitedOpener(): Pair<KorneaTypeChecker<I>, LimitedInstanceOpenerHolder<I, S>> =
                Pair(KorneaTypeChecker.ClassBased.inline(I::class), LimitedInstanceOpenerHolder.openLimitedInputFlow(this, S::class))

            public inline fun <reified LI : BareFlowOpener<I, S>, reified S : Any, reified I : InputFlow> LI.withBareOpener(): Pair<KorneaTypeChecker<I>, LimitedInstanceOpenerHolder<I, S>> =
                Pair(KorneaTypeChecker.ClassBased.inline(I::class), LimitedInstanceOpenerHolder.openBareInputFlow(this, S::class))
        }

        public constructor(pair: Pair<KorneaTypeChecker<I>, LimitedInstanceOpenerHolder<I, S>>) : this(
            pair.first,
            pair.second
        )

        public constructor(
            typeClass: KClass<I>,
            opener: LimitedInstanceOpenerHolder<I, S>
        ) : this(KorneaTypeChecker.ClassBased.inline(typeClass), opener)
    }

    /**
     * The maximum number of instances this data source can have open at any one time.
     * Attempting to call [openInputFlow] or [openNamedInputFlow] when [openInstanceCount] is greater than or equal to this will result in an error.
     * If this is null, an unlimited number of instances may be opened
     */
    public abstract val maximumInstanceCount: Int?
    public val openInstanceCount: Int
        get() = if (openInstancesDelegated) openInstances.size else 0

    private var openInstancesDelegated: Boolean = false
    protected open val openInstances: MutableList<I> by lazy {
        openInstancesDelegated = true
        ArrayList(maximumInstanceCount ?: 0)
    }

    private suspend fun registerInputFlow(flow: I) {
        flow.registerCloseHandler(this::instanceClosed)
        openInstances.add(flow)
    }

    override suspend fun registerCloseHandler(handler: DataCloseableEventHandler): Boolean = mutableCloseHandlers.add(handler)

    override suspend fun openNamedInputFlow(location: String?): KorneaResult<I> =
        when {
            closed -> korneaSourceClosed()
            openInstances.size == maximumInstanceCount -> korneaTooManySourcesOpen(maximumInstanceCount)
            canOpenInputFlow() -> opener.openLimitedInputFlow(opener.asInstance(this), location)
                .doOnSuccessAsync(this::registerInputFlow)
            else -> korneaSourceUnknown()
        }

    override suspend fun canOpenInputFlow(): Boolean =
        !closed && (openInstances.size != maximumInstanceCount)

    @Suppress("RedundantSuspendModifier")
    protected suspend fun instanceClosed(closeable: ObservableDataCloseable) {
        if (isInstance(closeable)) {
            openInstances.remove(closeable)
        }
    }

    override suspend fun whenClosed() {
        super.whenClosed()

        openInstances.closeAll()
        openInstances.clear()
    }
}