@file:OptIn(ExperimentalComposeUiApi::class)

package com.gigamole.composelevitation.levitation

import android.view.MotionEvent
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onPlaced
import com.gigamole.composelevitation.common.LevitationState
import com.gigamole.composelevitation.common.rememberLevitationState
import com.gigamole.composelevitation.press.PressConfig
import com.gigamole.composelevitation.shadow.ShadowConfig
import com.gigamole.composelevitation.shadow.ShadowType
import com.gigamole.composeshadowsplus.rsblur.rsBlurShadow
import com.gigamole.composeshadowsplus.softlayer.softLayerShadow

/**
 * A levitation effect [Composable].
 *
 * This container applies levitation effect to its [content] based on the provided [state]. The parent container consists of two pieces: an optional shadow content and
 * the levitation [content]. Basically, the optional shadow content controlled by the [ShadowConfig]. Yet, levitation [content] is controlled by whole [state] and does
 * complex inter-manipulation between [PressConfig] and [LevitationConfig].
 *
 * @param modifier The parent container [Modifier].
 * @param state The [LevitationState].
 * @param isEnabled Indicates whether the levitation effect is enabled or not.
 * @param content The content with levitation effect.
 * @see Modifier.levitation
 * @see LevitationConfig
 * @see PressConfig
 * @see ShadowConfig
 * @author GIGAMOLE
 */
@Composable
fun LevitationContainer(
    modifier: Modifier = Modifier,
    state: LevitationState = rememberLevitationState(),
    isEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    // Handle Composable based values in state.
    with(state) {
        ConvertShadowOffsets()
        AnimatePressFraction()
        AnimateLevitationOffset()
    }

    // Parent container. IntrinsicSize.Min is required to limit max contents size.
    Box(
        modifier = modifier
            .height(intrinsicSize = IntrinsicSize.Min)
            .width(intrinsicSize = IntrinsicSize.Min),
        contentAlignment = Alignment.Center
    ) {
        // Optional shadow content.
        state.shadowConfig?.let {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (isEnabled) {
                                Modifier.levitation(
                                    state = state,
                                    isShadow = true
                                )
                            } else {
                                Modifier
                            }
                        )
                        .then(
                            when (it.type) {
                                is ShadowType.Elevation -> {
                                    with(it.type) {
                                        Modifier.shadow(
                                            elevation = it.radius,
                                            shape = state.levitationConfig.shape,
                                            clip = isClipped,
                                            ambientColor = ambientColor,
                                            spotColor = spotColor,
                                        )
                                    }
                                }
                                is ShadowType.ShadowsPlus.SoftLayer -> {
                                    Modifier.softLayerShadow(
                                        radius = it.radius,
                                        shape = state.levitationConfig.shape,
                                        color = it.type.color,
                                        spread = it.type.spread,
                                        offset = it.type.offset,
                                    )
                                }
                                is ShadowType.ShadowsPlus.RSBlur -> {
                                    Modifier.rsBlurShadow(
                                        radius = it.radius,
                                        shape = state.levitationConfig.shape,
                                        color = it.type.color,
                                        spread = it.type.spread,
                                        offset = it.type.offset,
                                    )
                                }
                            }
                        )
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .onPlaced {
                    state.intSizeState = it.size
                }
                .then(
                    if (isEnabled) {
                        Modifier
                            .then(
                                // A little bit odd, but required to handle the gesture clipping properly.
                                if (state.levitationConfig.isClipped) {
                                    Modifier
                                        .levitation(
                                            state = state,
                                            isShadow = false
                                        )
                                        .clip(shape = state.levitationConfig.shape)
                                        .levitationPointer(state = state)
                                } else {
                                    Modifier
                                        .levitationPointer(state = state)
                                        .levitation(
                                            state = state,
                                            isShadow = false
                                        )
                                }
                            )
                    } else {
                        Modifier
                    }
                )
        ) {
            content()
        }
    }
}

/**
 * A [Modifier] to apply levitation effect.
 *
 * Basically, [LevitationState] built around [GraphicsLayerScope] to match its values.
 *
 * @param state The [LevitationState].
 * @param isShadow Indicates whether levitation effect applied to shadow or not.
 * @return The modified [Modifier] with applied levitation effect.
 * @author GIGAMOLE
 */
fun Modifier.levitation(
    state: LevitationState,
    isShadow: Boolean
): Modifier {
    return then(
        Modifier.graphicsLayer {
            with(state) {
                val scale = if (isShadow) shadowScale else scale

                // Apply levitation downscale.
                scaleX = scale
                scaleY = scale

                // Apply levitation pivot point.
                transformOrigin = if (levitationConfig.isBounded) {
                    TransformOrigin(
                        pivotFractionX = 1.0F - offsetProgress.x,
                        pivotFractionY = 1.0F - offsetProgress.y
                    )
                } else {
                    TransformOrigin(
                        pivotFractionX = levitationConfig.pivot.x,
                        pivotFractionY = levitationConfig.pivot.y
                    )
                }

                if (isShadow) {
                    // Apply levitation shadow translation offset.
                    translationX = shadowTranslation.x
                    translationY = shadowTranslation.y

                    // Apply levitation shadow degree rotation.
                    rotationX = shadowRotation.x
                    rotationY = shadowRotation.y

                    // Apply levitation shadow camera distance.
                    cameraDistance = shadowCameraDistance
                } else {
                    // Apply levitation content degree rotation.
                    rotationX = rotation.x
                    rotationY = rotation.y

                    // Apply levitation content camera distance.
                    cameraDistance = levitationConfig.cameraDistance
                }
            }
        }
    )
}

/**
 * A [Modifier] to handle levitation gesture events: press, release, and drag.
 *
 * @param state The [LevitationState].
 * @return The modified [Modifier] with levitation gestures handling.
 * @author GIGAMOLE
 */
private fun Modifier.levitationPointer(
    state: LevitationState
): Modifier = this.then(
    with(state) {
        Modifier
            .pointerInteropFilter(
                onTouchEvent = {
                    // Intercept down events, specifically for clickable content.
                    if (it.action == MotionEvent.ACTION_DOWN) {
                        rawPressOffsetState = Offset(
                            x = it.x,
                            y = it.y
                        )
                        isPressedState = true

                        cancelPendingAwaitPressedState()
                    }

                    false
                }
            )
            .pointerInput(this) {
                awaitEachGesture {
                    // Handle release events, basically single clicks/taps.
                    if (isPressedState) {
                        if (currentEvent.type == PointerEventType.Release) {
                            if (pressConfig.isAwaitPressAnimation) {
                                handleAwaitPressedState()
                            } else {
                                isPressedState = false
                            }
                        }
                    }

                    waitForUpOrCancellation()
                }
            }
            .pointerInput(this) {
                detectTapGestures(
                    // Handle tap events if upstream didn't.
                    onPress = {
                        rawPressOffsetState = it
                        isPressedState = true

                        awaitRelease()

                        if (pressConfig.isAwaitPressAnimation) {
                            isAwaitPressedState = true
                        } else {
                            isPressedState = false
                        }
                    }
                )
            }
            .pointerInput(this) {
                // Handle drag events.
                detectDragGestures(
                    onDragStart = {
                        isPressedState = true
                        isDraggedState = true

                        cancelPendingAwaitPressedState()
                    },
                    onDragEnd = {
                        rawPressOffsetState = rawDragOffsetState
                        isPressedState = false
                        isDraggedState = false
                    },
                    onDragCancel = {
                        rawPressOffsetState = rawDragOffsetState
                        isPressedState = false
                        isDraggedState = false
                    },
                    onDrag = { change, _ ->
                        handleDrag(change = change)
                    }
                )
            }
    }
)