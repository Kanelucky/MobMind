package org.kanelucky.mobmind.core.entity.ai.executor

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.attribute.Attribute

import org.kanelucky.mobmind.api.entity.IntelligentEntity
import org.kanelucky.mobmind.api.entity.ai.executor.BehaviorExecutor
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryType

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Makes the entity flee from a target stored in memory.
 * Unlike PanicExecutor, this actively runs away from a specific entity
 * rather than fleeing in a random direction.
 *
 * @param targetMemory memory type holding the entity to flee from
 * @param fleeSpeed movement speed while fleeing (default 0.15)
 * @param normalSpeed movement speed restored after (default 0.1)
 * @param minFleeRangeSq minimum squared distance to maintain from target (default 64.0 = 8 blocks)
 * @param maxFleeRangeSq stop fleeing beyond this squared distance (default 256.0 = 16 blocks)
 * @param recalculateInterval ticks between recalculating flee target (default 10)
 *
 * Originally inspired by PNX FleeExecutor
 * Port author: Kanelucky
 */
open class FleeExecutor(
    private val targetMemory: MemoryType<out Any?>,
    private val fleeSpeed: Double = 0.15,
    private val normalSpeed: Double = 0.1,
    private val minFleeRangeSq: Double = 64.0,
    private val maxFleeRangeSq: Double = 256.0,
    private val recalculateInterval: Int = 10
) : BehaviorExecutor {

    private var tick = 0

    override fun onStart(entity: EntityCreature) {
        tick = 0
        entity.getAttribute(Attribute.MOVEMENT_SPEED).baseValue = fleeSpeed
    }

    override fun execute(entity: EntityCreature): Boolean {
        if (entity !is IntelligentEntity) return false

        val threat = entity.behaviorGroup.memoryStorage.get(targetMemory)
            ?.let { it as? LivingEntity }
            ?: return false

        if (threat.isDead) return false

        val distSq = entity.position.distanceSquared(threat.position)

        if (distSq >= maxFleeRangeSq) return false

        tick++
        if (tick >= recalculateInterval) {
            tick = 0
            calculateFleeTarget(entity, threat)
        }

        return true
    }

    override fun onStop(entity: EntityCreature) {
        if (entity !is IntelligentEntity) return
        entity.getAttribute(Attribute.MOVEMENT_SPEED).baseValue = normalSpeed
        EntityControlHelper.removeRouteTarget(entity)
        EntityControlHelper.removeLookTarget(entity)
    }

    override fun onInterrupt(entity: EntityCreature) = onStop(entity)

    private fun calculateFleeTarget(entity: EntityCreature, threat: LivingEntity) {
        if (entity !is IntelligentEntity) return
        val instance = entity.instance ?: return

        val dx = entity.position.x() - threat.position.x()
        val dz = entity.position.z() - threat.position.z()
        val length = sqrt(dx * dx + dz * dz)

        if (length == 0.0) {
            val angle = Random.nextDouble() * 2 * Math.PI
            fleeToward(
                entity, instance,
                entity.position.x() + cos(angle) * 8,
                entity.position.z() + sin(angle) * 8
            )
            return
        }

        val normalizedDx = dx / length
        val normalizedDz = dz / length
        val fleeDistance = 10.0 + Random.nextDouble(4.0)
        val angleOffset = (Random.nextDouble() - 0.5) * Math.PI / 3

        val finalDx = normalizedDx * cos(angleOffset) - normalizedDz * sin(angleOffset)
        val finalDz = normalizedDx * sin(angleOffset) + normalizedDz * cos(angleOffset)

        val targetX = entity.position.x() + finalDx * fleeDistance
        val targetZ = entity.position.z() + finalDz * fleeDistance

        fleeToward(entity, instance, targetX, targetZ)
    }

    private fun fleeToward(
        entity: EntityCreature,
        instance: net.minestom.server.instance.Instance,
        targetX: Double,
        targetZ: Double
    ) {
        if (entity !is IntelligentEntity) return
        val chunk = instance.getChunkAt(targetX, targetZ)
        if (chunk == null || !chunk.isLoaded) return

        val targetPos = Pos(targetX, entity.position.y(), targetZ)
        EntityControlHelper.setRouteTarget(entity, targetPos)
    }

    /**
     * Override to customize flee behavior.
     * Called every time a new flee target is calculated.
     */
    protected open fun onFlee(entity: EntityCreature, threat: LivingEntity) {}
}