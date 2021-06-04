package dev.brella.kornea.io.common

public actual fun ByteArray.decodeToString(charset: TextCharsets): String = manuallyDecode(this, charset)
public actual fun String.encodeToByteArray(charset: TextCharsets): ByteArray = manuallyEncode(this, charset)