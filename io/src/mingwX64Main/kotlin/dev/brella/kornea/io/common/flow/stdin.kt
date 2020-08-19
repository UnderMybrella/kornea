package dev.brella.kornea.io.common.flow

import kotlinx.cinterop.get
import kotlinx.cinterop.getRawValue
import kotlinx.cinterop.refTo
import kotlinx.cinterop.toCValues
import platform.posix.STDIN_FILENO
import platform.posix.read
import platform.posix.stdin

public actual fun readFromStdin(buffer: ByteArray): Int =
    read(STDIN_FILENO, buffer.refTo(0), buffer.size.toUInt())