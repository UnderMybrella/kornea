package dev.brella.kornea.io.common.flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import dev.brella.kornea.io.common.ObservableDataCloseable
import dev.brella.kornea.io.common.readInt16BE
import dev.brella.kornea.io.common.use
import kotlin.properties.Delegates

@ExperimentalUnsignedTypes
public open class FlowReader(protected val backing: InputFlow) : ObservableDataCloseable by backing {
    private val cb: CharArray = CharArray(8192)
    private var nChars: Int = 0
    private var nextChar: Int = 0
    private var skipLF = false

    private suspend fun fill() {
        val dest: Int = 0

        var n: Int = 0

        do {
            n = backing.read(cb, dest, cb.size - dest) ?: break
        } while (n == 0)

        if (n > 0) {
            nChars = dest + n
            nextChar = dest
        }
    }

    private suspend fun InputFlow.read(cbuf: CharArray, offset: Int, length: Int): Int? {
        if (offset < 0 || offset > cbuf.size || length < 0 || (offset + length) > cbuf.size || (offset + length) < 0)
            throw IndexOutOfBoundsException()

        if (length == 0)
            return 0

        for (i in offset until offset + length) {
            cbuf[i] = readUtf8Character() ?: if (i == offset) return null else return i - offset
        }

        return length
    }

    private suspend fun InputFlow.readUtf8Character(): Char? {
        val a = read() ?: return null

        when {
            a and 0xF0 == 0xF0 -> {
                val b = read() ?: return null
                val c = read() ?: return null
                val d = read() ?: return null

                return (((a and 0xF) shl 18) or
                        ((b and 0x3F) shl 12) or
                        ((c and 0x3F) shl 6) or
                        ((d and 0x3F) shl 0)).toChar()
            }
            a and 0xE0 == 0xE0 -> {
                val b = read() ?: return null
                val c = read() ?: return null

                return (((a and 0xF) shl 12) or
                        ((b and 0x3F) shl 6) or
                        ((c and 0x3F) shl 0)).toChar()
            }
            a and 0xC0 == 0xC0 -> {
                val b = read() ?: return null

                return (((a and 0xF) shl 6) or
                        ((b and 0x3F) shl 0)).toChar()
            }
            a and 0x80 == 0x80 -> return null
            else -> return a.toChar()
        }
    }


    public suspend fun readLine(ignoreLF: Boolean = false): String? {
        val s = StringBuilder()
        var startChar: Int

        var omitLF = ignoreLF || skipLF

        bufferLoop@ while (true) {
            if (nextChar >= nChars) {
                fill()
            }

            if (nextChar >= nChars) { /* EOF */
                if (s.isNotEmpty()) return s.toString()
                else return null
            }

            var eol = false
            var c by Delegates.notNull<Char>()
            var i = 0

            if (omitLF && (cb[nextChar] == '\n'))
                nextChar++

            skipLF = false
            omitLF = false

            i = nextChar
            charLoop@ while (i in nextChar until nChars) {
                c = cb[i]
                if ((c == '\n') || (c == '\r')) {
                    eol = true
                    break@charLoop
                }
                i++
            }

            startChar = nextChar
            nextChar = i

            if (eol) {
                s.append(String(cb, startChar, i - startChar))

                val str: String = s.toString()

                nextChar++

                if (c == '\r') {
                    skipLF = true
                }

                return str
            }

            s.append(String(cb, startChar, i - startChar))
        }
    }
}

@ExperimentalUnsignedTypes
@ExperimentalCoroutinesApi
//Removed noinline from operation. If the compiler starts crashing, add it back
public suspend inline fun <T> FlowReader.useLines(scope: CoroutineScope, operation: (ReceiveChannel<String>) -> T): T = use { reader ->
    operation(scope.produce {
        while (isActive) {
            send(reader.readLine() ?: break)
        }

        close()
    })
}

@ExperimentalUnsignedTypes
@ExperimentalCoroutinesApi
//Removed noinline from operation. If the compiler starts crashing, add it back
public suspend inline fun FlowReader.useEachLine(operation: (String) -> Unit): Unit = use { reader ->
    while (!isClosed) { operation(reader.readLine() ?: break) }
}