@file:OptIn(ExperimentalTextApi::class)

package com.gigamole.composelevitation.sample

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.graphics.scale
import com.gigamole.composelevitation.common.rememberLevitationState
import com.gigamole.composelevitation.levitation.LevitationConfig
import com.gigamole.composelevitation.levitation.LevitationContainer
import com.gigamole.composelevitation.press.PressConfig
import com.gigamole.composelevitation.press.PressType
import com.gigamole.composelevitation.shadow.ShadowConfig
import com.gigamole.composelevitation.shadow.ShadowType
import com.gigamole.composeshadowsplus.softlayer.SoftLayerShadowContainer
import kotlin.math.abs

private val sampleHolographicColors = listOf(
    Color(0xFF9FDAFF),
    Color(0xFFFEF1A5),
    Color(0xFFFBA1C9),
    Color(0xFFAB90D3),
    Color(0xFF9FDAFF),
    Color(0xFFFBB466),
)
private val sampleHolographicAltColors = listOf(
    Color(0xFF2AD0CA),
    Color(0xFFE1F664),
    Color(0xFFFEB0FE),
    Color(0xFFABB3FC),
    Color(0xFF5DF7A4),
    Color(0xFF58C4F6),
)

private val sampleHolographicMetalColors = listOf(
    Color.White,
    Color.Black,
    Color.White,
    Color.Black,
    Color.White,
    Color.Black,
    Color.White,
    Color.Black,
    Color.White
)

private val sampleBorderHighlightColors = listOf(
    Color.White.copy(alpha = 0.5F),
    Color.White.copy(alpha = 0.0F)
)
private val sampleBorderHighlightSoftColors = listOf(
    Color.White.copy(alpha = 0.3F),
    Color.White.copy(alpha = 0.0F)
)

private val sampleBorderHighdarkColors = listOf(
    Color.Black.copy(alpha = 0.5F),
    Color.Black.copy(alpha = 0.0F)
)
private val sampleBorderHighdarkSoftColors = listOf(
    Color.Black.copy(alpha = 0.3F),
    Color.Black.copy(alpha = 0.0F)
)

@Suppress("unused")
@Composable
fun MainScreenHolographicContent() {
    val mainShape = RoundedCornerShape(size = 24.dp)

    // Gathering working bitmaps.
    val noiseImageBitmap = ImageBitmap.imageResource(id = R.drawable.noise)
    val scale = with(LocalDensity.current) { 65.dp.roundToPx() }
    val logoImageBitmap = ImageBitmap.imageResource(id = R.drawable.logo).let {
        // Adjusting logo size.
        it.asAndroidBitmap().scale(
            width = it.width * scale / it.height,
            height = scale
        )
    }.asImageBitmap()

    // Prepare LevitationState.
    val state = rememberLevitationState(
        levitationConfig = LevitationConfig(
            shape = mainShape,
            pivot = Offset(
                x = 0.5F,
                y = 0.4F
            ),
            cameraDistance = 7.0F
        ),
        pressConfig = PressConfig(
            type = PressType.Ranged(),
            downscale = 0.04F,
            pressAnimationSpec = tween(durationMillis = 500),
            isAwaitPressAnimation = true
        ),
        shadowConfig = ShadowConfig(
            type = ShadowType.ShadowsPlus.SoftLayer(
                offset = DpOffset.Zero,
                color = Color(0xFF61707F).copy(alpha = 0.4F),
                spread = 0.dp
            ),
            degreeMultiplier = 1.0F,
            downscaleMultiplier = 1.0F,
            cameraDistanceMultiplier = 0.9F,
            translationOffset = DpOffset(
                x = 2.dp,
                y = 2.dp
            ),
            radius = 1.dp,
        )
    )

    // Modifier for a lighter side highlight.
    fun Modifier.highlightBorder(
        shape: Shape,
        start: Offset,
        end: Offset,
        isStart: Boolean,
        isBig: Boolean = false,
        isSoft: Boolean = false
    ): Modifier =
        border(
            width = if (isBig) {
                2.dp
            } else {
                1.dp
            },
            shape = shape,
            brush = Brush.linearGradient(
                colors = if (isSoft) {
                    sampleBorderHighlightSoftColors
                } else {
                    sampleBorderHighlightColors
                }.let {
                    if (isStart) {
                        it
                    } else {
                        it.reversed()
                    }
                },
                start = start,
                end = end,
                tileMode = TileMode.Clamp
            )
        )

    // Modifier for a darker side highlight.
    fun Modifier.highdarkBorder(
        shape: Shape,
        start: Offset,
        end: Offset,
        isStart: Boolean,
        isBig: Boolean = false,
        isSoft: Boolean = false
    ): Modifier =
        border(
            width = if (isBig) {
                2.dp
            } else {
                1.dp
            },
            shape = shape,
            brush = Brush.linearGradient(
                colors = if (isSoft) {
                    sampleBorderHighdarkSoftColors
                } else {
                    sampleBorderHighdarkColors
                }.let {
                    if (isStart) {
                        it
                    } else {
                        it.reversed()
                    }
                },
                start = start,
                end = end,
                tileMode = TileMode.Clamp
            )
        )

    SoftLayerShadowContainer {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.scene),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )

            LevitationContainer(
                modifier = Modifier
                    .width(width = 250.dp)
                    .height(height = 270.dp),
                state = state
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = Color.White,
                            shape = mainShape
                        )
                        // Applying card container highlights.
                        .highlightBorder(
                            shape = mainShape,
                            start = Offset.Zero,
                            end = state.size.center.times(operand = 1.75F),
                            isStart = true,
                            isBig = true
                        )
                        .highdarkBorder(
                            shape = mainShape,
                            start = state.size.center.times(operand = 0.25F),
                            end = Offset(
                                x = state.size.width,
                                y = state.size.height
                            ),
                            isStart = false,
                            isBig = true
                        )
                        .drawWithContent {
                            val pivot = Offset(
                                x = size.center.x,
                                y = size.height * 0.4F
                            )
                            val degreeFraction = maxOf(abs(state.degreeProgress.x), abs(state.degreeProgress.y))

                            // Drawing main holographic gradient.
                            drawRect(
                                brush = Brush.radialGradient(
                                    colorStops = sampleHolographicColors.let {
                                        arrayOf(
                                            (0.0F + 0.1F * degreeFraction) to it[0],
                                            (0.2F + 0.1F * degreeFraction) to it[1],
                                            (0.4F + 0.08F * degreeFraction) to it[2],
                                            (0.6F + 0.08F * degreeFraction) to it[3],
                                            (0.8F + 0.06F * degreeFraction) to it[4],
                                            (1.0F + 0.06F * degreeFraction) to it[5],
                                        )
                                    },
                                    center = Offset(
                                        x = (size.width * 0.5F) + (size.width * 0.25F * -state.degreeProgress.x * state.pressFraction),
                                        y = (size.height * 0.4F) + (size.height * 0.2F * -state.degreeProgress.y * state.pressFraction)
                                    ),
                                    radius = (size.width * 0.75F) + (size.height * 0.6F * degreeFraction * state.pressFraction),
                                    tileMode = TileMode.Mirror
                                ),
                            )
                            // Drawing support holographic gradient.
                            drawRect(
                                brush = Brush.radialGradient(
                                    colorStops = sampleHolographicAltColors.let {
                                        arrayOf(
                                            (0.0F + 0.1F * degreeFraction) to it[0],
                                            (0.2F + 0.1F * degreeFraction) to it[1],
                                            (0.4F + 0.08F * degreeFraction) to it[2],
                                            (0.6F + 0.08F * degreeFraction) to it[3],
                                            (0.8F + 0.06F * degreeFraction) to it[4],
                                            (1.0F + 0.06F * degreeFraction) to it[5],
                                        )
                                    },
                                    center = Offset(
                                        x = (size.width * 0.5F) + (size.width * 0.5F * state.degreeProgress.x * state.pressFraction),
                                        y = (size.height * 0.6F) + (size.height * 0.5F * state.degreeProgress.y * state.pressFraction)
                                    ),
                                    radius = (size.height * 0.9F) - (size.height * 0.35F * (1.0F - degreeFraction) * state.pressFraction),
                                    tileMode = TileMode.Clamp
                                ),
                                alpha = 0.5F + (0.2F * degreeFraction),
                                blendMode = BlendMode.Overlay
                            )

                            val rawSweepFraction = (state.degreeProgress.x + state.degreeProgress.y) * state.pressFraction
                            val sweepFraction = rawSweepFraction * 0.3F

                            // Skewing sweep gradients on different angles.
                            scale(
                                scaleX = 1.5F + 0.3F * state.degreeProgress.x * state.pressFraction,
                                scaleY = 1.5F - 0.3F * state.degreeProgress.y * state.pressFraction,
                                pivot = pivot
                            ) {
                                // Rotating sweep gradients to start diagonally.
                                rotate(
                                    degrees = 45.0F + 8.0F * rawSweepFraction,
                                    pivot = pivot
                                ) {
                                    // Drawing main sweep gradient.
                                    drawRect(
                                        topLeft = size.center.times(operand = -2.0F),
                                        size = size.times(operand = 3.0F),
                                        brush = Brush.sweepGradient(
                                            colorStops = sampleHolographicMetalColors.let {
                                                arrayOf(
                                                    0.0F to it[0],
                                                    (0.15F + (0.1F * sweepFraction)) to it[1],
                                                    (0.25F + (0.08F * sweepFraction)) to it[2],
                                                    (0.4F + (0.05F * sweepFraction)) to it[3],
                                                    (0.5F + (0.05F * sweepFraction)) to it[4],
                                                    (0.55F + (0.03F * sweepFraction)) to it[5],
                                                    (0.76F + (0.1F * sweepFraction)) to it[6],
                                                    (0.87F + (0.05F * sweepFraction)) to it[7],
                                                    1.0F to it[8],
                                                )
                                            },
                                            center = pivot
                                        ),
                                        alpha = 1.0F - (0.25F * (1.0F - degreeFraction) * state.pressFraction),
                                        blendMode = BlendMode.Difference
                                    )

                                    // Drawing support sweep gradient.
                                    drawRect(
                                        topLeft = size.center.times(operand = -2.0F),
                                        size = size.times(operand = 3.0F),
                                        brush = Brush.sweepGradient(
                                            colorStops = sampleHolographicMetalColors.let {
                                                arrayOf(
                                                    0.0F to it[0],
                                                    (0.12F + (0.12F * sweepFraction)) to it[1],
                                                    (0.23F + (0.05F * sweepFraction)) to it[2],
                                                    (0.38F + (0.1F * sweepFraction)) to it[3],
                                                    (0.52F + (0.12F * sweepFraction)) to it[4],
                                                    (0.6F + (0.1F * sweepFraction)) to it[5],
                                                    (0.75F + (0.05F * sweepFraction)) to it[6],
                                                    (0.86F + (0.07F * sweepFraction)) to it[7],
                                                    1.0F to it[8],
                                                )
                                            },
                                            center = pivot
                                        ),
                                        alpha = 1.0F - (0.2F * (1.0F - degreeFraction) * state.pressFraction),
                                        blendMode = BlendMode.Screen
                                    )
                                }
                            }
                            drawIntoCanvas {
                                // Drawing noise bitmap as a Paint with tile mode support.
                                it.nativeCanvas.drawPaint(
                                    Paint()
                                        .apply {
                                            alpha = 0.4F
                                            blendMode = BlendMode.Overlay
                                        }
                                        .asFrameworkPaint()
                                        .apply {
                                            isAntiAlias = true
                                            isDither = true

                                            shader = ImageShader(
                                                image = noiseImageBitmap,
                                                tileModeX = TileMode.Repeated,
                                                tileModeY = TileMode.Repeated
                                            )
                                        }
                                )
                            }
                            drawContent()
                        }
                        .padding(all = 9.dp)
                        // Applying card outline outer border highlights.
                        .highdarkBorder(
                            shape = RoundedCornerShape(size = 15.dp),
                            start = Offset(
                                x = 0.0F,
                                y = 0.0F
                            ),
                            end = state.size.center.times(operand = 1.65F),
                            isStart = true
                        )
                        .highlightBorder(
                            shape = RoundedCornerShape(size = 15.dp),
                            start = state.size.center.times(operand = 0.35F),
                            end = Offset(
                                x = state.size.width * 0.85F,
                                y = state.size.height * 0.85F
                            ),
                            isStart = false
                        )
                        .padding(all = 1.dp)
                        .highdarkBorder(
                            shape = RoundedCornerShape(size = 14.dp),
                            start = Offset(
                                x = 0.0F,
                                y = 0.0F
                            ),
                            end = state.size.center.times(operand = 1.65F),
                            isStart = true
                        )
                        .highlightBorder(
                            shape = RoundedCornerShape(size = 14.dp),
                            start = state.size.center.times(operand = 0.35F),
                            end = Offset(
                                x = state.size.width * 0.85F,
                                y = state.size.height * 0.85F
                            ),
                            isStart = false
                        )
                ) {
                    // Modifier for a black outlines with a spot highlighter.
                    fun Modifier.spotHighlighter(): Modifier =
                        graphicsLayer {
                            compositingStrategy = CompositingStrategy.Offscreen
                        }.drawWithContent {
                            drawContent()
                            drawIntoCanvas {
                                drawRect(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.4F),
                                            Color.Transparent
                                        ),
                                        center = Offset(
                                            x = size.width * state.offsetProgress.x * state.pressFraction,
                                            y = size.height * state.offsetProgress.y * state.pressFraction
                                        ),
                                        radius = size.height * 1.25F,
                                        tileMode = TileMode.Clamp,
                                    ),
                                    alpha = 0.8F - 0.2F * state.offsetProgress.y * state.pressFraction,
                                    blendMode = BlendMode.SrcAtop
                                )
                            }
                        }

                    // Recreating card structure and apply spot highlighter to the black outlines.
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .spotHighlighter()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(
                                    width = 6.dp,
                                    color = Color.Black,
                                    shape = RoundedCornerShape(size = 14.dp)
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(weight = 1.0F)
                                    .padding(all = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(size = 100.dp)
                                        .background(
                                            color = Color.Black,
                                            shape = RoundedCornerShape(size = 8.dp)
                                        )
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(height = 6.dp)
                                    .background(color = Color.Black)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp)
                                    .height(height = 32.dp)
                            )
                        }
                    }

                    // Main card content.
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(weight = 1.0F)
                                .padding(
                                    top = 5.dp,
                                    start = 5.dp,
                                    end = 5.dp,
                                    bottom = 4.dp
                                )
                                // Applying top card content border highlights.
                                .highlightBorder(
                                    shape = RoundedCornerShape(
                                        topStart = 9.dp,
                                        topEnd = 9.dp
                                    ),
                                    start = Offset(
                                        x = state.size.width * 0.1F,
                                        y = state.size.height * 0.1F
                                    ),
                                    end = state.size.center.times(operand = 1.45F),
                                    isStart = true,
                                    isSoft = true
                                )
                                .highdarkBorder(
                                    shape = RoundedCornerShape(
                                        topStart = 9.dp,
                                        topEnd = 9.dp
                                    ),
                                    start = state.size.center.times(operand = 0.45F),
                                    end = Offset(
                                        x = state.size.width * 0.9F,
                                        y = state.size.height * 0.9F
                                    ),
                                    isStart = false,
                                    isSoft = true
                                )
                                .padding(all = 1.dp)
                                .highlightBorder(
                                    shape = RoundedCornerShape(
                                        topStart = 8.dp,
                                        topEnd = 8.dp
                                    ),
                                    start = Offset(
                                        x = state.size.width * 0.1F,
                                        y = state.size.height * 0.1F
                                    ),
                                    end = state.size.center.times(operand = 1.45F),
                                    isStart = true,
                                    isSoft = true
                                )
                                .highdarkBorder(
                                    shape = RoundedCornerShape(
                                        topStart = 8.dp,
                                        topEnd = 8.dp
                                    ),
                                    start = state.size.center.times(operand = 0.45F),
                                    end = Offset(
                                        x = state.size.width * 0.9F,
                                        y = state.size.height * 0.9F
                                    ),
                                    isStart = false,
                                    isSoft = true
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(bottom = 6.dp)
                                    .size(size = 102.dp)
                                    // Applying center logo black outline border highlights.
                                    .highdarkBorder(
                                        shape = RoundedCornerShape(size = 9.dp),
                                        start = Offset.Zero,
                                        end = Offset(
                                            x = state.size.width * 0.32F,
                                            y = state.size.height * 0.24F
                                        ),
                                        isStart = true
                                    )
                                    .highlightBorder(
                                        shape = RoundedCornerShape(size = 9.dp),
                                        start = Offset(
                                            x = state.size.width * 0.13F,
                                            y = state.size.height * 0.11F
                                        ),
                                        end = Offset(
                                            x = state.size.width * 0.3F,
                                            y = state.size.height * 0.28F
                                        ),
                                        isStart = false
                                    )
                                    .padding(all = 1.dp)
                                    .highdarkBorder(
                                        shape = RoundedCornerShape(size = 8.dp),
                                        start = Offset.Zero,
                                        end = Offset(
                                            x = state.size.width * 0.32F,
                                            y = state.size.height * 0.24F
                                        ),
                                        isStart = true
                                    )
                                    .highlightBorder(
                                        shape = RoundedCornerShape(size = 8.dp),
                                        start = Offset(
                                            x = state.size.width * 0.13F,
                                            y = state.size.height * 0.11F
                                        ),
                                        end = Offset(
                                            x = state.size.width * 0.3F,
                                            y = state.size.height * 0.28F
                                        ),
                                        isStart = false
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                val offset = with(LocalDensity.current) { 1.dp.toPx() }

                                // Applying sweep gradient to the logo and draw it with a border highlights.
                                Box(
                                    modifier = Modifier
                                        .size(size = 67.dp)
                                        .graphicsLayer {
                                            compositingStrategy = CompositingStrategy.Offscreen
                                        }
                                        .drawWithContent {
                                            val degreeFraction = maxOf(abs(state.degreeProgress.x), abs(state.degreeProgress.y))
                                            val rawSweepFraction = (state.degreeProgress.x + state.degreeProgress.y) * state.pressFraction
                                            val sweepFraction = rawSweepFraction * 0.5F

                                            drawIntoCanvas { canvas ->
                                                translate(
                                                    left = center.x - logoImageBitmap.width / 2.0F,
                                                    top = center.y - logoImageBitmap.height / 2.0F
                                                ) {
                                                    drawImage(
                                                        image = logoImageBitmap,
                                                        colorFilter = ColorFilter.tint(color = Color.White)
                                                    )
                                                    drawRect(
                                                        brush = Brush.sweepGradient(
                                                            colorStops = sampleHolographicMetalColors.let {
                                                                arrayOf(
                                                                    0.0F to it[0],
                                                                    (0.15F + (0.1F * sweepFraction)) to it[1],
                                                                    (0.25F + (0.08F * sweepFraction)) to it[2],
                                                                    (0.4F + (0.05F * sweepFraction)) to it[3],
                                                                    (0.5F + (0.05F * sweepFraction)) to it[4],
                                                                    (0.55F + (0.03F * sweepFraction)) to it[5],
                                                                    (0.76F + (0.1F * sweepFraction)) to it[6],
                                                                    (0.87F + (0.05F * sweepFraction)) to it[7],
                                                                    1.0F to it[8],
                                                                )
                                                            },
                                                            center = Offset(
                                                                x = size.width * 0.1F,
                                                                y = -size.height * 0.15F
                                                            )
                                                        ),
                                                        alpha = 0.8F - (0.4F * (1.0F - degreeFraction) * state.pressFraction),
                                                        blendMode = BlendMode.SrcAtop
                                                    )
                                                }
                                                canvas.saveLayer(
                                                    bounds = size.toRect(),
                                                    paint = Paint().apply {
                                                        alpha = 0.4F
                                                    }
                                                )
                                                translate(
                                                    left = center.x - logoImageBitmap.width / 2.0F,
                                                    top = center.y - logoImageBitmap.height / 2.0F
                                                ) {
                                                    drawImage(
                                                        image = logoImageBitmap,
                                                        topLeft = Offset(
                                                            x = -offset,
                                                            y = -offset
                                                        )
                                                    )
                                                    drawImage(
                                                        image = logoImageBitmap,
                                                        topLeft = Offset(
                                                            x = offset,
                                                            y = -offset
                                                        )
                                                    )
                                                    drawImage(
                                                        image = logoImageBitmap,
                                                        topLeft = Offset(
                                                            x = offset,
                                                            y = offset
                                                        )
                                                    )
                                                    drawImage(
                                                        image = logoImageBitmap,
                                                        topLeft = Offset(
                                                            x = -offset,
                                                            y = offset
                                                        )
                                                    )
                                                    canvas.nativeCanvas.drawPaint(
                                                        Paint()
                                                            .apply {
                                                                shader = LinearGradientShader(
                                                                    colors = listOf(
                                                                        Color.White,
                                                                        Color.Black
                                                                    ),
                                                                    from = Offset(
                                                                        x = size.width * 0.3F,
                                                                        y = size.height * 0.3F
                                                                    ),
                                                                    to = Offset(
                                                                        x = size.width * 0.7F,
                                                                        y = size.height * 0.7F
                                                                    ),
                                                                )
                                                                blendMode = BlendMode.SrcAtop
                                                            }
                                                            .asFrameworkPaint()
                                                    )
                                                }
                                                canvas.restore()
                                            }
                                        }
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 5.dp,
                                    end = 5.dp,
                                    bottom = 5.dp
                                )
                                // Applying border highlights to the bottom card content panel.
                                .highlightBorder(
                                    shape = RoundedCornerShape(
                                        bottomStart = 9.dp,
                                        bottomEnd = 9.dp
                                    ),
                                    start = Offset(
                                        x = state.size.width * 0.1F,
                                        y = state.size.height * 0.8F
                                    ),
                                    end = Offset(
                                        x = state.size.width * 0.2F,
                                        y = state.size.height * 0.87F
                                    ),
                                    isStart = true,
                                    isSoft = true
                                )
                                .highdarkBorder(
                                    shape = RoundedCornerShape(
                                        bottomStart = 9.dp,
                                        bottomEnd = 9.dp
                                    ),
                                    start = Offset(
                                        x = state.size.center.x * 0.9F,
                                        y = state.size.height * 0.85F
                                    ),
                                    end = Offset(
                                        x = state.size.width * 0.9F,
                                        y = state.size.height * 0.9F
                                    ),
                                    isStart = false,
                                    isSoft = true
                                )
                                .padding(all = 1.dp)
                                .highlightBorder(
                                    shape = RoundedCornerShape(
                                        bottomStart = 8.dp,
                                        bottomEnd = 8.dp
                                    ),
                                    start = Offset(
                                        x = state.size.width * 0.1F,
                                        y = state.size.height * 0.8F
                                    ),
                                    end = Offset(
                                        x = state.size.width * 0.2F,
                                        y = state.size.height * 0.87F
                                    ),
                                    isStart = true,
                                    isSoft = true
                                )
                                .highdarkBorder(
                                    shape = RoundedCornerShape(
                                        bottomStart = 8.dp,
                                        bottomEnd = 8.dp
                                    ),
                                    start = Offset(
                                        x = state.size.center.x * 0.9F,
                                        y = state.size.height * 0.85F
                                    ),
                                    end = Offset(
                                        x = state.size.width * 0.9F,
                                        y = state.size.height * 0.9F
                                    ),
                                    isStart = false,
                                    isSoft = true
                                )
                                .height(height = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val heightPx = with(LocalDensity.current) { 20.dp.toPx() }
                            val widthPx = with(LocalDensity.current) { 18.dp.toPx() }
                            val strokePx = with(LocalDensity.current) { 2.dp.toPx() }

                            // Drawing each letter separately to apply border highlight to each.
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(
                                    space = 2.dp,
                                    alignment = Alignment.CenterHorizontally
                                ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                "GIGAMOLE".forEach {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            modifier = Modifier.padding(bottom = 1.dp),
                                            text = it.toString(),
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                brush = Brush.linearGradient(
                                                    colors = sampleBorderHighlightSoftColors.asReversed(),
                                                    start = Offset(
                                                        x = 0.0F,
                                                        y = 0.0F
                                                    ),
                                                    end = Offset(
                                                        x = widthPx,
                                                        y = heightPx
                                                    ),
                                                    tileMode = TileMode.Clamp
                                                ),
                                                drawStyle = Stroke(width = strokePx),
                                            ),
                                            fontFamily = FontFamilyDaysOne
                                        )

                                        Text(
                                            modifier = Modifier.padding(bottom = 1.dp),
                                            text = it.toString(),
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                brush = Brush.linearGradient(
                                                    colors = sampleBorderHighdarkSoftColors,
                                                    start = Offset(
                                                        x = 0.0F,
                                                        y = 0.0F
                                                    ),
                                                    end = Offset(
                                                        x = widthPx,
                                                        y = heightPx
                                                    ),
                                                    tileMode = TileMode.Clamp
                                                ),
                                                drawStyle = Stroke(width = strokePx),
                                            ),
                                            fontFamily = FontFamilyDaysOne
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Recreating the card content structure again to draw a card title with a spot highlighter.
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .spotHighlighter()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(weight = 1.0F)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(height = 6.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp)
                                    .height(height = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(
                                        space = 2.dp,
                                        alignment = Alignment.CenterHorizontally
                                    ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    "GIGAMOLE".forEach {
                                        Text(
                                            modifier = Modifier.padding(bottom = 1.dp),
                                            text = it.toString(),
                                            style = MaterialTheme.typography.titleLarge,
                                            fontFamily = FontFamilyDaysOne
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
