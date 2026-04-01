package org.kanelucky.mobmind.core.entity.ai.controller

import net.minestom.server.entity.EntityCreature

import org.kanelucky.mobmind.api.entity.IntelligentEntity
import org.kanelucky.mobmind.api.entity.ai.controller.Controller
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryTypes

import kotlin.math.*

/**
 * Controls entity rotation: body yaw from movement direction,
 * head yaw and pitch from the look target.
 * <p>
 * If the entity implements {@link EntityHeadYawComponent}, head yaw is
 * set independently from body yaw. Otherwise, head yaw equals body yaw.
 *
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 *
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
class LookController : Controller {

    override fun control(entity: EntityCreature) {
        if (entity !is IntelligentEntity) return

        val lookTarget = entity.behaviorGroup.memoryStorage.get(MemoryTypes.LOOK_TARGET) ?: return

        val pos = entity.position
        val dx = lookTarget.x() - pos.x()
        val dz = lookTarget.z() - pos.z()

        val yaw = Math.toDegrees(atan2(-dx, dz)).toFloat()

        entity.setView(yaw, pos.pitch())
    }
}