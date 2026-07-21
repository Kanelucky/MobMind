package org.kanelucky.mobmind.core.entity.ai.route.posevaluator

import net.minestom.server.entity.EntityCreature
import net.minestom.server.instance.block.Block

/**
 * Evaluates ground blocks during pathfinding.
 *
 * @author Kanelucky || Allay (https://github.com/AllayMC/Allay)
 */
fun interface GroundPosEvaluator {
    fun evaluate(entity: EntityCreature, block: Block, x: Int, y: Int, z: Int): Boolean
}