package dev.brella.kornea.annotations

@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.TYPEALIAS
)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
@AvailableSince(KorneaAnnotations.VERSION_1_2_0_INDEV)
@Repeatable
public annotation class ChangedSince(val version: String, val changelog: String = "")