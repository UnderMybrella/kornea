package dev.brella.kornea.annotations

@Target(AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.TYPE_PARAMETER, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD, AnnotationTarget.LOCAL_VARIABLE, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.TYPE)
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
@AvailableSince(KorneaAnnotations.VERSION_1_2_0_INDEV)
@ChangedSince(KorneaAnnotations.VERSION_1_3_0_INDEV)
public annotation class ExperimentalKorneaToolkit(val message: String = "")