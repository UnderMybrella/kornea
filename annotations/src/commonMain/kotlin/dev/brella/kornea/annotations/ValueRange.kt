package dev.brella.kornea.annotations

/**
 * Provides a range that values are expected to fall in.
 */
@Target(
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@AvailableSince(KorneaAnnotations.VERSION_1_4_0_ALPHA)
public annotation class ValueRange(val min: String, val max: String, val notes: String = "")

/**
 * Provides an integer range that values are expected to fall in.
 *
 * @param min The minimum value to be expected, inclusive.
 * @param max The maximum value to be expected, inclusive.
 * @param shouldEnforce Whether this range should be enforced.
 */
@Target(
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@AvailableSince(KorneaAnnotations.VERSION_1_4_0_ALPHA)
public annotation class IntegerValueRange(
    val min: Long,
    val max: Long,
    val shouldEnforce: Boolean = false,
    val notes: String = ""
)

/**
 * Provides a decimal range that values are expected to fall in.
 *
 * @param min The minimum value to be expected, inclusive.
 * @param max The maximum value to be expected, inclusive.
 * @param shouldEnforce Whether this range should be enforced.
 */
@Target(
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@AvailableSince(KorneaAnnotations.VERSION_1_4_0_ALPHA)
public annotation class DecimalValueRange(
    val min: Double,
    val max: Double,
    val shouldEnforce: Boolean = false,
    val notes: String = ""
)