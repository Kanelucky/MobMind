package org.kanelucky.mobmind.core.entity.ai.executor

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityCreature
import org.kanelucky.mobmind.api.entity.IntelligentEntity
import org.kanelucky.mobmind.api.entity.ai.executor.BehaviorExecutor
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryType

/**
 * Looks at an entity whose runtime ID is stored in memory for a duration
 *
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 *
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
/**
 * Looks at an entity stored in memory for a given duration.
 * Works with any [Entity] target — not limited to players.
 *
 * @param entityMemory memory type holding the target entity
 * @param duration how many ticks to look at the target (default 60)
 *
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 *
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
class LookAtEntityExecutor(
    private val entityMemory: MemoryType<out Any?>,
    private val duration: Int = 60
) : BehaviorExecutor {

    private var tickCounter = 0

    override fun onStart(entity: EntityCreature) {
        tickCounter = 0
    }

    override fun execute(entity: EntityCreature): Boolean {
        if (entity !is IntelligentEntity) return false
        if (++tickCounter > duration) return false

        val target = entity.behaviorGroup.memoryStorage.get(entityMemory)
        if (target !is Entity) return false
        EntityControlHelper.setLookTarget(entity,
            Pos(target.position.x(), target.position.y() + target.eyeHeight, target.position.z())
        )
        return true
    }

    override fun onStop(entity: EntityCreature) {
        if (entity !is IntelligentEntity) return
        EntityControlHelper.removeLookTarget(entity)
    }

    override fun onInterrupt(entity: EntityCreature) = onStop(entity)

    /**
     * Override to customize how the look target position is calculated.
     */
    protected open fun getLookTarget(target: Entity): Pos =
        Pos(target.position.x(), target.position.y() + target.eyeHeight, target.position.z())
}