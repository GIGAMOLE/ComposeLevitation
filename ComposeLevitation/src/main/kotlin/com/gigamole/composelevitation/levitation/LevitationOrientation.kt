package com.gigamole.composelevitation.levitation

import com.gigamole.composelevitation.common.LevitationState

/**
 * Levitation orientations.
 *
 * @see LevitationConfig
 * @see LevitationState
 * @author GIGAMOLE
 */
enum class LevitationOrientation {

    /** Levitation can occur in all directions (both [Horizontal] and [Vertical]). */
    All,

    /** Levitation can occur only horizontally. */
    Horizontal,

    /** Levitation can occur only vertically. */
    Vertical
}
