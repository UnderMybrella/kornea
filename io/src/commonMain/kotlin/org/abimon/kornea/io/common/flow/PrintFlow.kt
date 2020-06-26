package org.abimon.kornea.io.common.flow

import org.abimon.kornea.annotations.AvailableSince
import org.abimon.kornea.io.common.AppendableAwait
import org.abimon.kornea.io.common.KorneaIO

@AvailableSince(KorneaIO.VERSION_4_2_0)
public interface PrintFlow : AppendableAwait {
    override suspend fun appendAwait(value: Char): PrintFlow = print(value)
    override suspend fun appendAwait(value: CharSequence?): PrintFlow = print(value)
    override suspend fun appendAwait(value: CharSequence?, startIndex: Int, endIndex: Int): PrintFlow = print(value, startIndex, endIndex)

    /**
     * Prints the specified character [value] to this PrintFlow and returns this instance.
     *
     * @param value the character to append.
     */
    @AvailableSince(KorneaIO.VERSION_4_2_0)
    public suspend fun print(value: Char): PrintFlow

    /**
     * Appends the specified character sequence [value] to this PrintFlow and returns this instance.
     *
     * @param value the character sequence to append. If [value] is `null`, then the four characters `"null"` are appended to this PrintFlow.
     */
    @AvailableSince(KorneaIO.VERSION_4_2_0)
    public suspend fun print(value: CharSequence?): PrintFlow

    /**
     * Appends a subsequence of the specified character sequence [value] to this PrintFlow and returns this instance.
     *
     * @param value the character sequence from which a subsequence is appended. If [value] is `null`,
     *  then characters are appended as if [value] contained the four characters `"null"`.
     * @param startIndex the beginning (inclusive) of the subsequence to append.
     * @param endIndex the end (exclusive) of the subsequence to append.
     *
     * @throws IndexOutOfBoundsException or [IllegalArgumentException] when [startIndex] or [endIndex] is out of range of the [value] character sequence indices or when `startIndex > endIndex`.
     */
    @AvailableSince(KorneaIO.VERSION_4_2_0)
    public suspend fun print(value: CharSequence?, startIndex: Int, endIndex: Int): PrintFlow
}

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_4_2_0)
public interface PrintOutputFlow: PrintFlow, OutputFlow {
    override suspend fun print(value: Char): PrintOutputFlow {
        write(value.toInt())
        return this
    }

    @ExperimentalStdlibApi
    override suspend fun print(value: CharSequence?): PrintOutputFlow {
        write(value.toString().encodeToByteArray())
        return this
    }

    @ExperimentalStdlibApi
    override suspend fun print(value: CharSequence?, startIndex: Int, endIndex: Int): PrintOutputFlow {
        write((value?.subSequence(startIndex, endIndex).toString()).encodeToByteArray())
        return this
    }
}