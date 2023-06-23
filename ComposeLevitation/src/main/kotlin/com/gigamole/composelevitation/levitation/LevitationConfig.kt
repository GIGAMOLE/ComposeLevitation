package com.gigamole.composelevitation.levitation

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shape
import com.gigamole.composelevitation.common.LevitationState
import com.gigamole.composelevitation.press.PressConfig
import com.gigamole.composelevitation.shadow.ShadowConfig

/**
 * A required main levitation configuration for [LevitationState].
 *
 * @property orientation The [LevitationOrientation].
 * @property degree The levitation degree. Can be positive or negative.
 * @property pivot The levitation pivot point. Range from 0.0F to 1.0F. Set to the center point with an offset of (0.5F, 0.5F).
 * @property cameraDistance The levitation camera distance. Range from 0.0F.
 * @property isBounded Indicates whether levitation is bounded to its size or not.
 * @property shape The shape of the levitation content and its shadow.
 * @property isClipped Indicates whether levitation gesture events are clipped (and the content) within the [shape] or not.
 * @see LevitationState
 * @see PressConfig
 * @see ShadowConfig
 * @author GIGAMOLE
 */
data class LevitationConfig(
    val orientation: LevitationOrientation = LevitationDefaults.Orientation,
    val degree: Float = LevitationDefaults.Degree,
    val pivot: Offset = LevitationDefaults.Pivot,
    val cameraDistance: Float = LevitationDefaults.CameraDistance,
    val isBounded: Boolean = LevitationDefaults.IsBounded,
    val shape: Shape = LevitationDefaults.Shape,
    val isClipped: Boolean = LevitationDefaults.IsClipped
)

