package org.abimon.kornea.errors.common

public object KorneaErrors {

    /**
     * kornea-errors 3.1.0
     *
     * - Add [KorneaResult][mapCausedBy] and [KorneaResult][mapRootCausedBy]
     */
    public const val VERSION_3_1_0: String = "3.1.0"

    /**
     * kornea-errors 3.0.3
     *
     * - Mark [KorneaResult][doOnFailure] as deprecated, since IntelliJ's custom inspections for Kotlin are borked still
     */
    public const val VERSION_3_0_3: String = "3.0.3"

    /**
     * kornea-errors 3.0.2
     *
     * - Change functionality of [KorneaResult][doOnFailure] to better reflect the other doX functions
     * - Add [KorneaResult][getOrBreak] to reflect the same behaviour
     * - Add [KorneaErrors]
     */
    public const val VERSION_3_0_2: String = "3.0.2"
}