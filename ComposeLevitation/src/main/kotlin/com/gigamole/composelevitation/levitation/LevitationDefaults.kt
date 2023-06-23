package com.gigamole.composelevitation.levitation

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.DefaultCameraDistance
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.RectangleShape
import com.gigamole.composelevitation.shadow.ShadowConfig

/**
 * Default values for [LevitationConfig].
 *
 * @author GIGAMOLE
 */
object LevitationDefaults {

    /** The default [LevitationOrientation]. */
    val Orientation = LevitationOrientation.All

    /**
     * The default levitation degree. Can be positive or negative.
     *
     * @see GraphicsLayerScope
     */
    const val Degree = 4.0F

    /**
     * The default levitation pivot point. Range from 0.0F to 1.0F.
     *
     * Set to the center point with an offset of (0.5F, 0.5F).
     *
     * @see GraphicsLayerScope
     */
    val Pivot = Offset(
        x = 0.5F,
        y = 0.5F
    )

    /**
     * The default levitation camera distance. Range from 0.0F.
     *
     * @see GraphicsLayerScope
     */
    const val CameraDistance = DefaultCameraDistance

    /** Indicates whether levitation is bounded to its size or not. */
    const val IsBounded = false

    /**
     * The default shape of the levitation content and its shadow.
     *
     * @see LevitationContainer
     * @see ShadowConfig
     */
    val Shape = RectangleShape

    /**
     * Indicates whether levitation gesture events are clipped (and the content) within the [Shape] or not.
     *
     * @see LevitationContainer
     */
    const val IsClipped = true
}