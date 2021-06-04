@file:JvmName("TextJvm")

package dev.brella.kornea.io.common

import java.nio.charset.Charset

public val TextCharsets.java: Charset
    get() = when (this) {
        TextCharsets.ASCII -> Charsets.US_ASCII
        TextCharsets.UTF_8 -> Charsets.UTF_8
        TextCharsets.UTF_16 -> Charsets.UTF_16
        TextCharsets.UTF_16BE -> Charsets.UTF_16BE
        TextCharsets.UTF_16LE -> Charsets.UTF_16LE
    }

public actual fun ByteArray.decodeToString(charset: TextCharsets): String =
    String(this, charset.java)

public actual fun String.encodeToByteArray(charset: TextCharsets): ByteArray =
    toByteArray(charset.java)