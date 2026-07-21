package org.kanelucky.mobmind.core.entity.ai.evaluator

import net.minestom.server.entity.EntityCreature

import org.kanelucky.mobmind.api.entity.IntelligentEntity
import org.kanelucky.mobmind.api.entity.ai.evaluator.BehaviorEvaluator
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryType

/**
 * Checks if the time elapsed since a timed value is within
 * the range {@code [min, max]}
 *
 * @author Kanelucky || Allay (https://github.com/AllayMC/Allay)
 */
class PassByTimeEvaluator(
    private val timedMemory: MemoryType<Long>,
    private val min: Long,
    private val max: Long
) : BehaviorEvaluator {
    override fun evaluate(entity: EntityCreature): Boolean {
        if (entity !is IntelligentEntity) return false
        val lastTime = entity.behaviorGroup.memoryStorage.get(timedMemory) ?: return false
        val elapsed = entity.aliveTicks - lastTime
        return elapsed in min..max
    }
}