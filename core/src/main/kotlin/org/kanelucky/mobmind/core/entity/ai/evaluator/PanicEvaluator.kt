package org.kanelucky.mobmind.core.entity.ai.evaluator

import net.minestom.server.entity.EntityCreature

import org.kanelucky.mobmind.api.entity.IntelligentEntity
import org.kanelucky.mobmind.api.entity.ai.evaluator.BehaviorEvaluator
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryTypes

/**
 * Returns true if the entity has panic ticks remaining
 *
 * @author Kanelucky
 */
class PanicEvaluator : BehaviorEvaluator {
    override fun evaluate(entity: EntityCreature): Boolean {
        if (entity !is IntelligentEntity) return false
        return (entity.behaviorGroup.memoryStorage.get(MemoryTypes.PANIC_TICKS) ?: 0) > 0
    }
}