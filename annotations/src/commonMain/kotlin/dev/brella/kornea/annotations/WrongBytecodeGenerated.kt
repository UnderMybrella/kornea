package dev.brella.kornea.annotations

@Target(
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@MustBeDocumented
public annotation class WrongBytecodeGenerated(
    val bytecodeError: String,
    val replaceWith: ReplaceWith = ReplaceWith(""),
    val level: BytecodeWarningLevel = BytecodeWarningLevel.WARNING
) {
    public companion object {
        public const val STACK_SHOULD_BE_SPILLED: String = "Stack should be spilled before suspension call"
    }
}


/**
 * Possible levels of warning for bytecode. The level specifies how usages are reported in code.
 *
 * @see WrongBytecodeGenerated
 */
public enum class BytecodeWarningLevel {
    /** Usage of the element will be reported as a warning. */
    WARNING,

    /** Usage of the element will be reported as an error. */
    ERROR,

    /** Element will not be accessible from code. */
    HIDDEN
}