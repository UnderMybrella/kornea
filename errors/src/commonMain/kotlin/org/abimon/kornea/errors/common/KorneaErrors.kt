package org.abimon.kornea.errors.common

public object KorneaErrors {
    /**
     * kornea-errors 3.2.0
     * - Add [switchIfHasCause] and [doWithCause]
     * - Change [switchIfEmpty] and [doOnEmpty] to pass the empty result in
     * - Add [StaticSuccess] and [success]
     * - Add [KorneaResult.Empty.Null] and [KorneaResult.Empty.Undefined]
     */
    public const val VERSION_3_2_0: String = "3.2.0"

    /**
     * kornea-errors 3.1.1
     * - Add [doOnSuccessAsync] overloads
     */
    public const val VERSION_3_1_1: String = "3.1.1"

    /**
     * kornea-errors 3.1.0
     *
     * - Add [mapCausedBy] and [mapRootCausedBy]
     */
    public const val VERSION_3_1_0: String = "3.1.0"

    /**
     * kornea-errors 3.0.3
     *
     * - Mark [doOnFailure] as deprecated, since IntelliJ's custom inspections for Kotlin are borked still
     */
    public const val VERSION_3_0_3: String = "3.0.3"

    /**
     * kornea-errors 3.0.2
     *
     * - Change functionality of [doOnFailure] to better reflect the other doX functions
     * - Add [getOrBreak] to reflect the same behaviour
     * - Add [KorneaErrors]
     */
    public const val VERSION_3_0_2: String = "3.0.2"
}