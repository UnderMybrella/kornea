package dev.brella.kornea.io.common.flow
import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.io.common.KorneaIO
import kotlin.io.print as printStd

@AvailableSince(KorneaIO.VERSION_1_2_0_ALPHA)
public class StdoutPrintFlow: PrintFlow {
    override suspend fun print(value: Char): StdoutPrintFlow {
        printStd(value)
        return this
    }

    override suspend fun print(value: CharSequence?): PrintFlow {
        printStd(value)
        return this
    }
}