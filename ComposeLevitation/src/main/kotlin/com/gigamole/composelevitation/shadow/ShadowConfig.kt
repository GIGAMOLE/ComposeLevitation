package com.gigamole.composelevitation.shadow

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import com.gigamole.composelevitation.common.LevitationState
import com.gigamole.composelevitation.levitation.LevitationConfig
import com.gigamole.composelevitation.press.PressConfig

/**
 * An optional levitation shadow configuration for [LevitationState].
 *
 * The shadow shape set automatically to the [LevitationConfig.shape].
 *
 * @property degreeMultiplier The levitation shadow degree multiplier. Can be positive or negative.
 * @property downscaleMultiplier The levitation shadow downscale multiplier. Can be positive or negative.
 * @property cameraDistanceMultiplier The levitation shadow camera distance multiplier. Can be positive or negative.
 * @property translationOffset The levitation shadow translation offset. Can be positive or negative.
 * @property isPivotedWhenPressed Indicates whether levitation shadow is pivoted when fully pressed at pivot.
 * @property radius The levitation shadow radius.
 * @property type The levitation [ShadowType].
 * @see LevitationState
 * @see LevitationConfig
 * @see PressConfig
 * @see com.gigamole.composeshadowsplus
 * @author GIGAMOLE
 */
data class ShadowConfig(
    val degreeMultiplier: Float = ShadowDefaults.Config.DegreeMultiplier,
    val downscaleMultiplier: Float = ShadowDefaults.Config.DownscaleMultiplier,
    val cameraDistanceMultiplier: Float = ShadowDefaults.Config.CameraDistanceMultiplier,
    val translationOffset: DpOffset = ShadowDefaults.Config.TranslationOffset,
    val isPivotedWhenPressed: Boolean = ShadowDefaults.Config.IsPivotedWhenPressed,
    val radius: Dp = ShadowDefaults.Config.Radius,
    val type: ShadowType = ShadowDefaults.Config.Type
)

