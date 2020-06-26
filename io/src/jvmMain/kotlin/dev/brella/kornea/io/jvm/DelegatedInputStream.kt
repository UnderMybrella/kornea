package dev.brella.kornea.io.jvm

import java.io.InputStream

public open class DelegatedInputStream(protected val delegatedInputStream: InputStream) : InputStream() {
    override fun read(): Int = delegatedInputStream.read()
    override fun read(b: ByteArray): Int = delegatedInputStream.read(b)
    override fun read(b: ByteArray, off: Int, len: Int): Int = delegatedInputStream.read(b, off, len)
    override fun available(): Int = delegatedInputStream.available()
    override fun close(): Unit = delegatedInputStream.close()
    override fun mark(readlimit: Int): Unit = delegatedInputStream.mark(readlimit)
    override fun markSupported(): Boolean = delegatedInputStream.markSupported()
    override fun reset(): Unit = delegatedInputStream.reset()
    override fun skip(n: Long): Long = delegatedInputStream.skip(n)
}