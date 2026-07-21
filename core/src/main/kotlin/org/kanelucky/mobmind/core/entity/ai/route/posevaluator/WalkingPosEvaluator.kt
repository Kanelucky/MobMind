package org.kanelucky.mobmind.core.entity.ai.route.posevaluator

import net.minestom.server.entity.EntityCreature
import net.minestom.server.instance.block.Block

/**
 * Evaluates walkable positions for ground entities.
 * Avoids lava, cactus, and other hazardous blocks.
 *
 * @author Kanelucky || Allay (https://github.com/AllayMC/Allay)
 */
class WalkingPosEvaluator : GroundPosEvaluator {
    override fun evaluate(entity: EntityCreature, block: Block, x: Int, y: Int, z: Int): Boolean {
        if (block == Block.LAVA) return false
        if (block == Block.CACTUS) return false
        if (block == Block.WATER) return true
        return block.isSolid
    }
}