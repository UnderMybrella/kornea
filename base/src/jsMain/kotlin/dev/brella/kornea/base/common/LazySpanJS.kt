package dev.brella.kornea.base.common

public actual fun <T> lazySpan(size: Int, initializer: (Int) -> T): LazySpan<T> =
    UnsafeLazySpanImpl(size, initializer)

public actual fun <T> lazySpan(size: Int, mode: LazyThreadSafetyMode, initializer: (Int) -> T): LazySpan<T> =
    UnsafeLazySpanImpl(size, initializer)