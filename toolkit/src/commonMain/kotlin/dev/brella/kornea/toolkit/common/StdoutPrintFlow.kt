package dev.brella.kornea.toolkit.common
import dev.brella.kornea.annotations.AvailableSince
import kotlin.io.print as printStd

@AvailableSince(KorneaToolkit.VERSION_2_4_0_ALPHA)
public object StdoutPrintFlow: PrintFlow {
    override suspend fun print(value: Char): StdoutPrintFlow {
        printStd(value)
        return this
    }

    override suspend fun print(value: CharSequence?): StdoutPrintFlow {
        printStd(value)
        return this
    }
}