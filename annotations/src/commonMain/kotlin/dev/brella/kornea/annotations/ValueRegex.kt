package dev.brella.kornea.annotations

/**
 * Provides a pattern that all values are expected to abide by.
 */
@Target(
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@AvailableSince(KorneaAnnotations.VERSION_1_4_0_ALPHA)
public annotation class ValueRegex(val pattern: String, val shouldEnforce: Boolean = false, val notes: String = "")