package com.gigamole.composelevitation.press

import androidx.compose.animation.core.AnimationSpec
import com.gigamole.composelevitation.common.LevitationState
import com.gigamole.composelevitation.levitation.LevitationConfig
import com.gigamole.composelevitation.shadow.ShadowConfig

/**
 * A required levitation press configuration for [LevitationState].
 *
 * @property type The levitation [PressType].
 * @property downscale The levitation press downscale. Can be positive or negative.
 * @property pressAnimationSpec The levitation press animation specification.
 * @property isAwaitPressAnimation Indicates whether to await full press animation on release or not.
 * @property isPivotedWhenReleased Indicates whether to pivot the press point when released or not.
 * @see LevitationState
 * @see LevitationConfig
 * @see ShadowConfig
 * @author GIGAMOLE
 */
data class PressConfig(
    val type: PressType = PressDefaults.Config.Type,
    val downscale: Float = PressDefaults.Config.Downscale,
    val pressAnimationSpec: AnimationSpec<Float> = PressDefaults.Config.PressAnimationSpec,
    val isAwaitPressAnimation: Boolean = PressDefaults.Config.IsAwaitPressAnimation,
    val isPivotedWhenReleased: Boolean = PressDefaults.Config.IsPivotedWhenReleased
)