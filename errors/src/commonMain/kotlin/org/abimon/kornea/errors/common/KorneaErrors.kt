package org.abimon.kornea.errors.common

import org.abimon.kornea.annotations.AvailableSince

public object KorneaErrors {
    /**
     * kornea-errors
     * - Add [KorneaResult.Failure.Base]
     */
    @AvailableSince(VERSION_3_4_1)
    public const val VERSION_3_4_1: String = "3.4.1"

    /**
     * kornea-errors
     * - Add [getOrEmptyDefault]
     * - Add class based versions of [filterToInstance]
     * - Remove reified modifier from a lot of methods, since they don't need it
     */
    @AvailableSince(VERSION_3_4_0)
    public const val VERSION_3_4_0: String = "3.4.0"

    /**
     * kornea-errors
     * - Add [flatMapOrSelf] and [foldResults]
     */
    @AvailableSince(VERSION_3_3_0)
    public const val VERSION_3_3_0: String = "3.3.0"

    /**
     * kornea-errors 3.2.0
     * - Add [switchIfHasCause] and [doWithCause]
     * - Change [switchIfEmpty] and [doOnEmpty] to pass the empty result in
     * - Add [StaticSuccess] and [success]
     * - Add [KorneaResult.Empty.Null] and [KorneaResult.Empty.Undefined]
     */
    @AvailableSince(VERSION_3_2_0)
    public const val VERSION_3_2_0: String = "3.2.0"

    /**
     * kornea-errors 3.1.1
     * - Add [doOnSuccessAsync] overloads
     */
    @AvailableSince(VERSION_3_1_1)
    public const val VERSION_3_1_1: String = "3.1.1"

    /**
     * kornea-errors 3.1.0
     *
     * - Add [mapCausedBy] and [mapRootCausedBy]
     */
    @AvailableSince(VERSION_3_1_0)
    public const val VERSION_3_1_0: String = "3.1.0"

    /**
     * kornea-errors 3.0.3
     *
     * - Mark [doOnFailure] as deprecated, since IntelliJ's custom inspections for Kotlin are borked still
     */
    @AvailableSince(VERSION_3_0_3)
    public const val VERSION_3_0_3: String = "3.0.3"

    /**
     * kornea-errors 3.0.2
     *
     * - Change functionality of [doOnFailure] to better reflect the other doX functions
     * - Add [getOrBreak] to reflect the same behaviour
     * - Add [KorneaErrors]
     */
    @AvailableSince(VERSION_3_0_2)
    public const val VERSION_3_0_2: String = "3.0.2"
}