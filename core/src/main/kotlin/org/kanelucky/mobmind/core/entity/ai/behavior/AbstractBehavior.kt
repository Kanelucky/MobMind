package org.kanelucky.mobmind.core.entity.ai.behavior

import org.kanelucky.mobmind.api.entity.ai.behavior.Behavior
import org.kanelucky.mobmind.api.entity.ai.behavior.BehaviorState

/**
 * Base class for behaviors that provides default state management.
 *
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 *
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
abstract class AbstractBehavior : Behavior {
    private var state: BehaviorState = BehaviorState.STOP
    override fun getBehaviorState(): BehaviorState = state
    override fun setBehaviorState(state: BehaviorState) {
        this.state = state
    }
}