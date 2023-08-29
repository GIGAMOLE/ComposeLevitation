package com.gigamole.composelevitation.press

import com.gigamole.composelevitation.levitation.LevitationConfig

/**
 * Levitation press types.
 *
 * @author GIGAMOLE
 */
sealed class PressType {

    /**
     * Press type for ranged (interpolated) downscale press between [start] and [stop] fractions. Range from 0.0F to 1.0F.
     *
     * @property start The start fraction (from pivot point).
     * @property stop The stop fraction (from content bounds).
     * @see LevitationConfig.pivot
     */
    data class Ranged(
        val start: Float = PressDefaults.Type.Ranged.Start,
        val stop: Float = PressDefaults.Type.Ranged.Stop
    ) : PressType()

    /** Press type for automatic full downscale press. */
    data object Full : PressType()

    /** Press type for none downscale press. */
    data object None : PressType()
}
