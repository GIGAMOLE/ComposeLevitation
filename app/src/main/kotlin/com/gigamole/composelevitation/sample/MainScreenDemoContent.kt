package com.gigamole.composelevitation.sample

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.gigamole.composelevitation.common.rememberLevitationState
import com.gigamole.composelevitation.levitation.LevitationContainer
import com.gigamole.composelevitation.press.PressConfig
import com.gigamole.composelevitation.press.PressType
import com.gigamole.composelevitation.shadow.ShadowConfig
import com.gigamole.composelevitation.shadow.ShadowType
import com.gigamole.composeshadowsplus.softlayer.SoftLayerShadowContainer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Suppress("unused")
@Composable
fun MainScreenDemoContent() {
    val shape = remember { RectangleShape }

    val startAngle = 20.0F
    val finishAngle = -380.0F
    val angleZoneOffset = 20.0F
    val startZoneAngle = startAngle - angleZoneOffset
    val finishZoneAngle = finishAngle + angleZoneOffset

    var size by remember { mutableStateOf(IntSize.Zero) }
    var isCycling by remember { mutableStateOf(false) }
    val angle = remember {
        Animatable(initialValue = 65.0F)
    }

    val radians by remember(angle.value) {
        derivedStateOf {
            Math.toRadians(angle.value.toDouble()).toFloat()
        }
    }

    val rawOffset by remember(radians) {
        derivedStateOf {
            val startFraction = 1.0F - ((angle.value - startZoneAngle) / (startAngle - startZoneAngle)).coerceIn(0.0F, 1.0F)
            val finishFraction = 1.0F - ((angle.value - finishZoneAngle) / (finishAngle - finishZoneAngle)).coerceIn(0.0F, 1.0F)
            val fraction = when (angle.value) {
                in startZoneAngle..startAngle -> startFraction
                else -> finishFraction
            }

            Offset(
                x = size.center.x + lerp(0.0F, size.center.x.toFloat(), fraction) * sin(radians),
                y = size.center.y + lerp(0.0F, size.center.y.toFloat(), fraction) * cos(radians),
            )
        }
    }
    val pressPivot by remember(rawOffset) {
        derivedStateOf {
            Offset(
                x = rawOffset.x / size.width.toFloat(),
                y = rawOffset.y / size.height.toFloat()
            )
        }
    }

    var isPressAnimation by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val shadowConfig = remember {
        ShadowConfig(
            type = ShadowType.ShadowsPlus.SoftLayer(),
            downscaleMultiplier = 3.0F
        )
    }
    val pressState = rememberLevitationState(
        pressConfig = PressConfig(
            type = PressType.Full,
            pressAnimationSpec = tween(durationMillis = 500)
        ),
        shadowConfig = shadowConfig
    )
    val cyclingState = rememberLevitationState(
        pressConfig = PressConfig(
            type = PressType.None,
            pressAnimationSpec = tween(durationMillis = 500)
        ),
        shadowConfig = shadowConfig
    )

    SoftLayerShadowContainer {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ) {
                    coroutineScope.launch {
                        isCycling = false
                        isPressAnimation = true
                        delay(500)
                        pressState.press()
                        delay(1000)
                        isPressAnimation = false
                        angle.snapTo(targetValue = startAngle)
                        isCycling = true
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            LevitationContainer(
                modifier = Modifier,
                state = if (isPressAnimation) pressState else cyclingState
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = Color.White,
                            shape = shape
                        )
                        .padding(
                            horizontal = 32.dp,
                            vertical = 24.dp
                        )
                        .onPlaced {
                            size = it.size
                        }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(space = 6.dp)) {
                        Text(
                            text = "ComposeLevitation",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamilySpaceGrotesk
                        )
                        Text(
                            text = "Empower Android Compose UI with levitation effect",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Normal,
                            fontFamily = FontFamilyOpenSans
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(pressPivot, isPressAnimation, isCycling) {
        if (pressPivot != Offset.Unspecified && isPressAnimation.not() && isCycling) {
            cyclingState.press(pivot = pressPivot)
        }
    }
    LaunchedEffect(isCycling) {
        if (isCycling) {
            angle.animateTo(
                targetValue = finishAngle,
                animationSpec = tween(durationMillis = 2500)
            )
        }
    }
}