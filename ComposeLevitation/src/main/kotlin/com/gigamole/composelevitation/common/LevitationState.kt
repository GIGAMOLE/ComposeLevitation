@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.gigamole.composelevitation.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.isOutOfBounds
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import com.gigamole.composelevitation.levitation.LevitationConfig
import com.gigamole.composelevitation.levitation.LevitationContainer
import com.gigamole.composelevitation.levitation.LevitationOrientation
import com.gigamole.composelevitation.levitation.levitation
import com.gigamole.composelevitation.press.PressConfig
import com.gigamole.composelevitation.press.PressType
import com.gigamole.composelevitation.shadow.ShadowConfig
import com.gigamole.composelevitation.shadow.ShadowType
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.max

/**
 * A utility function to create a [Composable] [LevitationState].
 *
 * @param levitationConfig The required [LevitationConfig].
 * @param pressConfig The required [PressConfig].
 * @param shadowConfig An optional [ShadowConfig].
 * @return The [Composable] prepared [LevitationState].
 * @see LevitationContainer
 * @author GIGAMOLE
 */
@Composable
fun rememberLevitationState(
    levitationConfig: LevitationConfig = LevitationConfig(),
    pressConfig: PressConfig = PressConfig(),
    shadowConfig: ShadowConfig? = ShadowConfig()
): LevitationState = remember(
    levitationConfig,
    pressConfig,
    shadowConfig
) {
    LevitationState(
        levitationConfig = levitationConfig,
        pressConfig = pressConfig,
        shadowConfig = shadowConfig
    )
}

/**
 * A centralized source of state data for [LevitationContainer].
 *
 * @param levitationConfig The required [LevitationConfig].
 * @param pressConfig The required [PressConfig].
 * @param shadowConfig An optional [ShadowConfig].
 * @see LevitationContainer
 * @see Modifier.levitation
 * @author GIGAMOLE
 */
class LevitationState(
    val levitationConfig: LevitationConfig = LevitationConfig(),
    val pressConfig: PressConfig = PressConfig(),
    val shadowConfig: ShadowConfig? = ShadowConfig()
) {

    /** Raw content size state. */
    internal var intSizeState by mutableStateOf(IntSize.Zero)

    /** Raw press offset state. Range from 0.0F to max [size]. */
    internal var rawPressOffsetState by mutableStateOf(Offset.Zero)

    /**
     * Raw drag offset state. Range from 0.0F to max [size].
     *
     * @see handleDrag
     */
    internal var rawDragOffsetState by mutableStateOf(Offset.Zero)

    /**
     * Indicates whether levitation is in pressed state or not.
     *
     * @see AnimatePressFraction
     * @see PressConfig.isPivotedWhenReleased
     */
    internal var isPressedState by mutableStateOf(false)

    /**
     * Indicates whether an ongoing press animation should awaits for full press to release after or not.
     *
     * @see AnimatePressFraction
     * @see cancelPendingAwaitPressedState
     * @see handleAwaitPressedState
     * @see PressConfig.isAwaitPressAnimation
     */
    internal var isAwaitPressedState by mutableStateOf(false)

    /**
     * Indicates whether levitation is in dragged state or not.
     *
     * @see handleDrag
     */
    internal var isDraggedState by mutableStateOf(false)

    /** Press fraction state. Range from 0.0F to 1.0F. */
    private var pressFractionState by mutableStateOf(0.0F)

    /**
     * Offset state that combines [rawPressOffsetState] and [rawDragOffsetState], depending on the current press gesture. Range from 0.0F to max [size].
     *
     * @see AnimateLevitationOffset
     */
    private var offsetState by mutableStateOf(Offset.Zero)

    /**
     * Converted from [DpOffset] to [Offset] shadow translation offset state. Can be positive or negative.
     *
     * @see ConvertShadowOffsets
     * @see shadowTranslation
     */
    private var shadowTranslationOffsetState by mutableStateOf(Offset.Zero)

    /**
     * Converted from [DpOffset] to [Offset] shadow offset state. Can be positive or negative.
     *
     * @see ConvertShadowOffsets
     * @see shadowTranslation
     * @see ShadowConfig.isPivotedWhenPressed
     */
    private var shadowOffsetState by mutableStateOf(Offset.Zero)

    /**
     * State which indicates whether the simulated press started or not.
     *
     * @see press
     * @see AnimatePressFraction
     */
    private var isSimulatedPressStartedState by mutableStateOf(false)

    /**
     * State which indicates whether the [isAwaitPressedState] and press animation finished ([pressFractionState] == 1.0F) or not.
     *
     * @see AnimatePressFraction
     * @see PressConfig.isAwaitPressAnimation
     */
    private var isAwaitPressedAnimationFinishedState by mutableStateOf(false)

    /** Pivot size based offset. Range from 0.0F to max [size]. */
    private val pivotOffset by derivedStateOf {
        Offset(
            x = size.width * levitationConfig.pivot.x,
            y = size.height * levitationConfig.pivot.y
        )
    }

    /**
     * Indicates whether the at least one drag event is outside the [intSizeState] bounds, so next drag events are skipped until release or not.
     *
     * @see handleDrag
     */
    private val isDragBlockedUntilRelease by derivedStateOf {
        isDraggedState.not()
    }

    /** Indicates whether the levitation is pressed. */
    val isPressed: Boolean
        get() = isPressedState

    /** Press fraction. Range from 0.0F to 1.0F. */
    val pressFraction: Float
        get() = pressFractionState

    /**
     * Levitation content size.
     *
     * @see intSizeState
     */
    val size by derivedStateOf {
        intSizeState.toSize()
    }

    /**
     * Levitation animated (between press, drag and release) raw offset. Range from 0.0F to max [size].
     *
     * @see rawPressOffsetState
     * @see rawDragOffsetState
     */
    val offset by derivedStateOf {
        offsetState
    }

    /**
     * Levitation offset progress. Range from 0.0F to 1.0F.
     *
     * @see offset
     * @see calculateOffsetAxisProgress
     */
    val offsetProgress by derivedStateOf {
        Offset(
            x = when (levitationConfig.orientation) {
                LevitationOrientation.All,
                LevitationOrientation.Horizontal -> {
                    calculateOffsetAxisProgress(
                        offsetAxis = offset.x,
                        defaultOffsetAxis = levitationConfig.pivot.x,
                        sizeAxis = size.width
                    )
                }
                LevitationOrientation.Vertical -> {
                    levitationConfig.pivot.x
                }
            },
            y = when (levitationConfig.orientation) {
                LevitationOrientation.All,
                LevitationOrientation.Vertical -> {
                    calculateOffsetAxisProgress(
                        offsetAxis = offset.y,
                        defaultOffsetAxis = levitationConfig.pivot.y,
                        sizeAxis = size.height
                    )
                }
                LevitationOrientation.Horizontal -> {
                    levitationConfig.pivot.y
                }
            }
        )
    }

    /**
     * Levitation degree progress. Range from -1.0F to 1.0F. The (0.0F, 0.0F) degree progress is at the pivot.
     *
     * @see offsetProgress
     * @see calculateDegreeAxisProgress
     * @see LevitationConfig.pivot
     */
    val degreeProgress by derivedStateOf {
        Offset(
            x = when (levitationConfig.orientation) {
                LevitationOrientation.All,
                LevitationOrientation.Horizontal -> {
                    calculateDegreeAxisProgress(
                        offsetProgressAxis = offsetProgress.x,
                        pivotAxis = levitationConfig.pivot.x
                    )
                }
                LevitationOrientation.Vertical -> {
                    0.0F
                }
            },
            y = when (levitationConfig.orientation) {
                LevitationOrientation.All,
                LevitationOrientation.Vertical -> {
                    calculateDegreeAxisProgress(
                        offsetProgressAxis = offsetProgress.y,
                        pivotAxis = levitationConfig.pivot.y
                    )
                }
                LevitationOrientation.Horizontal -> {
                    0.0F
                }
            }
        )
    }

    /**
     * Levitation content scale. Calculated specifically for [Modifier.levitation].
     *
     * @see calculateScale
     */
    val scale by derivedStateOf {
        calculateScale()
    }

    /**
     * Levitation content rotation. Calculated specifically for [Modifier.levitation].
     *
     * @see degreeProgress
     * @see pressFraction
     * @see LevitationConfig.degree
     */
    val rotation by derivedStateOf {
        Offset(
            x = degreeProgress.y * -pressFraction * levitationConfig.degree,
            y = degreeProgress.x * pressFraction * levitationConfig.degree
        )
    }

    /**
     * Levitation shadow scale. Calculated specifically for [Modifier.levitation].
     *
     * @see calculateScale
     * @see ShadowConfig.downscaleMultiplier
     */
    val shadowScale by derivedStateOf {
        shadowConfig?.let {
            calculateScale(downscaleMultiplier = it.downscaleMultiplier)
        } ?: 0.0F
    }

    /**
     * Levitation shadow rotation. Calculated specifically for [Modifier.levitation].
     *
     * @see rotation
     * @see ShadowConfig.degreeMultiplier
     */
    val shadowRotation by derivedStateOf {
        shadowConfig?.let {
            rotation.times(operand = it.degreeMultiplier)
        } ?: rotation
    }

    /**
     * Levitation shadow translation. Calculated specifically for [Modifier.levitation].
     *
     * @see degreeProgress
     * @see pressFraction
     * @see LevitationConfig.degree
     * @see ShadowConfig.translationOffset
     * @see ShadowConfig.isPivotedWhenPressed
     */
    val shadowTranslation by derivedStateOf {
        shadowConfig?.let {
            // Combines shadows offset, shadow translation offset, and pivot centering if needed.
            val degreePressTranslationX = degreeProgress.x * pressFraction * -shadowTranslationOffsetState.x
            val degreePressCenteredShadowX = if (it.isPivotedWhenPressed) {
                ((1.0F - abs(degreeProgress.x)) * pressFraction * shadowOffsetState.x)
            } else {
                0.0F
            }
            val degreePressTranslationY = degreeProgress.y * pressFraction * -shadowTranslationOffsetState.y
            val degreePressCenteredShadowY = if (it.isPivotedWhenPressed) {
                ((1.0F - abs(degreeProgress.y)) * pressFraction * shadowOffsetState.y)
            } else {
                0.0F
            }

            Offset(
                x = degreePressTranslationX - degreePressCenteredShadowX,
                y = degreePressTranslationY - degreePressCenteredShadowY
            )
        } ?: Offset.Zero
    }

    /**
     * Shadow camera distance. Calculated specifically for [Modifier.levitation].
     *
     * @see LevitationConfig.cameraDistance
     * @see ShadowConfig.cameraDistanceMultiplier
     */
    val shadowCameraDistance by derivedStateOf {
        shadowConfig?.let {
            levitationConfig.cameraDistance * it.cameraDistanceMultiplier
        } ?: levitationConfig.cameraDistance
    }

    /**
     * Performs levitation press at the provided [pivot] point. Levitation animates to full press, and then releases press.
     *
     * @param pivot The point at which perform press action. Range from 0.0F to 1.0F.
     * @see LevitationConfig.pivot
     * @see AnimatePressFraction
     * @see isSimulatedPressStartedState
     */
    fun press(pivot: Offset = levitationConfig.pivot) {
        rawPressOffsetState = Offset(
            x = size.width * pivot.x,
            y = size.height * pivot.y,
        )
        isSimulatedPressStartedState = true
        isPressedState = true

        // If levitation is already fully pressed, release it.
        if (pressFraction == 1.0F) {
            isSimulatedPressStartedState = false
            isPressedState = false
        }
    }

    /**
     * Animates [pressFraction]. Also, handles the [isAwaitPressedState] and [isSimulatedPressStartedState] behaviour.
     *
     * @see PressConfig.isAwaitPressAnimation
     * @see press
     * @see LevitationContainer
     */
    @Composable
    internal fun AnimatePressFraction() {
        val pressFraction by animateFloatAsState(
            targetValue = if (isPressedState) 1.0F else 0.0F,
            animationSpec = pressConfig.pressAnimationSpec
        ) { value ->
            if (pressConfig.isAwaitPressAnimation) {
                isAwaitPressedAnimationFinishedState = value == 1.0F

                if (isAwaitPressedState) {
                    isAwaitPressedState = false
                    isPressedState = false
                }
            }

            if (isSimulatedPressStartedState) {
                if (value == 1.0F) {
                    isSimulatedPressStartedState = false
                    isPressedState = false
                }
            }
        }

        isAwaitPressedAnimationFinishedState = pressFraction == 1.0F
        pressFractionState = pressFraction
    }

    /**
     * Animates levitation [offsetState] between [rawPressOffsetState], [rawDragOffsetState] and [pivotOffset] when released if needed.
     *
     * @see isPressedState
     * @see isDraggedState
     * @see PressConfig.isPivotedWhenReleased
     * @see LevitationContainer
     */
    @Composable
    internal fun AnimateLevitationOffset() {
        val offset by animateOffsetAsState(
            targetValue = when {
                // Animate to drag offset.
                isDraggedState && isPressedState -> {
                    rawDragOffsetState
                }
                else -> {
                    if (pressConfig.isPivotedWhenReleased) {
                        if (isPressedState && isDraggedState.not()) {
                            // Animate to press offset.
                            rawPressOffsetState
                        } else {
                            // Animate to pivot center offset.
                            pivotOffset
                        }
                    } else {
                        // Animate to press offset.
                        rawPressOffsetState
                    }
                }
            },
            animationSpec = if (pressFraction == 0.0F) {
                // Required to react to the first levitation press offset change without animation.
                tween(durationMillis = 0)
            } else {
                spring()
            }
        )

        offsetState = offset
    }

    /**
     * Converts [shadowOffsetState] and [shadowTranslationOffsetState] from [DpOffset] to [Offset] if [ShadowConfig] provided.
     *
     * @see LevitationContainer
     */
    @Composable
    internal fun ConvertShadowOffsets() {
        shadowConfig?.let {
            val density = LocalDensity.current

            @Composable
            fun rememberAndConvertOffsetToPx(offset: DpOffset): Offset {
                return remember(offset) {
                    with(density) {
                        Offset(
                            x = offset.x.toPx(),
                            y = offset.y.toPx()
                        )
                    }
                }
            }

            shadowTranslationOffsetState = rememberAndConvertOffsetToPx(offset = it.translationOffset)

            if (it.isPivotedWhenPressed) {
                shadowOffsetState = when (it.type) {
                    is ShadowType.Elevation -> {
                        Offset.Zero
                    }
                    is ShadowType.ShadowsPlus.SoftLayer -> {
                        rememberAndConvertOffsetToPx(offset = it.type.offset)
                    }
                    is ShadowType.ShadowsPlus.RSBlur -> {
                        rememberAndConvertOffsetToPx(offset = it.type.offset)
                    }
                }
            }
        }
    }

    /**
     * Handles levitation [rawDragOffsetState] and checks whether current [change] drag event is within levitation content bounds, and if no, then the
     * [rawDragOffsetState] calculation will be stopped (when [isDragBlockedUntilRelease] equals false, or basically released press).
     *
     * @param change Current [PointerInputChange] drag event data.
     * @see LevitationContainer
     */
    internal fun handleDrag(
        change: PointerInputChange
    ) {
        // Skip drag calculation until release, if any drag event is outside content bounds.
        if (isDragBlockedUntilRelease) {
            return
        }

        val isOutOfBounds = change.isOutOfBounds(
            size = intSizeState,
            extendedTouchPadding = Size.Zero
        )
        isPressedState = isOutOfBounds.not()
        isDraggedState = isPressedState

        if (isOutOfBounds) {
            rawPressOffsetState = rawDragOffsetState
            return
        }

        change.consume()

        rawDragOffsetState = change.position
    }

    /**
     * Cancels the pending [isAwaitPressedState] indicator.
     *
     * @see AnimatePressFraction
     * @see LevitationContainer
     */
    internal fun cancelPendingAwaitPressedState() {
        if (pressConfig.isAwaitPressAnimation) {
            isAwaitPressedState = false
        }
    }

    /**
     * Handles [PressConfig.isAwaitPressAnimation] behaviour ([isAwaitPressedState] and [isAwaitPressedAnimationFinishedState]) on press release.
     *
     * @see AnimatePressFraction
     * @see LevitationContainer
     */
    internal fun handleAwaitPressedState() {
        if (isAwaitPressedAnimationFinishedState) {
            isPressedState = false
        } else {
            isAwaitPressedState = true
        }
    }

    /**
     * Calculates levitation downscale based on provided [PressConfig.downscale], [PressConfig.type], and [downscaleMultiplier]
     *
     * @param downscaleMultiplier The downscale multiplier fraction. Used for [shadowScale].
     * @return Calculated scale specifically for [Modifier.levitation].
     * @see scale
     * @see shadowScale
     * @see pressFraction
     */
    private fun calculateScale(downscaleMultiplier: Float = 1.0F): Float = with(pressConfig) {
        val downscaleFraction = pressFraction * downscale
        val fraction = downscaleFraction * downscaleMultiplier

        when (type) {
            PressType.Full -> {
                1.0F - fraction
            }
            is PressType.Ranged -> {
                val radialDegreeOffset = max(
                    degreeProgress.x.absoluteValue,
                    degreeProgress.y.absoluteValue
                ).subLerp(
                    start = type.start,
                    stop = type.stop
                )

                1.0F - (1.0F - radialDegreeOffset) * fraction
            }
            PressType.None -> {
                1.0F
            }
        }
    }

    /**
     * Calculates levitation offset progress by its axis in [offsetProgress]. Range from 0.0F to 1.0F.
     *
     * @param offsetAxis The levitation offset axis.
     * @param defaultOffsetAxis The levitation offset default axis, basically the [LevitationConfig.pivot] axis.
     * @param sizeAxis The levitation [size] axis.
     * @return Calculated single axis for [offsetProgress].
     */
    private fun calculateOffsetAxisProgress(
        offsetAxis: Float,
        defaultOffsetAxis: Float,
        sizeAxis: Float
    ): Float {
        if (sizeAxis <= 0) {
            return defaultOffsetAxis
        }

        return (offsetAxis / sizeAxis).coerceIn(0.0F, 1.0F)
    }

    /**
     * Calculates levitation degree progress by its axis in [degreeProgress]. Range from -1.0F to 1.0F. The (0.0F, 0.0F) degree progress is at the [pivotAxis].
     *
     * @param offsetProgressAxis The levitation [offsetProgress] axis.
     * @param pivotAxis The [LevitationConfig.pivot] axis.
     * @return Calculated single axis for [degreeProgress].
     */
    private fun calculateDegreeAxisProgress(
        offsetProgressAxis: Float,
        pivotAxis: Float
    ): Float = if (offsetProgressAxis <= pivotAxis) {
        offsetProgressAxis.subLerp(0.0F, pivotAxis) - 1.0F
    } else {
        offsetProgressAxis.subLerp(pivotAxis, 1.0F)
    }
}