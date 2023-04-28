package dev.brella.kornea.base.jstress

import dev.brella.kornea.base.common.LazySpan
import dev.brella.kornea.base.common.lazySpan
import org.openjdk.jcstress.annotations.*
import org.openjdk.jcstress.infra.results.II_Result

@JCStressTest
@Description("Tests that LazyArray is threadsafe")
@Outcome(id = ["0, 0"], expect = Expect.ACCEPTABLE, desc = "Initialiser only run once")
@Outcome(id = ["1, 0"], expect = Expect.FORBIDDEN, desc = "actor2 ran initialiser, then actor1")
@Outcome(id = ["0, 1"], expect = Expect.FORBIDDEN, desc = "actor1 ran initialiser, then actor2")
@State
public open class LazySpanTest {
    var counter = 0
    val array: LazySpan<Int> = lazySpan(1, LazyThreadSafetyMode.SYNCHRONIZED) { counter++ }

    @Actor
    public fun actor1(r: II_Result) {
        r.r1 = array[0]
    }

    @Actor
    public fun actor2(r: II_Result) {
        r.r2 = array[0]
    }
}