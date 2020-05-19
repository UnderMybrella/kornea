package org.abimon.kornea.io.common

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.PROPERTY_GETTER)
annotation class BlockingOperation

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR)
annotation class NonBlockingOperation

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR)
annotation class BlockableContext

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.CLASS)
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
annotation class ExperimentalKorneaIO