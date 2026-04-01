package org.kanelucky.mobmind.core.entity.ai.executor

import net.minestom.server.entity.EntityCreature

import org.kanelucky.mobmind.api.entity.IntelligentEntity
import org.kanelucky.mobmind.api.entity.ai.executor.BehaviorExecutor

import kotlin.random.Random

/**
 * Makes the entity stand still for a random duration.
 *
 * @param minTicks minimum idle duration in ticks (default 20)
 * @param maxTicks maximum idle duration in ticks (default 60)
 *
 * @author Kanelucky
 */
class IdleExecutor(
    private val minTicks: Int = 20,
    private val maxTicks: Int = 60
) : BehaviorExecutor {

    private var ticksLeft = 0

    override fun onStart(entity: EntityCreature) {
        ticksLeft = Random.nextInt(minTicks, maxTicks)
        (entity as? IntelligentEntity)?.let { EntityControlHelper.removeRouteTarget(it) }
    }

    override fun execute(entity: EntityCreature): Boolean = --ticksLeft > 0
}