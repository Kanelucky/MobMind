package org.kanelucky.mobmind.core.entity.ai.controller

import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.pathfinding.PPath

import org.kanelucky.mobmind.api.entity.IntelligentEntity
import org.kanelucky.mobmind.api.entity.ai.controller.Controller
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryTypes
import org.kanelucky.mobmind.core.entity.ai.executor.EntityControlHelper

/**
 * Ground walking movement controller. Walks the entity toward its current
 * move direction
 *
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 *
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
class WalkController : Controller {

    override fun control(entity: EntityCreature) {
        if (entity !is IntelligentEntity) return

        val target = entity.behaviorGroup.memoryStorage.get(MemoryTypes.MOVE_TARGET) ?: run {
            entity.navigator.reset()
            return
        }

        // Check chunk loaded
        val instance = entity.instance ?: return
        val chunk = instance.getChunkAt(target)
        if (chunk == null || !chunk.isLoaded) {
            EntityControlHelper.removeRouteTarget(entity)
            return
        }

        val state = entity.navigator.state
        if (state == PPath.State.TERMINATED ||
            state == PPath.State.INVALID ||
            state == PPath.State.BEST_EFFORT
        ) {
            try {
                entity.navigator.setPathTo(target)
            } catch (e: NullPointerException) {
                EntityControlHelper.removeRouteTarget(entity)
            }
        }
    }
}