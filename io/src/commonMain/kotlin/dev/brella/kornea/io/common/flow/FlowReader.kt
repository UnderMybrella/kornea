package dev.brella.kornea.io.common.flow

import dev.brella.kornea.base.common.ObservableDataCloseable
import dev.brella.kornea.base.common.use
import dev.brella.kornea.io.common.flow.extensions.readUtf8Character
import kotlin.properties.Delegates

public open class FlowReader(protected val backing: InputFlow) : ObservableDataCloseable by backing {
    private val cb: CharArray = CharArray(8192)
    private var nChars: Int = 0
    private var nextChar: Int = 0
    private var skipLF = false

    private suspend fun fill() {
        val dest = 0

        var n = 0

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

        var foundLF = false

        for (i in offset until offset + length) {
            if (foundLF) {
                cbuf[i] = readUtf8Character() ?: return if (i == offset) null else i - offset
            } else {
                cbuf[i] = readUtf8Character() ?: return if (i == offset) null else i - offset
                foundLF = cbuf[i] == '\n'
            }
        }

        return length
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
                return if (s.isNotEmpty())
                    s.toString()
                else
                    null
            }

            var eol = false
            var c by Delegates.notNull<Char>()

            if (omitLF && (cb[nextChar] == '\n'))
                nextChar++

            skipLF = false
            omitLF = false

            var i: Int = nextChar
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
                s.append(cb.concatToString(startChar, startChar + (i - startChar)))

                val str: String = s.toString()

                nextChar++

                if (c == '\r') {
                    skipLF = true
                }

                return str
            }

            s.append(cb.concatToString(startChar, startChar + (i - startChar)))
        }
    }
}

//Removed noinline from operation. If the compiler starts crashing, add it back
public suspend inline fun FlowReader.useEachLine(operation: (String) -> Unit): Unit = use { reader ->
    while (!isClosed) {
        operation(reader.readLine() ?: break)
    }
}