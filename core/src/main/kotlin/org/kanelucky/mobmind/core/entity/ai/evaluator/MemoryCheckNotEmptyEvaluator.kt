package org.kanelucky.mobmind.core.entity.ai.evaluator

import net.minestom.server.entity.EntityCreature

import org.kanelucky.mobmind.api.entity.IntelligentEntity
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryType
import org.kanelucky.mobmind.api.entity.ai.evaluator.BehaviorEvaluator

/**
 * Checks if a specific memory type has a value stored (is not empty)
 *
 * @author Kanelucky || Allay (https://github.com/AllayMC/Allay)
 */
class MemoryCheckNotEmptyEvaluator(private val type: MemoryType<*>) : BehaviorEvaluator {
    override fun evaluate(entity: EntityCreature): Boolean {
        if (entity !is IntelligentEntity) return false
        return entity.behaviorGroup.memoryStorage.get(type) != null
    }
}