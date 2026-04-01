package org.kanelucky.mobmind.core.entity.ai.executor

import net.minestom.server.entity.EntityCreature

import org.kanelucky.mobmind.api.entity.IntelligentEntity
import org.kanelucky.mobmind.api.entity.ai.executor.BehaviorExecutor
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryTypes

import kotlin.random.Random

/**
 * Picks a random position within radius and sets it as the move target.
 * Completes when the entity reaches the target.
 *
 * @param radius max XZ distance from current position (default 10.0)
 *
 * @author Kanelucky
 */
class WanderExecutor(private val radius: Double = 10.0) : BehaviorExecutor {

    override fun onStart(entity: EntityCreature) {
        if (entity !is IntelligentEntity) return
        val target = entity.position.add(
            (Random.nextDouble() - 0.5) * radius * 2,
            0.0,
            (Random.nextDouble() - 0.5) * radius * 2
        )
        EntityControlHelper.setRouteTarget(entity, target)
        EntityControlHelper.setLookTarget(entity, target)
    }

    override fun execute(entity: EntityCreature): Boolean {
        if (entity !is IntelligentEntity) return false
        return entity.behaviorGroup.memoryStorage.get(MemoryTypes.MOVE_TARGET) != null
    }

    override fun onStop(entity: EntityCreature) {
        if (entity !is IntelligentEntity) return
        EntityControlHelper.removeRouteTarget(entity)
        EntityControlHelper.removeLookTarget(entity)
    }

    override fun onInterrupt(entity: EntityCreature) = onStop(entity)
}