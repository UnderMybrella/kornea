package dev.brella.kornea.annotations

@Target(AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD, AnnotationTarget.LOCAL_VARIABLE, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@RequiresOptIn(level = RequiresOptIn.Level.ERROR)
@AvailableSince(KorneaAnnotations.VERSION_1_3_0_ALPHA)
public annotation class DangerousApiUsage(val message: String = "")
