package org.kanelucky.mobmind.core.entity.ai.controller

import net.minestom.server.entity.EntityCreature
import net.minestom.server.instance.block.Block

import org.kanelucky.mobmind.api.entity.ai.controller.Controller

import kotlin.random.Random

/**
 * Water bobbing controller. Simulates buoyancy by applying graduated
 * upward forces based on submersion depth, producing a natural
 * bobbing motion at the water surface
 *
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 *
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
class FluctuateController : Controller {

    companion object {
        private const val SUBMERGED_BUOYANCY = 0.05
        private const val SURFACE_BUOYANCY = 0.015
        private const val BUOYANCY_JITTER = 0.005
    }

    override fun control(entity: EntityCreature) {
        if (!hasWaterAt(entity, 0.0)) return

        val eyeInWater = hasWaterAt(entity, entity.eyeHeight.toDouble())
        var buoyancy = if (eyeInWater) SUBMERGED_BUOYANCY else SURFACE_BUOYANCY
        buoyancy += (Random.nextDouble() - 0.5) * 2 * BUOYANCY_JITTER

        entity.velocity = entity.velocity.add(0.0, buoyancy * 20, 0.0)
    }

    private fun hasWaterAt(entity: EntityCreature, heightOffset: Double): Boolean {
        val pos = entity.position
        val block = entity.instance?.getBlock(
            Math.floor(pos.x()).toInt(), Math.floor(pos.y() + heightOffset).toInt(), Math.floor(pos.z()).toInt()
        ) ?: return false
        return block == Block.WATER
    }
}