package dev.brella.kornea.toolkit.common.collections

import dev.brella.kornea.annotations.AvailableSince
import dev.brella.kornea.toolkit.common.KorneaToolkit

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public class ByteArrayBackedList(private val backing: ByteArray) : AbstractList<Byte>(), ListWithBuffer<Byte>, RandomAccess {
    override var size: Int = backing.size
    override fun isEmpty(): Boolean = backing.isEmpty()
    override fun contains(element: Byte): Boolean = backing.contains(element)
    override fun get(index: Int): Byte = backing[index]
    override fun indexOf(element: Byte): Int = backing.indexOf(element)
    override fun lastIndexOf(element: Byte): Int = backing.lastIndexOf(element)
}

@AvailableSince(KorneaToolkit.VERSION_2_3_0_ALPHA)
public inline fun ByteArray.asBufferedList(): ListWithBuffer<Byte> = ByteArrayBackedList(this)