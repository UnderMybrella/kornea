
import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.errors.common.consumeInner
import kotlin.random.Random

public suspend fun main(args: Array<String>) {
    print("Waiting...")
    readLine()
    println("oh, hi!")

    while (true) {
        val result = if (Random.nextInt(4) == 0) KorneaResult.success(Random.nextLong().toString(16), false)
        else KorneaResult.empty()


        result.consumeInner { str -> println(str.reversed()) }

        Thread.sleep(1)
    }
}