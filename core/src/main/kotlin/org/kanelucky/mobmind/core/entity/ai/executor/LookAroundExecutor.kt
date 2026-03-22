package org.kanelucky.mobmind.core.entity.ai.executor

import net.minestom.server.entity.EntityCreature
import org.kanelucky.mobmind.api.entity.ai.executor.BehaviorExecutor
import kotlin.random.Random

/**
 * Makes the entity look around in a random direction for a random duration.
 *
 * @param minTicks minimum ticks to look in one direction (default 20)
 * @param maxTicks maximum ticks to look in one direction (default 60)
 *
 * @author Kanelucky
 */
class LookAroundExecutor(
    private val minTicks: Int = 20,
    private val maxTicks: Int = 60
) : BehaviorExecutor {

    private var ticksLeft = 0

    override fun onStart(entity: EntityCreature) {
        ticksLeft = Random.nextInt(minTicks, maxTicks)
        entity.teleport(entity.position.withYaw(randomYaw()))
    }

    override fun execute(entity: EntityCreature): Boolean {
        return --ticksLeft > 0
    }

    /**
     * Override to customize yaw generation.
     */
    protected open fun randomYaw(): Float = Random.nextFloat() * 360f - 180f
}