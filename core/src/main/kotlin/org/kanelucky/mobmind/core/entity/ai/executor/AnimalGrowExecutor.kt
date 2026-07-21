package org.kanelucky.mobmind.core.entity.ai.executor

import net.minestom.server.entity.EntityCreature

import org.kanelucky.mobmind.api.entity.ai.Breedable
import org.kanelucky.mobmind.api.entity.ai.executor.BehaviorExecutor

/**
 * Grows a baby animal into an adult on execution.
 *
 * @author Kanelucky || Allay (https://github.com/AllayMC/Allay)
 */
class AnimalGrowExecutor : BehaviorExecutor {
    override fun execute(entity: EntityCreature): Boolean {
        if (entity is Breedable && entity.isBaby) {
            entity.isBaby = false
        }
        return false
    }
}