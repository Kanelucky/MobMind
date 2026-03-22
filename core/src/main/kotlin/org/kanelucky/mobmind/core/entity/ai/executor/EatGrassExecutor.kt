package org.kanelucky.mobmind.core.entity.ai.executor

import net.minestom.server.entity.EntityCreature
import net.minestom.server.instance.block.Block
import org.kanelucky.mobmind.api.entity.ai.executor.BehaviorExecutor
import kotlin.math.floor

/**
 * Makes the entity eat grass — destroys short grass or converts grass block to dirt.
 * Override [onEatGrass] to add custom behavior when grass is eaten (e.g. animations, sounds).
 *
 * @param duration how many ticks the eating animation lasts (default 40)
 *
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 *
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
open class EatGrassExecutor(private val duration: Int = 40) : BehaviorExecutor {

    private var tickCounter = 0

    override fun onStart(entity: EntityCreature) {
        tickCounter = 0
    }

    override fun execute(entity: EntityCreature): Boolean = ++tickCounter < duration

    override fun onStop(entity: EntityCreature) {
        val instance = entity.instance ?: return
        val x = floor(entity.position.x()).toInt()
        val y = floor(entity.position.y()).toInt()
        val z = floor(entity.position.z()).toInt()

        when {
            instance.getBlock(x, y, z) == Block.SHORT_GRASS -> {
                instance.setBlock(x, y, z, Block.AIR)
                onEatGrass(entity)
            }
            instance.getBlock(x, y - 1, z) == Block.GRASS_BLOCK -> {
                instance.setBlock(x, y - 1, z, Block.DIRT)
                onEatGrass(entity)
            }
        }
    }

    override fun onInterrupt(entity: EntityCreature) = onStop(entity)

    /**
     * Called after grass is successfully eaten.
     * Override to play animations, sounds, or apply effects.
     */
    protected open fun onEatGrass(entity: EntityCreature) {}
}