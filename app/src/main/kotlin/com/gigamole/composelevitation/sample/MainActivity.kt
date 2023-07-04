@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalTextApi::class, ExperimentalTextApi::class)

package com.gigamole.composelevitation.sample

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.gigamole.composelevitation.common.LevitationState
import com.gigamole.composelevitation.levitation.LevitationConfig
import com.gigamole.composelevitation.levitation.LevitationContainer
import com.gigamole.composelevitation.levitation.LevitationDefaults
import com.gigamole.composelevitation.levitation.LevitationOrientation
import com.gigamole.composelevitation.press.PressConfig
import com.gigamole.composelevitation.press.PressDefaults
import com.gigamole.composelevitation.press.PressType
import com.gigamole.composelevitation.shadow.ShadowConfig
import com.gigamole.composelevitation.shadow.ShadowDefaults
import com.gigamole.composelevitation.shadow.ShadowType
import com.gigamole.composeshadowsplus.common.ShadowsPlusDefaults
import com.gigamole.composeshadowsplus.rsblur.RSBlurShadowDefaults
import com.gigamole.composeshadowsplus.softlayer.SoftLayerShadowContainer
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor
import kotlin.math.abs
import kotlin.random.Random

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainScreen()
        }
    }
}

@Composable
private fun MainScreen() {
    MainTheme {
        MainScreenContent()
//        MainScreenDemoContent()
//        MainScreenHolographicContent()
    }
}

@Composable
fun MainScreenContent() {
    var levitationSampleConfigType by remember { mutableStateOf(SampleConfigType.Common) }

    var contentAdditionalWidth by remember { mutableStateOf(0.dp) }
    var contentAdditionalHeight by remember { mutableStateOf(0.dp) }
    var levitationShapeType by remember { mutableStateOf(LevitationShapeType.Rect) }
    var levitationShapeCorners by remember { mutableStateOf(8.dp) }
    var levitationIsClickable by remember { mutableStateOf(false) }
    var contentIsImage by remember { mutableStateOf(false) }
    var contentSampleImageHighlightType by remember { mutableStateOf(SampleImageHighlightType.None) }
    var contentColor by remember { mutableStateOf(Color.White) }

    var levitationOrientation by remember { mutableStateOf(LevitationDefaults.Orientation) }
    var levitationDegree by remember { mutableStateOf(LevitationDefaults.Degree) }
    var levitationCameraDistance by remember { mutableStateOf(LevitationDefaults.CameraDistance) }
    var levitationPivotX by remember { mutableStateOf(LevitationDefaults.Pivot.x) }
    var levitationPivotY by remember { mutableStateOf(LevitationDefaults.Pivot.y) }
    var levitationIsBounded by remember { mutableStateOf(LevitationDefaults.IsBounded) }
    var levitationIsClipped by remember { mutableStateOf(LevitationDefaults.IsClipped) }
    var levitationIsEnabled by remember { mutableStateOf(true) }

    var samplePressType by remember { mutableStateOf(SamplePressType.Ranged) }
    var rangedPressStart by remember { mutableStateOf(PressDefaults.Type.Ranged.Start) }
    var rangedPressStop by remember { mutableStateOf(PressDefaults.Type.Ranged.Stop) }
    var pressDownscale by remember { mutableStateOf(PressDefaults.Config.Downscale) }
    var pressAnimationSpecType by remember { mutableStateOf(PressAnimationSpecType.Spring) }
    var isAwaitPressAnimation by remember { mutableStateOf(PressDefaults.Config.IsAwaitPressAnimation) }
    var isPressPivotedWhenReleased by remember { mutableStateOf(PressDefaults.Config.IsPivotedWhenReleased) }

    var shadowIsEnabled by remember { mutableStateOf(true) }

    var shadowDegreeMultiplier by remember { mutableStateOf(ShadowDefaults.Config.DegreeMultiplier) }
    var shadowDownscaleMultiplier by remember { mutableStateOf(ShadowDefaults.Config.DownscaleMultiplier) }
    var shadowCameraDistanceMultiplier by remember { mutableStateOf(ShadowDefaults.Config.CameraDistanceMultiplier) }
    var shadowTranslationOffsetX by remember { mutableStateOf(ShadowDefaults.Config.TranslationOffset.x) }
    var shadowTranslationOffsetY by remember { mutableStateOf(ShadowDefaults.Config.TranslationOffset.y) }
    var shadowIsPivotedWhenPressed by remember { mutableStateOf(ShadowDefaults.Config.IsPivotedWhenPressed) }
    var sampleShadowType by remember { mutableStateOf(SampleShadowType.SoftLayer) }
    var shadowRadius by remember { mutableStateOf(ShadowsPlusDefaults.ShadowRadius) }
    var shadowColor by remember { mutableStateOf(ShadowsPlusDefaults.ShadowColor) }
    var shadowSpread by remember { mutableStateOf(ShadowsPlusDefaults.ShadowSpread) }
    var shadowOffsetX by remember { mutableStateOf(ShadowsPlusDefaults.ShadowOffset.x) }
    var shadowOffsetY by remember { mutableStateOf(ShadowsPlusDefaults.ShadowOffset.y) }
    var rsBlurShadowAlignRadius by remember { mutableStateOf(RSBlurShadowDefaults.RSBlurShadowAlignRadius) }

    var isContentColorPickerVisible by remember { mutableStateOf(false) }
    var isShadowColorPickerVisible by remember { mutableStateOf(false) }

    val levitationPivot by remember(
        levitationPivotX,
        levitationPivotY
    ) {
        derivedStateOf {
            Offset(
                x = levitationPivotX,
                y = levitationPivotY
            )
        }
    }
    val levitationShape by remember(
        levitationShapeType,
        levitationShapeCorners
    ) {
        derivedStateOf {
            when (levitationShapeType) {
                LevitationShapeType.Rect -> RectangleShape
                LevitationShapeType.Circle -> CircleShape
                LevitationShapeType.RoundedCorners -> RoundedCornerShape(size = levitationShapeCorners)
                LevitationShapeType.CutCorners -> CutCornerShape(size = levitationShapeCorners)
            }
        }
    }

    val pressType by remember(
        samplePressType,
        rangedPressStart,
        rangedPressStop
    ) {
        derivedStateOf {
            when (samplePressType) {
                SamplePressType.Ranged -> PressType.Ranged(
                    start = rangedPressStart,
                    stop = rangedPressStop
                )
                SamplePressType.Full -> PressType.Full
                SamplePressType.None -> PressType.None
            }
        }
    }
    val pressAnimationSpec: AnimationSpec<Float> by remember(pressAnimationSpecType) {
        derivedStateOf {
            when (pressAnimationSpecType) {
                PressAnimationSpecType.Spring -> spring()
                PressAnimationSpecType.Tween500 -> tween(durationMillis = 500)
            }
        }
    }

    val shadowTranslationOffset by remember(
        shadowTranslationOffsetX,
        shadowTranslationOffsetY
    ) {
        derivedStateOf {
            DpOffset(
                x = shadowTranslationOffsetX,
                y = shadowTranslationOffsetY
            )
        }
    }
    val shadowOffset by remember(shadowOffsetX, shadowOffsetY) {
        derivedStateOf {
            DpOffset(
                x = shadowOffsetX,
                y = shadowOffsetY
            )
        }
    }
    val elevationColor by remember(shadowColor) {
        derivedStateOf {
            shadowColor.copy(alpha = 1.0F)
        }
    }

    val shadowType by remember(
        sampleShadowType,
        rsBlurShadowAlignRadius,
        shadowColor,
        elevationColor,
        shadowOffset,
        shadowSpread
    ) {
        derivedStateOf {
            when (sampleShadowType) {
                SampleShadowType.RSBlur -> ShadowType.ShadowsPlus.RSBlur(
                    color = shadowColor,
                    offset = shadowOffset,
                    spread = shadowSpread,
                    isRadiusAligned = rsBlurShadowAlignRadius
                )
                SampleShadowType.SoftLayer -> ShadowType.ShadowsPlus.SoftLayer(
                    color = shadowColor,
                    offset = shadowOffset,
                    spread = shadowSpread
                )
                SampleShadowType.Elevation -> ShadowType.Elevation(
                    isClipped = true,
                    ambientColor = elevationColor,
                    spotColor = elevationColor
                )
            }
        }
    }

    val levitationConfig by remember(
        levitationOrientation,
        levitationDegree,
        levitationPivot,
        levitationCameraDistance,
        levitationIsBounded,
        levitationShape,
        levitationIsClipped
    ) {
        derivedStateOf {
            LevitationConfig(
                orientation = levitationOrientation,
                degree = levitationDegree,
                pivot = levitationPivot,
                cameraDistance = levitationCameraDistance,
                isBounded = levitationIsBounded,
                shape = levitationShape,
                isClipped = levitationIsClipped
            )
        }
    }

    val pressConfig by remember(
        pressType,
        pressDownscale,
        pressAnimationSpec,
        isAwaitPressAnimation,
        isPressPivotedWhenReleased
    ) {
        derivedStateOf {
            PressConfig(
                type = pressType,
                downscale = pressDownscale,
                pressAnimationSpec = pressAnimationSpec,
                isAwaitPressAnimation = isAwaitPressAnimation,
                isPivotedWhenReleased = isPressPivotedWhenReleased
            )
        }
    }

    val shadowConfig: ShadowConfig? by remember(
        shadowIsEnabled,
        shadowDegreeMultiplier,
        shadowDownscaleMultiplier,
        shadowCameraDistanceMultiplier,
        shadowTranslationOffset,
        shadowIsPivotedWhenPressed,
        shadowType,
        shadowRadius
    ) {
        derivedStateOf {
            if (shadowIsEnabled) {
                ShadowConfig(
                    degreeMultiplier = shadowDegreeMultiplier,
                    downscaleMultiplier = shadowDownscaleMultiplier,
                    cameraDistanceMultiplier = shadowCameraDistanceMultiplier,
                    translationOffset = shadowTranslationOffset,
                    isPivotedWhenPressed = shadowIsPivotedWhenPressed,
                    radius = shadowRadius,
                    type = shadowType
                )
            } else {
                null
            }
        }
    }

    val state by remember(
        levitationConfig,
        pressConfig,
        shadowConfig
    ) {
        derivedStateOf {
            LevitationState(
                levitationConfig = levitationConfig,
                pressConfig = pressConfig,
                shadowConfig = shadowConfig
            )
        }
    }

    fun resetCommonConfig() {
        contentAdditionalWidth = 0.dp
        contentAdditionalHeight = 0.dp
        levitationShapeType = LevitationShapeType.Rect
        levitationShapeCorners = 8.dp
        levitationIsClickable = false
        contentIsImage = false
        contentSampleImageHighlightType = SampleImageHighlightType.None
        contentColor = Color.White
        isContentColorPickerVisible = false
    }

    fun resetLevitationConfig() {
        levitationOrientation = LevitationDefaults.Orientation
        levitationDegree = LevitationDefaults.Degree
        levitationCameraDistance = LevitationDefaults.CameraDistance
        levitationPivotX = LevitationDefaults.Pivot.x
        levitationPivotY = LevitationDefaults.Pivot.y
        levitationIsBounded = LevitationDefaults.IsBounded
        levitationIsClipped = LevitationDefaults.IsClipped
        levitationIsEnabled = true
    }

    fun resetPressConfig() {
        samplePressType = SamplePressType.Ranged
        rangedPressStart = PressDefaults.Type.Ranged.Start
        rangedPressStop = PressDefaults.Type.Ranged.Stop
        pressDownscale = PressDefaults.Config.Downscale
        pressAnimationSpecType = PressAnimationSpecType.Spring
        isAwaitPressAnimation = PressDefaults.Config.IsAwaitPressAnimation
        isPressPivotedWhenReleased = PressDefaults.Config.IsPivotedWhenReleased
    }

    fun resetShadowConfig() {
        shadowIsEnabled = true
        shadowDegreeMultiplier = ShadowDefaults.Config.DegreeMultiplier
        shadowDownscaleMultiplier = ShadowDefaults.Config.DownscaleMultiplier
        shadowCameraDistanceMultiplier = ShadowDefaults.Config.CameraDistanceMultiplier
        shadowTranslationOffsetX = ShadowDefaults.Config.TranslationOffset.x
        shadowTranslationOffsetY = ShadowDefaults.Config.TranslationOffset.y
        shadowIsPivotedWhenPressed = ShadowDefaults.Config.IsPivotedWhenPressed
        sampleShadowType = SampleShadowType.SoftLayer
        shadowRadius = ShadowsPlusDefaults.ShadowRadius
        shadowColor = ShadowsPlusDefaults.ShadowColor
        shadowSpread = ShadowsPlusDefaults.ShadowSpread
        shadowOffsetX = ShadowsPlusDefaults.ShadowOffset.x
        shadowOffsetY = ShadowsPlusDefaults.ShadowOffset.y
        rsBlurShadowAlignRadius = RSBlurShadowDefaults.RSBlurShadowAlignRadius
        isShadowColorPickerVisible = false
    }

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val startFullHighlightColor = Color.Black.copy(alpha = 0.5F)
    val stopFullHighlightColor = Color.White.copy(alpha = 0.5F)
    val minHighlightRadius = 50.dp
    val maxHighlightRadius = 150.dp
    val startRadialHighlightColor = Color.Black.copy(alpha = 1.0F)
    val stopRadialHighlightColor = Color.White.copy(alpha = 1.0F)
    val startRadialEndHighlightColor = startFullHighlightColor.copy(alpha = 0.0F)
    val stopRadialEndHighlightColor = stopFullHighlightColor.copy(alpha = 0.0F)

    LaunchedEffect(levitationSampleConfigType) {
        scrollState.scrollTo(0)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        @Composable
        fun LevitationContent() {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LevitationContainer(
                    modifier = Modifier
                        .width(200.dp + contentAdditionalWidth)
                        .height(200.dp + contentAdditionalHeight),
                    isEnabled = levitationIsEnabled,
                    state = state
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = levitationShape,
                        color = if (contentIsImage) Color.White else contentColor,
                        onClick = {
                            Toast.makeText(context, "Click!", Toast.LENGTH_SHORT).show()
                        },
                        enabled = levitationIsClickable
                    ) {
                        if (contentIsImage) {
                            Image(
                                modifier = Modifier.fillMaxSize(),
                                painter = painterResource(id = R.drawable.android),
                                contentDescription = "",
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.Center
                            )

                            when (contentSampleImageHighlightType) {
                                SampleImageHighlightType.Full -> {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        drawRect(
                                            color = lerp(
                                                start = startFullHighlightColor,
                                                stop = stopFullHighlightColor,
                                                fraction = 1.0F - state.offsetProgress.y
                                            ),
                                            alpha = abs(state.degreeProgress.y) * state.pressFraction,
                                            blendMode = BlendMode.Overlay
                                        )
                                    }
                                }
                                SampleImageHighlightType.Radial -> {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val fraction = (abs(state.degreeProgress.x) + abs(state.degreeProgress.y)) / 2.0F
                                        val radius = lerp(
                                            start = minHighlightRadius,
                                            stop = maxHighlightRadius,
                                            fraction = fraction
                                        ).toPx()
                                        val circleCenter = Offset(
                                            x = size.width * state.offsetProgress.x,
                                            y = size.height * state.offsetProgress.y
                                        )

                                        drawCircle(
                                            brush = Brush.radialGradient(
                                                colors = listOf(
                                                    lerp(
                                                        start = startRadialHighlightColor,
                                                        stop = stopRadialHighlightColor,
                                                        fraction = 1.0F - state.offsetProgress.y
                                                    ),
                                                    lerp(
                                                        start = startRadialEndHighlightColor,
                                                        stop = stopRadialEndHighlightColor,
                                                        fraction = 1.0F - state.offsetProgress.y
                                                    ),
                                                ),
                                                center = circleCenter,
                                                radius = radius
                                            ),
                                            center = circleCenter,
                                            radius = radius,
                                            alpha = fraction * state.pressFraction,
                                            blendMode = BlendMode.Overlay
                                        )
                                    }
                                }
                                SampleImageHighlightType.None -> {
                                    /* Do nothing. */
                                }
                            }
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ratio = 1.0F)
                .clipToBounds()
        ) {
            if (sampleShadowType == SampleShadowType.SoftLayer) {
                SoftLayerShadowContainer {
                    LevitationContent()
                }
            } else {
                LevitationContent()
            }
        }
        Divider()
        TabRow(
            selectedTabIndex = levitationSampleConfigType.ordinal,
            modifier = Modifier.fillMaxWidth(),
            tabs = {
                SampleConfigType.values().forEach { enumValue ->
                    Tab(
                        selected = enumValue == levitationSampleConfigType,
                        text = {
                            Text(
                                modifier = Modifier.basicMarquee(),
                                text = enumValue.name.uppercase(),
                                maxLines = 1
                            )
                        },
                        onClick = {
                            levitationSampleConfigType = enumValue
                        },
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(state = scrollState)
                .padding(all = 20.dp),
            verticalArrangement = Arrangement.spacedBy(space = 4.dp)
        ) {
            when (levitationSampleConfigType) {
                SampleConfigType.Common -> {
                    var tempContentAdditionalWidth by remember(contentAdditionalWidth) {
                        mutableStateOf(contentAdditionalWidth)
                    }

                    Text(
                        text = "ADDITIONAL WIDTH:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Slider(
                        modifier = Modifier.fillMaxWidth(),
                        value = tempContentAdditionalWidth.value,
                        onValueChange = {
                            tempContentAdditionalWidth = it.dp
                        },
                        onValueChangeFinished = {
                            contentAdditionalWidth = tempContentAdditionalWidth
                        },
                        valueRange = -50.0F..100.0F,
                        steps = 15
                    )

                    var tempContentAdditionalHeight by remember(contentAdditionalHeight) {
                        mutableStateOf(contentAdditionalHeight)
                    }

                    Text(
                        text = "ADDITIONAL HEIGHT:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Slider(
                        modifier = Modifier.fillMaxWidth(),
                        value = tempContentAdditionalHeight.value,
                        onValueChange = {
                            tempContentAdditionalHeight = it.dp
                        },
                        onValueChangeFinished = {
                            contentAdditionalHeight = tempContentAdditionalHeight
                        },
                        valueRange = -50.0F..100.0F,
                        steps = 15
                    )

                    Text(
                        text = "SHAPE:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    TabRow(
                        selectedTabIndex = levitationShapeType.ordinal,
                        modifier = Modifier.fillMaxWidth(),
                        tabs = {
                            LevitationShapeType.values().forEach { enumValue ->
                                Tab(
                                    selected = enumValue == levitationShapeType,
                                    text = {
                                        Text(
                                            modifier = Modifier.basicMarquee(),
                                            text = enumValue.name.uppercase(),
                                            maxLines = 1
                                        )
                                    },
                                    onClick = {
                                        levitationShapeType = enumValue
                                    },
                                )
                            }
                        }
                    )

                    if (levitationShapeType == LevitationShapeType.RoundedCorners ||
                        levitationShapeType == LevitationShapeType.CutCorners
                    ) {
                        Text(
                            modifier = Modifier.padding(top = 20.dp),
                            text = "SHAPE CORNERS:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Slider(
                            modifier = Modifier.fillMaxWidth(),
                            value = levitationShapeCorners.value,
                            onValueChange = {
                                levitationShapeCorners = it.dp
                            },
                            valueRange = 0.0F..75.0F,
                            steps = 20
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(space = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(weight = 1.0F),
                            text = "IS CLICKABLE:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Switch(
                            checked = levitationIsClickable,
                            onCheckedChange = {
                                levitationIsClickable = it
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(space = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(weight = 1.0F),
                            text = "IS IMAGE:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Switch(
                            checked = contentIsImage,
                            onCheckedChange = {
                                contentIsImage = it

                                if (contentIsImage.not()) {
                                    contentSampleImageHighlightType = SampleImageHighlightType.None
                                }
                            }
                        )
                    }

                    if (contentIsImage) {
                        Text(
                            text = "IMAGE HIGHLIGHT TYPE:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        TabRow(
                            selectedTabIndex = contentSampleImageHighlightType.ordinal,
                            modifier = Modifier.fillMaxWidth(),
                            tabs = {
                                SampleImageHighlightType.values().forEach { enumValue ->
                                    Tab(
                                        selected = enumValue == contentSampleImageHighlightType,
                                        text = {
                                            Text(
                                                modifier = Modifier.basicMarquee(),
                                                text = enumValue.name.uppercase(),
                                                maxLines = 1
                                            )
                                        },
                                        onClick = {
                                            contentSampleImageHighlightType = enumValue
                                        },
                                    )
                                }
                            }
                        )
                    } else {
                        Text(
                            text = "COLOR:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(intrinsicSize = IntrinsicSize.Min),
                            horizontalArrangement = Arrangement.spacedBy(space = 20.dp)
                        ) {
                            Button(
                                modifier = Modifier.weight(weight = 1.0F),
                                onClick = {
                                    isContentColorPickerVisible = true
                                }
                            ) {
                                Text(text = "PICK COLOR")
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .aspectRatio(ratio = 1.0F)
                                    .background(
                                        color = contentColor,
                                        shape = RoundedCornerShape(size = 4.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = Color.Black,
                                        shape = RoundedCornerShape(size = 4.dp)
                                    )
                                    .clickable {
                                        isContentColorPickerVisible = true
                                    }
                            )
                        }
                    }

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        onClick = {
                            resetCommonConfig()
                        }
                    ) {
                        Text(text = "RESET")
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            resetCommonConfig()
                            resetLevitationConfig()
                            resetPressConfig()
                            resetShadowConfig()
                        }
                    ) {
                        Text(text = "RESET ALL")
                    }
                }
                SampleConfigType.Levitation -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(space = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(weight = 1.0F),
                            text = "IS ENABLED:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Switch(
                            checked = levitationIsEnabled,
                            onCheckedChange = {
                                levitationIsEnabled = it
                            }
                        )
                    }

                    if (levitationIsEnabled.not()) {
                        return
                    }

                    Text(
                        text = "ORIENTATION:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    TabRow(
                        selectedTabIndex = levitationOrientation.ordinal,
                        modifier = Modifier.fillMaxWidth(),
                        tabs = {
                            LevitationOrientation.values().forEach { enumValue ->
                                Tab(
                                    selected = enumValue == levitationOrientation,
                                    text = {
                                        Text(
                                            modifier = Modifier.basicMarquee(),
                                            text = enumValue.name.uppercase(),
                                            maxLines = 1
                                        )
                                    },
                                    onClick = {
                                        levitationOrientation = enumValue
                                    },
                                )
                            }
                        }
                    )

                    Text(
                        modifier = Modifier.padding(top = 20.dp),
                        text = "DEGREE:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Slider(
                        modifier = Modifier.fillMaxWidth(),
                        value = levitationDegree,
                        onValueChange = {
                            levitationDegree = it
                        },
                        valueRange = -8.0F..8.0F,
                        steps = 16
                    )

                    Text(
                        text = "CAMERA DISTANCE:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Slider(
                        modifier = Modifier.fillMaxWidth(),
                        value = levitationCameraDistance,
                        onValueChange = {
                            levitationCameraDistance = it
                        },
                        valueRange = 1.0F..16.0F,
                        steps = 16
                    )

                    Text(
                        text = "PIVOT X:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Slider(
                        modifier = Modifier.fillMaxWidth(),
                        value = levitationPivotX,
                        onValueChange = {
                            levitationPivotX = it
                        },
                        valueRange = 0.0F..1.0F,
                        steps = 9
                    )

                    Text(
                        text = "PIVOT Y:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Slider(
                        modifier = Modifier.fillMaxWidth(),
                        value = levitationPivotY,
                        onValueChange = {
                            levitationPivotY = it
                        },
                        valueRange = 0.0F..1.0F,
                        steps = 9
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(space = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(weight = 1.0F),
                            text = "IS BOUNDED:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Switch(
                            checked = levitationIsBounded,
                            onCheckedChange = {
                                levitationIsBounded = it
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(space = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(weight = 1.0F),
                            text = "IS CLIPPED:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Switch(
                            checked = levitationIsClipped,
                            onCheckedChange = {
                                levitationIsClipped = it
                            }
                        )
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            resetLevitationConfig()
                        }
                    ) {
                        Text(text = "RESET")
                    }
                }
                SampleConfigType.Press -> {
                    Text(
                        text = "TYPE:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    TabRow(
                        selectedTabIndex = samplePressType.ordinal,
                        modifier = Modifier.fillMaxWidth(),
                        tabs = {
                            SamplePressType.values().forEach { enumValue ->
                                Tab(
                                    selected = enumValue == samplePressType,
                                    text = {
                                        Text(
                                            modifier = Modifier.basicMarquee(),
                                            text = enumValue.name.uppercase(),
                                            maxLines = 1
                                        )
                                    },
                                    onClick = {
                                        samplePressType = enumValue
                                    },
                                )
                            }
                        }
                    )

                    if (samplePressType == SamplePressType.None) {
                        return
                    }

                    if (samplePressType == SamplePressType.Ranged) {
                        Text(
                            modifier = Modifier.padding(top = 20.dp),
                            text = "RANGED PRESS START:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Slider(
                            modifier = Modifier.fillMaxWidth(),
                            value = rangedPressStart,
                            onValueChange = {
                                rangedPressStart = it
                            },
                            valueRange = 0.0F..1.0F,
                            steps = 9
                        )

                        Text(
                            text = "RANGED PRESS STOP:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Slider(
                            modifier = Modifier.fillMaxWidth(),
                            value = rangedPressStop,
                            onValueChange = {
                                rangedPressStop = it
                            },
                            valueRange = 0.0F..1.0F,
                            steps = 9
                        )
                    } else {
                        Spacer(modifier = Modifier.height(height = 20.dp))
                    }

                    Text(
                        text = "DOWNSCALE:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Slider(
                        modifier = Modifier.fillMaxWidth(),
                        value = pressDownscale,
                        onValueChange = {
                            pressDownscale = it
                        },
                        valueRange = -0.1F..0.1F,
                        steps = 32
                    )

                    Text(
                        text = "ANIMATION SPEC TYPE:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    TabRow(
                        selectedTabIndex = pressAnimationSpecType.ordinal,
                        modifier = Modifier.fillMaxWidth(),
                        tabs = {
                            PressAnimationSpecType.values().forEach { enumValue ->
                                Tab(
                                    selected = enumValue == pressAnimationSpecType,
                                    text = {
                                        Text(
                                            modifier = Modifier.basicMarquee(),
                                            text = enumValue.name.uppercase(),
                                            maxLines = 1
                                        )
                                    },
                                    onClick = {
                                        pressAnimationSpecType = enumValue
                                    },
                                )
                            }
                        }
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(space = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(weight = 1.0F),
                            text = "IS AWAIT PRESS ANIMATION:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Switch(
                            checked = isAwaitPressAnimation,
                            onCheckedChange = {
                                isAwaitPressAnimation = it
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(space = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(weight = 1.0F),
                            text = "IS PIVOTED WHEN RELEASED:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Switch(
                            checked = isPressPivotedWhenReleased,
                            onCheckedChange = {
                                isPressPivotedWhenReleased = it
                            }
                        )
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            state.press()
                        }
                    ) {
                        Text(text = "SIMULATE PIVOT PRESS")
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            state.press(
                                pivot = Offset(
                                    x = Random.nextFloat(),
                                    y = Random.nextFloat()
                                )
                            )
                        }
                    ) {
                        Text(text = "SIMULATE RANDOM PRESS")
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            resetPressConfig()
                        }
                    ) {
                        Text(text = "RESET")
                    }
                }
                SampleConfigType.Shadow -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(space = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(weight = 1.0F),
                            text = "IS ENABLED:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Switch(
                            checked = shadowIsEnabled,
                            onCheckedChange = {
                                shadowIsEnabled = it
                            }
                        )
                    }

                    if (shadowIsEnabled.not()) {
                        return
                    }

                    Text(
                        text = "DEGREE MULTIPLIER:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Slider(
                        modifier = Modifier.fillMaxWidth(),
                        value = shadowDegreeMultiplier,
                        onValueChange = {
                            shadowDegreeMultiplier = it
                        },
                        valueRange = -2.0F..2.0F,
                        steps = 40
                    )

                    Text(
                        text = "DOWNSCALE MULTIPLIER:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Slider(
                        modifier = Modifier.fillMaxWidth(),
                        value = shadowDownscaleMultiplier,
                        onValueChange = {
                            shadowDownscaleMultiplier = it
                        },
                        valueRange = -4.0F..4.0F,
                        steps = 40
                    )

                    Text(
                        text = "CAMERA DISTANCE MULTIPLIER:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Slider(
                        modifier = Modifier.fillMaxWidth(),
                        value = shadowCameraDistanceMultiplier,
                        onValueChange = {
                            shadowCameraDistanceMultiplier = it
                        },
                        valueRange = 0.1F..2.0F,
                        steps = 20
                    )

                    Text(
                        text = "TRANSLATION OFFSET X:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Slider(
                        modifier = Modifier.fillMaxWidth(),
                        value = shadowTranslationOffsetX.value,
                        onValueChange = {
                            shadowTranslationOffsetX = it.dp
                        },
                        valueRange = -32.0F..32.0F,
                        steps = 64
                    )

                    Text(
                        text = "TRANSLATION OFFSET Y:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Slider(
                        modifier = Modifier.fillMaxWidth(),
                        value = shadowTranslationOffsetY.value,
                        onValueChange = {
                            shadowTranslationOffsetY = it.dp
                        },
                        valueRange = -32.0F..32.0F,
                        steps = 64
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(space = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(weight = 1.0F),
                            text = "IS PIVOTED WHEN PRESSED:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Switch(
                            checked = shadowIsPivotedWhenPressed,
                            onCheckedChange = {
                                shadowIsPivotedWhenPressed = it
                            }
                        )
                    }

                    Text(
                        text = "TYPE:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    TabRow(
                        selectedTabIndex = sampleShadowType.ordinal,
                        modifier = Modifier.fillMaxWidth(),
                        tabs = {
                            SampleShadowType.values().forEach { enumValue ->
                                Tab(
                                    selected = enumValue == sampleShadowType,
                                    text = {
                                        Text(text = enumValue.name.uppercase())
                                    },
                                    onClick = {
                                        sampleShadowType = enumValue
                                    },
                                )
                            }
                        }
                    )

                    Text(
                        modifier = Modifier.padding(top = 20.dp),
                        text = "COLOR:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(intrinsicSize = IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(space = 20.dp)
                    ) {
                        Button(
                            modifier = Modifier.weight(weight = 1.0F),
                            onClick = {
                                isShadowColorPickerVisible = true
                            }
                        ) {
                            Text(text = "PICK COLOR")
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .aspectRatio(ratio = 1.0F)
                                .background(
                                    color = shadowColor,
                                    shape = RoundedCornerShape(size = 4.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color.Black,
                                    shape = RoundedCornerShape(size = 4.dp)
                                )
                                .clickable {
                                    isShadowColorPickerVisible = true
                                }
                        )
                    }

                    if (sampleShadowType == SampleShadowType.RSBlur) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(space = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.weight(weight = 1.0F),
                                text = "ALIGN RSBLUR RADIUS:",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Switch(
                                checked = rsBlurShadowAlignRadius,
                                onCheckedChange = {
                                    rsBlurShadowAlignRadius = it
                                }
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(height = 4.dp))
                    }

                    var rsBlurShadowRadius by remember { mutableStateOf(shadowRadius) }

                    Text(
                        text = "RADIUS:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Slider(
                        modifier = Modifier.fillMaxWidth(),
                        value = if (sampleShadowType == SampleShadowType.RSBlur) {
                            rsBlurShadowRadius.value
                        } else {
                            shadowRadius.value
                        },
                        onValueChange = {
                            if (sampleShadowType == SampleShadowType.RSBlur) {
                                rsBlurShadowRadius = it.dp
                            } else {
                                shadowRadius = it.dp
                            }
                        },
                        onValueChangeFinished = {
                            if (sampleShadowType == SampleShadowType.RSBlur) {
                                shadowRadius = rsBlurShadowRadius
                            }
                        },
                        valueRange = 0.0F..32.0F,
                        steps = 32
                    )

                    if (sampleShadowType != SampleShadowType.Elevation) {
                        var rsBlurShadowSpread by remember { mutableStateOf(shadowSpread) }

                        Text(
                            text = "SPREAD:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Slider(
                            modifier = Modifier.fillMaxWidth(),
                            value = if (sampleShadowType == SampleShadowType.RSBlur) {
                                rsBlurShadowSpread.value
                            } else {
                                shadowSpread.value
                            },
                            onValueChange = {
                                if (sampleShadowType == SampleShadowType.RSBlur) {
                                    rsBlurShadowSpread = it.dp
                                } else {
                                    shadowSpread = it.dp
                                }
                            },
                            onValueChangeFinished = {
                                if (sampleShadowType == SampleShadowType.RSBlur) {
                                    shadowSpread = rsBlurShadowSpread
                                }
                            },
                            valueRange = -32.0F..32.0F,
                            steps = 64
                        )

                        var rsBlurShadowOffsetX by remember { mutableStateOf(shadowOffsetX) }

                        Text(
                            text = "OFFSET X:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Slider(
                            modifier = Modifier.fillMaxWidth(),
                            value = if (sampleShadowType == SampleShadowType.RSBlur) {
                                rsBlurShadowOffsetX.value
                            } else {
                                shadowOffsetX.value
                            },
                            onValueChange = {
                                if (sampleShadowType == SampleShadowType.RSBlur) {
                                    rsBlurShadowOffsetX = it.dp
                                } else {
                                    shadowOffsetX = it.dp
                                }
                            },
                            onValueChangeFinished = {
                                if (sampleShadowType == SampleShadowType.RSBlur) {
                                    shadowOffsetX = rsBlurShadowOffsetX
                                }
                            },
                            valueRange = -32.0F..32.0F,
                            steps = 64
                        )

                        var rsBlurShadowOffsetY by remember { mutableStateOf(shadowOffsetY) }

                        Text(
                            text = "OFFSET Y:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Slider(
                            modifier = Modifier.fillMaxWidth(),
                            value = if (sampleShadowType == SampleShadowType.RSBlur) {
                                rsBlurShadowOffsetY.value
                            } else {
                                shadowOffsetY.value
                            },
                            onValueChange = {
                                if (sampleShadowType == SampleShadowType.RSBlur) {
                                    rsBlurShadowOffsetY = it.dp
                                } else {
                                    shadowOffsetY = it.dp
                                }
                            },
                            onValueChangeFinished = {
                                if (sampleShadowType == SampleShadowType.RSBlur) {
                                    shadowOffsetY = rsBlurShadowOffsetY
                                }
                            },
                            valueRange = -32.0F..32.0F,
                            steps = 64
                        )

                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                resetShadowConfig()
                            }
                        ) {
                            Text(text = "RESET")
                        }
                    }
                }
            }
        }
    }

    if (isContentColorPickerVisible || isShadowColorPickerVisible) {
        ModalBottomSheet(
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            onDismissRequest = {
                isShadowColorPickerVisible = false
                isContentColorPickerVisible = false
            }
        ) {
            ClassicColorPicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = 300.dp)
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp),
                color = HsvColor.from(
                    if (isContentColorPickerVisible) {
                        contentColor
                    } else if (isShadowColorPickerVisible) {
                        shadowColor
                    } else {
                        Color.White
                    }
                )
            ) {
                val color = it.toColor()

                if (isContentColorPickerVisible) {
                    contentColor = color
                } else if (isShadowColorPickerVisible) {
                    shadowColor = color
                }
            }
        }
    }
}

enum class SampleConfigType {
    Common,
    Levitation,
    Press,
    Shadow
}

enum class LevitationShapeType {
    Rect,
    Circle,
    RoundedCorners,
    CutCorners
}

enum class SampleImageHighlightType {
    None,
    Full,
    Radial
}

enum class SamplePressType {
    Ranged,
    Full,
    None
}

enum class PressAnimationSpecType {
    Spring,
    Tween500
}

enum class SampleShadowType {
    RSBlur,
    SoftLayer,
    Elevation
}
