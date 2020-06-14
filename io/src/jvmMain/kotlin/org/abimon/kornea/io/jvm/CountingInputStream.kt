package org.abimon.kornea.io.jvm

import java.io.InputStream

/**
 * Simple little wrapper that just does a count every time a byte is read
 */
public open class CountingInputStream(countedInputStream: InputStream) : DelegatedInputStream(countedInputStream) {
    private var _count = 0L
    private var _mark = 0L

    public val count: Long by ::_count
    public val mark: Long by ::_mark

    public open val streamOffset: Long by run {
        if (countedInputStream is CountingInputStream) {
            return@run (countedInputStream as CountingInputStream)::streamOffset
        }

        return@run ::_count
    }

    override fun read(): Int {
        _count++
        return super.read()
    }

    override fun read(b: ByteArray): Int {
        val read = super.read(b)
        _count += read.coerceAtLeast(0)
        return read
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        val read = super.read(b, off, len)
        _count += read.coerceAtLeast(0)
        return read
    }

    override fun skip(n: Long): Long {
        val amount = super.skip(n)
        _count += amount
        return amount
    }

    override fun reset() {
        super.reset()
        _count = _mark
    }

    override fun mark(readlimit: Int) {
        super.mark(readlimit)
        _mark = _count
    }

    public fun seekForward(n: Long): Long {
        return if (super.delegatedInputStream is CountingInputStream)
            (super.delegatedInputStream as CountingInputStream).seekForward(n)
        else
            super.delegatedInputStream.skip(n)
    }
}