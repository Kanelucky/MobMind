package org.kanelucky.mobmind.core.entity.ai.evaluator

import net.minestom.server.entity.EntityCreature
import net.minestom.server.instance.block.Block
import org.kanelucky.mobmind.api.entity.ai.evaluator.BehaviorEvaluator
import kotlin.math.floor

/**
 * Checks if the block at the entity's location plus an offset
 * matches the expected block type.
 *
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 *
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
class BlockCheckEvaluator(
    private val block: Block,
    private val offsetX: Int = 0,
    private val offsetY: Int = 0,
    private val offsetZ: Int = 0
) : BehaviorEvaluator {
    override fun evaluate(entity: EntityCreature): Boolean {
        val pos = entity.position
        val x = floor(pos.x()).toInt() + offsetX
        val y = floor(pos.y()).toInt() + offsetY
        val z = floor(pos.z()).toInt() + offsetZ
        return entity.instance?.getBlock(x, y, z) == block
    }
}