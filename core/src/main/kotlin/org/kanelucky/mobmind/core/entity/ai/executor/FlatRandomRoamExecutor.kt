package org.kanelucky.mobmind.core.entity.ai.executor

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.attribute.Attribute
import net.minestom.server.instance.block.Block
import org.kanelucky.mobmind.api.entity.IntelligentEntity
import org.kanelucky.mobmind.api.entity.ai.executor.BehaviorExecutor
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryTypes
import kotlin.math.floor
import kotlin.random.Random

/**
 * Random wandering executor. Picks random XZ targets within range
 * and sets route/look targets.
 *
 * @param speed movement speed while roaming (default 0.2)
 * @param normalSpeed movement speed restored after roaming (default 0.1)
 * @param maxRoamRange max XZ distance from current position (default 10)
 * @param frequency ticks between target recalculations (default 20)
 * @param calNextTargetImmediately recalculate target immediately on arrival (default true)
 * @param runningTime max ticks before executor stops, 0 = unlimited (default 100)
 * @param avoidWater skip targets above water blocks (default true)
 * @param maxRetryTime max failed target attempts before giving up (default 10)
 *
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 *
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
class FlatRandomRoamExecutor(
    private val speed: Double = 0.2,
    private val normalSpeed: Double = 0.1,
    private val maxRoamRange: Int = 10,
    private val frequency: Int = 20,
    private val calNextTargetImmediately: Boolean = true,
    private val runningTime: Int = 100,
    private val avoidWater: Boolean = true,
    private val maxRetryTime: Int = 10
) : BehaviorExecutor {

    private var durationTick = 0
    private var targetCalTick = 0
    private var retryCount = 0
    private var hasTarget = false

    override fun onStart(entity: EntityCreature) {
        durationTick = 0
        targetCalTick = 0
        retryCount = 0
        hasTarget = false
        entity.getAttribute(Attribute.MOVEMENT_SPEED).baseValue = speed
        findNewTarget(entity)
    }

    override fun execute(entity: EntityCreature): Boolean {
        if (entity !is IntelligentEntity) return false
        durationTick++
        targetCalTick++

        val moveTarget = entity.behaviorGroup.memoryStorage.get(MemoryTypes.MOVE_TARGET)
        if (moveTarget != null && hasReachedTarget(entity, moveTarget.x(), moveTarget.z())) {
            hasTarget = false
            if (!calNextTargetImmediately) {
                EntityControlHelper.removeRouteTarget(entity)
                EntityControlHelper.removeLookTarget(entity)
            }
        }

        if (!hasTarget) {
            if (calNextTargetImmediately || targetCalTick >= frequency) {
                findNewTarget(entity)
            }
        }

        return runningTime <= 0 || durationTick <= runningTime
    }

    override fun onStop(entity: EntityCreature) {
        if (entity !is IntelligentEntity) return
        entity.getAttribute(Attribute.MOVEMENT_SPEED).baseValue = normalSpeed
        EntityControlHelper.removeRouteTarget(entity)
        EntityControlHelper.removeLookTarget(entity)
    }

    override fun onInterrupt(entity: EntityCreature) = onStop(entity)

    /**
     * Override to customize which blocks are considered invalid targets.
     */
    protected open fun isInvalidGround(block: Block): Boolean = block == Block.WATER || block == Block.LAVA

    // --- Private helpers ---

    private fun hasReachedTarget(entity: EntityCreature, tx: Double, tz: Double): Boolean {
        val dx = tx - entity.position.x()
        val dz = tz - entity.position.z()
        return dx * dx + dz * dz < 1.0
    }

    private fun findNewTarget(entity: EntityCreature) {
        if (entity !is IntelligentEntity) return
        if (retryCount >= maxRetryTime) return
        val instance = entity.instance ?: return
        val pos = entity.position

        val tx = pos.x() + Random.nextInt(-maxRoamRange, maxRoamRange + 1)
        val tz = pos.z() + Random.nextInt(-maxRoamRange, maxRoamRange + 1)
        val ty = pos.y()

        val chunk = instance.getChunkAt(tx, tz)
        if (chunk == null || !chunk.isLoaded) { retryCount++; return }

        if (avoidWater) {
            val block = instance.getBlock(floor(tx).toInt(), floor(ty).toInt() - 1, floor(tz).toInt())
            if (isInvalidGround(block)) { retryCount++; return }
        }

        val target = Pos(tx, ty, tz)
        EntityControlHelper.setRouteTarget(entity, target)
        EntityControlHelper.setLookTarget(entity, target)
        hasTarget = true
        targetCalTick = 0
        retryCount = 0
    }
}