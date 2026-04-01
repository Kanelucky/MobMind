package org.kanelucky.mobmind.core.entity.ai.executor

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.attribute.Attribute

import org.kanelucky.mobmind.api.entity.IntelligentEntity
import org.kanelucky.mobmind.api.entity.ai.executor.BehaviorExecutor
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryType

/**
 * Follows an entity stored in a given memory type.
 * Works with any [Entity] target — not limited to players.
 *
 * @param entityMemory memory type holding the target entity
 * @param speed movement speed while following (default 0.125)
 * @param normalSpeed movement speed restored after following (default 0.1)
 * @param maxRangeSq max squared distance to follow (default 256.0)
 * @param minRangeSq min squared distance before stopping movement (default 4.0)
 *
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 *
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
class FollowEntityExecutor(
    private val entityMemory: MemoryType<out Any?>,
    private val speed: Double = 0.125,
    private val normalSpeed: Double = 0.1,
    private val maxRangeSq: Double = 256.0,
    private val minRangeSq: Double = 4.0
) : BehaviorExecutor {

    private var lastTargetPos: Pos? = null

    override fun onStart(entity: EntityCreature) {
        lastTargetPos = null
        entity.getAttribute(Attribute.MOVEMENT_SPEED).baseValue = speed
    }

    override fun execute(entity: EntityCreature): Boolean {
        if (entity !is IntelligentEntity) return false

        val target = entity.behaviorGroup.memoryStorage.get(entityMemory)
        if (target !is Entity) return false
        val targetPos = target.position
        val distSq = entity.position.distanceSquared(targetPos)

        if (distSq > maxRangeSq) return false

        if (distSq > minRangeSq) {
            val pos = Pos(targetPos.x(), targetPos.y(), targetPos.z())
            if (lastTargetPos == null || lastTargetPos!!.distanceSquared(pos) > 1.0) {
                EntityControlHelper.setRouteTarget(entity, pos)
                lastTargetPos = pos
            }
        } else {
            EntityControlHelper.removeRouteTarget(entity)
        }

        EntityControlHelper.setLookTarget(entity,
            Pos(targetPos.x(), targetPos.y() + target.eyeHeight, targetPos.z())
        )
        return true
    }

    override fun onStop(entity: EntityCreature) {
        if (entity !is IntelligentEntity) return
        entity.getAttribute(Attribute.MOVEMENT_SPEED).baseValue = normalSpeed
        EntityControlHelper.removeRouteTarget(entity)
        EntityControlHelper.removeLookTarget(entity)
        lastTargetPos = null
    }

    override fun onInterrupt(entity: EntityCreature) = onStop(entity)
}