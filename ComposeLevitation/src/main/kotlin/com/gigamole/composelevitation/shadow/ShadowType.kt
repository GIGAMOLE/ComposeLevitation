@file:Suppress("DEPRECATION")

package com.gigamole.composelevitation.shadow

import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.View
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import com.gigamole.composeshadowsplus.common.ShadowsPlusDefaults
import com.gigamole.composeshadowsplus.common.ShadowsPlusType
import com.gigamole.composeshadowsplus.rsblur.RSBlurShadowDefaults
import com.gigamole.composeshadowsplus.rsblur.rsBlurShadow
import com.gigamole.composeshadowsplus.softlayer.SoftLayerShadowContainer
import com.gigamole.composeshadowsplus.softlayer.softLayerShadow
import android.graphics.Paint as NativePaint

/**
 * Levitation shadow types.
 *
 * @author GIGAMOLE
 */
sealed class ShadowType {

    /**
     * Elevation shadow.
     *
     * The ComposeLevitation library advises against using elevation shadow due to a rendering issue where the shadow appears cropped and only visible at the content
     * border, creating an empty rectangle below the surface.
     *
     * @property isClipped Indicates whether the content drawing clips to the shape or not.
     * @property ambientColor The elevation ambient color.
     * @property spotColor The elevation spot color.
     */
    data class Elevation(
        val isClipped: Boolean = ShadowDefaults.Type.Elevation.IsClipped,
        val ambientColor: Color = ShadowDefaults.Type.Elevation.AmbientColor,
        val spotColor: Color = ShadowDefaults.Type.Elevation.SpotColor
    ) : ShadowType()

    /** Custom shadows (bridge to: [ShadowsPlusType]). */
    sealed class ShadowsPlus : ShadowType() {

        abstract val color: Color
        abstract val offset: DpOffset
        abstract val spread: Dp

        /**
         * A [NativePaint.setShadowLayer]/[View.LAYER_TYPE_SOFTWARE] custom shadow.
         *
         * You must use it with [SoftLayerShadowContainer].
         *
         * @property color The shadow color.
         * @property offset The shadow offset. Can be positive or negative.
         * @property spread The shadow spread. Can be positive or negative.
         * @see Modifier.softLayerShadow
         */
        data class SoftLayer(
            override val color: Color = ShadowsPlusDefaults.ShadowColor,
            override val offset: DpOffset = ShadowsPlusDefaults.ShadowOffset,
            override val spread: Dp = ShadowsPlusDefaults.ShadowSpread
        ) : ShadowsPlus()

        /**
         * A [RenderScript]/[ScriptIntrinsicBlur] custom shadow.
         *
         * @property color The shadow color.
         * @property offset The shadow offset. Can be positive or negative.
         * @property spread The shadow spread. Can be positive or negative.
         * @property isRadiusAligned Indicates whether the shadow radius is exponentially aligned.
         * @see Modifier.rsBlurShadow
         */
        data class RSBlur(
            override val color: Color = ShadowsPlusDefaults.ShadowColor,
            override val offset: DpOffset = ShadowsPlusDefaults.ShadowOffset,
            override val spread: Dp = ShadowsPlusDefaults.ShadowSpread,
            val isRadiusAligned: Boolean = RSBlurShadowDefaults.RSBlurShadowAlignRadius
        ) : ShadowsPlus()
    }
}