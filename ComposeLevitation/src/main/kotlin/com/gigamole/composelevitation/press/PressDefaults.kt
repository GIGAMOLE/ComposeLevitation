package com.gigamole.composelevitation.press

import androidx.compose.animation.core.spring
import com.gigamole.composelevitation.levitation.LevitationConfig

/**
 * Default values for [PressConfig] and [PressType].
 *
 * @author GIGAMOLE
 */
object PressDefaults {

    /** Default configuration values for [PressConfig]. */
    object Config {

        /** The default [PressType]. */
        val Type = PressType.Ranged()

        /** The default press downscale. Can be positive or negative. */
        const val Downscale = 0.03F

        /** The default animation specification for press. */
        val PressAnimationSpec = spring<Float>()

        /** Indicates whether to await full press animation on release or not. */
        const val IsAwaitPressAnimation = false

        /**
         * Indicates whether to pivot the press point when released or not.
         *
         * @see LevitationConfig.pivot
         */
        const val IsPivotedWhenReleased = false
    }

    /** Default configuration values for [PressType] enumeration. */
    object Type {

        /** Default configuration values for [PressType.Ranged]. Range from 0.0F to 1.0F. */
        object Ranged {

            /** The default start fraction (from pivot point) for [PressType.Ranged]. */
            const val Start = 0.0F

            /** The default stop fraction (from content bounds) for [PressType.Ranged]. */
            const val Stop = 1.0F
        }
    }
}