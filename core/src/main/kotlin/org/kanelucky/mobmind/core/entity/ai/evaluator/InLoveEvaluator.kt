package org.kanelucky.mobmind.core.entity.ai.evaluator

import net.minestom.server.entity.EntityCreature

import org.kanelucky.mobmind.api.entity.IntelligentEntity
import org.kanelucky.mobmind.api.entity.ai.Breedable
import org.kanelucky.mobmind.api.entity.ai.evaluator.BehaviorEvaluator
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryTypes

/**
 * Returns true if the entity is in love and ready to breed
 *
 * @author Kanelucky
 */
class InLoveEvaluator : BehaviorEvaluator {
    override fun evaluate(entity: EntityCreature): Boolean {
        if (entity !is IntelligentEntity) return false
        if (entity !is Breedable) return false
        if (entity.isBaby) return false
        if (entity.breedCooldown > 0) return false
        return entity.behaviorGroup.memoryStorage.get(MemoryTypes.IS_IN_LOVE) == true
    }
}