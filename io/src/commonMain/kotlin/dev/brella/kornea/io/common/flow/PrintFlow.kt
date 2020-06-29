@file:Suppress("UNCHECKED_CAST")

package dev.brella.kornea.io.common.flow

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.io.common.AppendableAwait
import dev.brella.kornea.io.common.KorneaIO

@AvailableSince(KorneaIO.VERSION_4_2_0_INDEV)
public interface PrintFlow : AppendableAwait {
    override suspend fun appendAwait(value: Char): PrintFlow = print(value)
    override suspend fun appendAwait(value: CharSequence?): PrintFlow = print(value)
    override suspend fun appendAwait(value: CharSequence?, startIndex: Int, endIndex: Int): PrintFlow = print(value, startIndex, endIndex)

    /**
     * Prints the specified character [value] to this PrintFlow and returns this instance.
     *
     * @param value the character to append.
     */
    @AvailableSince(KorneaIO.VERSION_4_2_0_INDEV)
    public suspend fun print(value: Char): PrintFlow

    /**
     * Appends the specified character sequence [value] to this PrintFlow and returns this instance.
     *
     * @param value the character sequence to append. If [value] is `null`, then the four characters `"null"` are appended to this PrintFlow.
     */
    @AvailableSince(KorneaIO.VERSION_4_2_0_INDEV)
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
    @AvailableSince(KorneaIO.VERSION_4_2_0_INDEV)
    public suspend fun print(value: CharSequence?, startIndex: Int, endIndex: Int): PrintFlow = print(value?.subSequence(startIndex, endIndex))
}

@ExperimentalUnsignedTypes
@AvailableSince(KorneaIO.VERSION_4_2_0_INDEV)
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


/**
 * Prints a subsequence of the specified character sequence [value] to this [PrintFlow] and returns this instance.
 *
 * @param value the character sequence from which a subsequence is appended. If [value] is `null`,
 *  then characters are appended as if [value] contained the four characters `"null"`.
 * @param startIndex the beginning (inclusive) of the subsequence to append.
 * @param endIndex the end (exclusive) of the subsequence to append.
 *
 * @throws IndexOutOfBoundsException or [IllegalArgumentException] when [startIndex] or [endIndex] is out of range of the [value] character sequence indices or when `startIndex > endIndex`.
 */
@AvailableSince(KorneaIO.VERSION_1_0_0_ALPHA)
public suspend fun <A : PrintFlow> A.printRange(value: CharSequence?, startIndex: Int, endIndex: Int): A {
    return print(value, startIndex, endIndex) as A
}

/**
 * Prints all arguments to the given [PrintFlow].
 */
@AvailableSince(KorneaIO.VERSION_1_0_0_ALPHA)
public suspend fun <A : PrintFlow> A.print(vararg value: CharSequence?): A {
    for (item in value)
        print(item)
    return this
}

/** Prints a line feed character (`\n`) to this [PrintFlow]. */
@AvailableSince(KorneaIO.VERSION_1_0_0_ALPHA)
public suspend inline fun <A : PrintFlow> A.printLine(): A = print('\n') as A

/** Prints value to the given [PrintFlow] and a line feed character (`\n`) after it. */
@AvailableSince(KorneaIO.VERSION_1_0_0_ALPHA)
public suspend inline fun <A : PrintFlow> A.printLine(value: CharSequence?): A = (print(value) as A).printLine()

/** Prints value to the given [PrintFlow] and a line feed character (`\n`) after it. */
@AvailableSince(KorneaIO.VERSION_1_0_0_ALPHA)
public suspend inline fun <A : PrintFlow> A.printLine(value: Char): A = (print(value) as A).printLine()


@AvailableSince(KorneaIO.VERSION_1_0_0_ALPHA)
public suspend fun <T, A : PrintFlow> A.printElement(element: T, transform: ((T) -> CharSequence)?) {
    when {
        transform != null -> print(transform(element))
        element is CharSequence? -> print(element)
        element is Char -> print(element)
        else -> print(element.toString())
    }
}
