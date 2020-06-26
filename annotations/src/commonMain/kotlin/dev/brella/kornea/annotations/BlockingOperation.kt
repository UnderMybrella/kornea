package dev.brella.kornea.annotations

/**
 * This annotation is used to denote that the target may block a thread, and should only be used in a blocking context.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
public annotation class BlockingOperation