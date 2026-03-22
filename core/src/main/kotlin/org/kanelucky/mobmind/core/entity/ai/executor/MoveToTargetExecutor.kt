package org.kanelucky.mobmind.core.entity.ai.executor

import net.minestom.server.coordinate.Point
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.attribute.Attribute
import org.kanelucky.mobmind.api.entity.IntelligentEntity
import org.kanelucky.mobmind.api.entity.ai.executor.BehaviorExecutor
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryType

/**
 * Moves the entity toward a position stored in a given memory type.
 * Works with any [IntelligentEntity] — no mob-specific dependencies.
 *
 * @param memoryType the memory type holding the target position
 * @param speed movement speed toward the target (default 0.2)
 * @param maxFollowRangeSq max squared distance to follow (default 256.0)
 * @param minFollowRangeSq min squared distance before stopping (default 0.0)
 *
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 *
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
class MoveToTargetExecutor(
    private val memoryType: MemoryType<out Any?>,
    private val speed: Double = 0.2,
    private val normalSpeed: Double = 0.1,
    private val maxFollowRangeSq: Double = 256.0,
    private val minFollowRangeSq: Double = 0.0
) : BehaviorExecutor {

    private var lastTarget: Point? = null

    override fun onStart(entity: EntityCreature) {
        lastTarget = null
        applySpeed(entity, speed)
    }

    override fun execute(entity: EntityCreature): Boolean {
        if (entity !is IntelligentEntity) return false

        val target = entity.behaviorGroup.memoryStorage.get(memoryType) as? Point? ?: return false
        val distSq = entity.position.distanceSquared(target)

        if (!isInRange(distSq)) return false

        if (shouldUpdateTarget(target)) {
            EntityControlHelper.setRouteTarget(entity, target)
            EntityControlHelper.setLookTarget(entity, target)
            lastTarget = target
        }
        return true
    }

    override fun onStop(entity: EntityCreature) {
        if (entity !is IntelligentEntity) return
        EntityControlHelper.removeRouteTarget(entity)
        EntityControlHelper.removeLookTarget(entity)
        lastTarget = null
        restoreSpeed(entity)
    }

    override fun onInterrupt(entity: EntityCreature) = onStop(entity)

    /**
     * Returns true if the entity is within valid follow range.
     * Override to add custom range logic.
     */
    protected open fun isInRange(distSq: Double): Boolean =
        distSq <= maxFollowRangeSq && distSq >= minFollowRangeSq

    /**
     * Returns true if the route target should be updated.
     * Override to change update frequency or conditions.
     */
    protected open fun shouldUpdateTarget(target: Point): Boolean =
        lastTarget == null || target.distanceSquared(lastTarget!!) > 1.0

    /**
     * Called when movement starts. Override to apply custom speed logic.
     */
    protected open fun applySpeed(entity: EntityCreature, speed: Double) {
        entity.getAttribute(Attribute.MOVEMENT_SPEED).baseValue = speed
    }

    /**
     * Called when movement stops. Override to restore custom speed.
     */
    protected open fun restoreSpeed(entity: EntityCreature) {
        entity.getAttribute(Attribute.MOVEMENT_SPEED).baseValue = 0.1
    }
}