package org.kanelucky.mobmind.core.entity.ai.behavior

import org.kanelucky.mobmind.api.entity.ai.behavior.Behavior
import org.kanelucky.mobmind.api.entity.ai.behavior.BehaviorState

/**
 * Base class for behaviors that provides default state management.
 *
 * @author Kanelucky || Allay (https://github.com/AllayMC/Allay)
 */
abstract class AbstractBehavior : Behavior {
    private var state: BehaviorState = BehaviorState.STOP
    override fun getBehaviorState(): BehaviorState = state
    override fun setBehaviorState(state: BehaviorState) {
        this.state = state
    }
}