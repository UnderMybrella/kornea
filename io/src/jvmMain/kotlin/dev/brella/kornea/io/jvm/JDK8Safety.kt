package dev.brella.kornea.io.jvm

import java.nio.Buffer
import java.nio.InvalidMarkException

/**
 * Sets this buffer's position.  If the mark is defined and larger than the
 * new position then it is discarded.
 *
 * This method is designed for JDK safety.
 *
 * @param  newPosition
 * The new position value; must be non-negative
 * and no larger than the current limit
 *
 * @return  This buffer
 *
 * @throws  IllegalArgumentException
 * If the preconditions on `newPosition` do not hold
 */
public inline fun <T: Buffer> T.positionSafe(newPosition: Int): T = (this as Buffer).position(newPosition) as T

/**
 * Sets this buffer's limit.  If the position is larger than the new limit
 * then it is set to the new limit.  If the mark is defined and larger than
 * the new limit then it is discarded.
 *
 * @param  newLimit
 * The new limit value; must be non-negative
 * and no larger than this buffer's capacity
 *
 * @return  This buffer
 *
 * @throws  IllegalArgumentException
 * If the preconditions on `newLimit` do not hold
 */
public inline fun <T: Buffer> T.limitSafe(newLimit: Int): T = (this as Buffer).limit(newLimit) as T

/**
 * Sets this buffer's mark at its position.
 *
 * @return  This buffer
 */
public inline fun <T: Buffer> T.markSafe(): T = (this as Buffer).mark() as T

/**
 * Resets this buffer's position to the previously-marked position.
 *
 *
 *  Invoking this method neither changes nor discards the mark's
 * value.
 *
 * @return  This buffer
 *
 * @throws  InvalidMarkException
 * If the mark has not been set
 */
public inline fun <T: Buffer> T.resetSafe(): T = (this as Buffer).reset() as T

/**
 * Clears this buffer.  The position is set to zero, the limit is set to
 * the capacity, and the mark is discarded.
 *
 *
 *  Invoke this method before using a sequence of channel-read or
 * *put* operations to fill this buffer.  For example:
 *
 * <blockquote><pre>
 * buf.clear();     // Prepare buffer for reading
 * in.read(buf);    // Read data</pre></blockquote>
 *
 *
 *  This method does not actually erase the data in the buffer, but it
 * is named as if it did because it will most often be used in situations
 * in which that might as well be the case.
 *
 * @return  This buffer
 */
public inline fun <T: Buffer> T.clearSafe(): T = (this as Buffer).clear() as T

/**
 * Flips this buffer.  The limit is set to the current position and then
 * the position is set to zero.  If the mark is defined then it is
 * discarded.
 *
 *
 *  After a sequence of channel-read or *put* operations, invoke
 * this method to prepare for a sequence of channel-write or relative
 * *get* operations.  For example:
 *
 * <blockquote><pre>
 * buf.put(magic);    // Prepend header
 * in.read(buf);      // Read data into rest of buffer
 * buf.flip();        // Flip buffer
 * out.write(buf);    // Write header + data to channel</pre></blockquote>
 *
 *
 *  This method is often used in conjunction with the [ ][java.nio.ByteBuffer.compact] method when transferring data from
 * one place to another.
 *
 * @return  This buffer
 */
public inline fun <T: Buffer> T.flipSafe(): T = (this as Buffer).flip() as T

/**
 * Rewinds this buffer.  The position is set to zero and the mark is
 * discarded.
 *
 *
 *  Invoke this method before a sequence of channel-write or *get*
 * operations, assuming that the limit has already been set
 * appropriately.  For example:
 *
 * <blockquote><pre>
 * out.write(buf);    // Write remaining data
 * buf.rewind();      // Rewind buffer
 * buf.get(array);    // Copy data into array</pre></blockquote>
 *
 * @return  This buffer
 */
public inline fun <T: Buffer> T.rewindSafe(): T = (this as Buffer).rewind() as T

public inline fun <T: Buffer, R> T.bookmark(block: T.() -> R): R {
    val pos = position()
    try {
        return block()
    } finally {
        positionSafe(pos)
    }
}