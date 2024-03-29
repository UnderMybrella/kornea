package dev.brella.kornea.annotations

@Target(AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD, AnnotationTarget.LOCAL_VARIABLE, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
public annotation class ExperimentalKorneaBase(val message: String = "")

@Target(AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD, AnnotationTarget.LOCAL_VARIABLE, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
@ChangedSince(KorneaAnnotations.VERSION_1_3_0_INDEV)
public annotation class ExperimentalKorneaErrors(val message: String = "")

@Target(AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD, AnnotationTarget.LOCAL_VARIABLE, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
@ChangedSince(KorneaAnnotations.VERSION_1_3_0_INDEV)
public annotation class ExperimentalKorneaImg(val message: String = "")

@Target(AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD, AnnotationTarget.LOCAL_VARIABLE, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
@ChangedSince(KorneaAnnotations.VERSION_1_3_0_INDEV)
public annotation class ExperimentalKorneaIO(val message: String = "")

@Target(AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD, AnnotationTarget.LOCAL_VARIABLE, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
@ChangedSince(KorneaAnnotations.VERSION_1_3_0_INDEV)
public annotation class ExperimentalKorneaModelling(val message: String = "")

@Target(AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD, AnnotationTarget.LOCAL_VARIABLE, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
@AvailableSince(KorneaAnnotations.VERSION_1_2_0_INDEV)
@ChangedSince(KorneaAnnotations.VERSION_1_3_0_INDEV)
public annotation class ExperimentalKorneaToolkit(val message: String = "")