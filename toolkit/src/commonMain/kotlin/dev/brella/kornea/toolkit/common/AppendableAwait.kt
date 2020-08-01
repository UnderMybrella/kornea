@file:Suppress("UNCHECKED_CAST")

package dev.brella.kornea.toolkit.common

import dev.brella.kornea.annotations.AvailableSince


/**
 * An object to which char sequences and values can be appended in a suspending manner
 */
@AvailableSince(KorneaToolkit.VERSION_2_4_0_ALPHA)
public interface AppendableAwait {
    /**
     * Appends the specified character [value] to this Appendable and returns this instance.
     *
     * @param value the character to append.
     */
    public suspend fun appendAwait(value: Char): AppendableAwait

    /**
     * Appends the specified character sequence [value] to this Appendable and returns this instance.
     *
     * @param value the character sequence to append. If [value] is `null`, then the four characters `"null"` are appended to this Appendable.
     */
    public suspend fun appendAwait(value: CharSequence?): AppendableAwait

    /**
     * Appends a subsequence of the specified character sequence [value] to this Appendable and returns this instance.
     *
     * @param value the character sequence from which a subsequence is appended. If [value] is `null`,
     *  then characters are appended as if [value] contained the four characters `"null"`.
     * @param startIndex the beginning (inclusive) of the subsequence to append.
     * @param endIndex the end (exclusive) of the subsequence to append.
     *
     * @throws IndexOutOfBoundsException or [IllegalArgumentException] when [startIndex] or [endIndex] is out of range of the [value] character sequence indices or when `startIndex > endIndex`.
     */
    public suspend fun appendAwait(value: CharSequence?, startIndex: Int, endIndex: Int): AppendableAwait
}


/**
 * Appends a subsequence of the specified character sequence [value] to this Appendable and returns this instance.
 *
 * @param value the character sequence from which a subsequence is appended. If [value] is `null`,
 *  then characters are appended as if [value] contained the four characters `"null"`.
 * @param startIndex the beginning (inclusive) of the subsequence to append.
 * @param endIndex the end (exclusive) of the subsequence to append.
 *
 * @throws IndexOutOfBoundsException or [IllegalArgumentException] when [startIndex] or [endIndex] is out of range of the [value] character sequence indices or when `startIndex > endIndex`.
 */
@AvailableSince(KorneaToolkit.VERSION_2_4_0_ALPHA)
public suspend fun <A : AppendableAwait> A.appendRangeAwait(value: CharSequence?, startIndex: Int, endIndex: Int): A {
    return appendAwait(value, startIndex, endIndex) as A
}

/**
 * Appends all arguments to the given [Appendable].
 */
@AvailableSince(KorneaToolkit.VERSION_2_4_0_ALPHA)
public suspend fun <A : AppendableAwait> A.appendAwait(vararg value: CharSequence?): A {
    for (item in value)
        appendAwait(item)
    return this
}

/** Appends a line feed character (`\n`) to this Appendable. */
@AvailableSince(KorneaToolkit.VERSION_2_4_0_ALPHA)
public suspend inline fun <A : AppendableAwait> A.appendLineAwait(): A = appendAwait('\n') as A

/** Appends value to the given Appendable and a line feed character (`\n`) after it. */
@AvailableSince(KorneaToolkit.VERSION_2_4_0_ALPHA)
public suspend inline fun <A : AppendableAwait> A.appendLineAwait(value: CharSequence?): A = (appendAwait(value) as A).appendLineAwait()

/** Appends value to the given Appendable and a line feed character (`\n`) after it. */
@AvailableSince(KorneaToolkit.VERSION_2_4_0_ALPHA)
public suspend inline fun <A : AppendableAwait> A.appendLineAwait(value: Char): A = (appendAwait(value) as A).appendLineAwait()


@AvailableSince(KorneaToolkit.VERSION_2_4_0_ALPHA)
public suspend fun <T, A : AppendableAwait> A.appendElementAwait(element: T, transform: ((T) -> CharSequence)?) {
    when {
        transform != null -> appendAwait(transform(element))
        element is CharSequence? -> appendAwait(element)
        element is Char -> appendAwait(element)
        else -> appendAwait(element.toString())
    }
}
