package dev.brella.kornea.toolkit.common

public inline fun StringBuilder.clearToString(): String =
    try { toString() } finally { clear() }