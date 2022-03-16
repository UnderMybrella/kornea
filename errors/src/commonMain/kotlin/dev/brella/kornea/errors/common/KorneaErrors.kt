package dev.brella.kornea.errors.common

import dev.brella.kornea.annotations.AvailableSince

public object KorneaErrors {
    public const val VERSION_3_0_1_ALPHA: String = "3.0.1-alpha"

    /**
     * kornea-errors 3.0.0-alpha
     * - Change KorneaResult to be a value class; failure is an interface required to be implemented by errors
     * - Remove PooledResult, consumeX methods
     */
    public const val VERSION_3_0_0_ALPHA: String = "3.0.0-alpha"

    public const val VERSION_2_1_0_ALPHA: String = "2.1.0-alpha"

    /**
     * kornea-errors 2.0.0-alpha
     * - Add kornea-config as a dependency, and overhaul KorneaResult methods to utilise that configuration
     * - Add [Optional]
     * - Add all MPP targets
     */
    public const val VERSION_2_0_0_ALPHA: String = "2.0.0-alpha"

    /**
     * kornea-errors
     * - Add [KorneaResult.Failure.Base]
     */
    @AvailableSince(VERSION_3_4_1_INDEV)
    public const val VERSION_3_4_1_INDEV: String = "3.4.1-indev"

    /**
     * kornea-errors
     * - Add [getOrElseWhenEmpty]
     * - Add class based versions of [filterToInstance]
     * - Remove reified modifier from a lot of methods, since they don't need it
     */
    @AvailableSince(VERSION_3_4_0_INDEV)
    public const val VERSION_3_4_0_INDEV: String = "3.4.0-indev"

    /**
     * kornea-errors
     * - Add [flatMapOrSelf] and [foldResults]
     */
    @AvailableSince(VERSION_3_3_0_INDEV)
    public const val VERSION_3_3_0_INDEV: String = "3.3.0-indev"

    /**
     * kornea-errors 3.2.0
     * - Add [switchIfHasCause] and [doWithCause]
     * - Change [switchIfEmpty] and [doOnEmpty] to pass the empty result in
     * - Add [StaticSuccess] and [success]
     * - Add [KorneaResult.Empty.Null] and [KorneaResult.Empty.Undefined]
     */
    @AvailableSince(VERSION_3_2_0_INDEV)
    public const val VERSION_3_2_0_INDEV: String = "3.2.0-indev"

    /**
     * kornea-errors 3.1.1
     * - Add [doOnSuccessAsync] overloads
     */
    @AvailableSince(VERSION_3_1_1_INDEV)
    public const val VERSION_3_1_1_INDEV: String = "3.1.1-indev"

    /**
     * kornea-errors 3.1.0
     *
     * - Add [mapCausedBy] and [mapRootCausedBy]
     */
    @AvailableSince(VERSION_3_1_0_INDEV)
    public const val VERSION_3_1_0_INDEV: String = "3.1.0-indev"

    /**
     * kornea-errors 3.0.3
     *
     * - Mark [doOnFailure] as deprecated, since IntelliJ's custom inspections for Kotlin are borked still
     */
    @AvailableSince(VERSION_3_0_3_INDEV)
    public const val VERSION_3_0_3_INDEV: String = "3.0.3-indev"

    /**
     * kornea-errors 3.0.2
     *
     * - Change functionality of [doOnFailure] to better reflect the other doX functions
     * - Add [getOrBreak] to reflect the same behaviour
     * - Add [KorneaErrors]
     */
    @AvailableSince(VERSION_3_0_2_INDEV)
    public const val VERSION_3_0_2_INDEV: String = "3.0.2-indev"
}