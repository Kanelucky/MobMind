package org.kanelucky.mobmind.core.entity.ai.route.posevaluator

import net.minestom.server.entity.EntityCreature
import net.minestom.server.instance.block.Block

/**
 * Evaluates ground blocks during pathfinding.
 *
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 *
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
fun interface GroundPosEvaluator {
    fun evaluate(entity: EntityCreature, block: Block, x: Int, y: Int, z: Int): Boolean
}