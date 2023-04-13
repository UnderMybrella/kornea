package dev.brella.kornea.annotations

/**
 * Provides an example value to demonstrate an expected value for library users.
 */
@Target(
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@AvailableSince(KorneaAnnotations.VERSION_1_4_0_ALPHA)
public annotation class ExampleValue(val value: String, val notes: String = "")


/**
 * Provides an example integer value to demonstrate an expected value for library users.
 */
@Target(
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@AvailableSince(KorneaAnnotations.VERSION_1_4_0_ALPHA)
public annotation class ExampleIntegerValue(val value: Long, val notes: String = "")

/**
 * Provides an example decimal value to demonstrate an expected value for library users.
 */
@Target(
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@AvailableSince(KorneaAnnotations.VERSION_1_4_0_ALPHA)
public annotation class ExampleDecimalValue(val value: Double, val notes: String = "")