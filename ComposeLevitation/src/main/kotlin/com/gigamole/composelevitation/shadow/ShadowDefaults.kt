package com.gigamole.composelevitation.shadow

import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.gigamole.composelevitation.levitation.LevitationConfig
import com.gigamole.composelevitation.press.PressConfig
import com.gigamole.composeshadowsplus.common.ShadowsPlusDefaults

/**
 * Default values for [ShadowConfig] and [ShadowType].
 *
 * @author GIGAMOLE
 */
object ShadowDefaults {

    /** Default configuration values for [ShadowConfig]. */
    object Config {

        /**
         * The default shadow degree multiplier. Can be positive or negative.
         *
         * @see LevitationConfig.degree
         */
        const val DegreeMultiplier = 1.1F

        /**
         * The default shadow downscale multiplier. Can be positive or negative.
         *
         * @see PressConfig.downscale
         */
        const val DownscaleMultiplier = 2.5F

        /**
         * The default shadow camera distance multiplier. Can be positive or negative.
         *
         * @see LevitationConfig.cameraDistance
         */
        const val CameraDistanceMultiplier = 0.9F

        /** The shadow default translation offset. Can be positive or negative. */
        val TranslationOffset: DpOffset = DpOffset(
            x = 3.dp,
            y = 6.dp
        )

        /**
         * Indicates whether shadow is pivoted when fully pressed at pivot.
         *
         * @see LevitationConfig.pivot
         */
        const val IsPivotedWhenPressed = true

        /**
         * The default shadow radius.
         *
         * @see ShadowsPlusDefaults
         */
        val Radius = ShadowsPlusDefaults.ShadowRadius

        /** The default [ShadowType]. */
        val Type = ShadowType.ShadowsPlus.RSBlur()
    }

    /** Default configuration values for [ShadowType]. */
    object Type {

        /** Default configuration values for [ShadowType.Elevation]. */
        object Elevation {

            /** Indicates whether elevation shadow is clipped or not. */
            const val IsClipped = true

            /** The default elevation shadow ambient color. */
            val AmbientColor = DefaultShadowColor

            /** The default elevation shadow spot color. */
            val SpotColor = DefaultShadowColor
        }
    }
}