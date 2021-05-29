
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.errors.common.doOnSuccess
import dev.brella.kornea.errors.common.map
import dev.brella.kornea.errors.common.successPooled

public suspend fun main(args: Array<String>) {
    print("Waiting...")
    readLine()
    println("oh, hi!")

    while (true) {
//        val result = if (Random.nextInt(4) == 0) KorneaResult.success(Random.nextLong().toString(16), false)
//        else KorneaResult.empty()
//
//
//        result.consumeInner { str -> println(str.reversed()) }

        val withLatch = KorneaResult.successPooled("This is my password!")

        withLatch.map { 17 }

        withLatch.doOnSuccess { println(it.reversed()) }

        Thread.sleep(1)
    }
}