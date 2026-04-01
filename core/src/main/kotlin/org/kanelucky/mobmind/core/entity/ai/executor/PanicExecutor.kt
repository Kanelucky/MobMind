package org.kanelucky.mobmind.core.entity.ai.executor

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.attribute.Attribute
import net.minestom.server.instance.block.Block

import org.kanelucky.mobmind.api.entity.IntelligentEntity
import org.kanelucky.mobmind.api.entity.ai.executor.BehaviorExecutor
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryTypes

import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.random.Random

/**
 * Makes the entity run to a random position when panicking.
 * Compatible with any [IntelligentEntity] — no mob-specific dependencies.
 *
 * @param panicSpeed movement speed during panic (default 0.15)
 * @param normalSpeed movement speed restored after panic (default 0.1)
 * @param range max distance to flee (default 8)
 *
 * @author Kanelucky
 */
class PanicExecutor(
    private val panicSpeed: Double = 0.15,
    private val normalSpeed: Double = 0.1,
    private val range: Int = 8
) : BehaviorExecutor {

    override fun onStart(entity: EntityCreature) {
        entity.getAttribute(Attribute.MOVEMENT_SPEED).baseValue = panicSpeed
        findPanicTarget(entity)
    }

    override fun execute(entity: EntityCreature): Boolean {
        if (entity !is IntelligentEntity) return false

        val panicTicks = entity.behaviorGroup.memoryStorage.get(MemoryTypes.PANIC_TICKS) ?: return false
        if (panicTicks <= 0) return false

        entity.behaviorGroup.memoryStorage.set(MemoryTypes.PANIC_TICKS, panicTicks - 1)

        val target = entity.behaviorGroup.memoryStorage.get(MemoryTypes.MOVE_TARGET)
        if (target == null || hasReachedTarget(entity, target.x(), target.z())) {
            findPanicTarget(entity)
        }

        return true
    }

    override fun onStop(entity: EntityCreature) {
        entity.getAttribute(Attribute.MOVEMENT_SPEED).baseValue = normalSpeed
        if (entity is IntelligentEntity) {
            EntityControlHelper.removeRouteTarget(entity)
            entity.behaviorGroup.memoryStorage.clear(MemoryTypes.PANIC_TICKS)
        }
    }

    override fun onInterrupt(entity: EntityCreature) = onStop(entity)

    private fun hasReachedTarget(entity: EntityCreature, tx: Double, tz: Double): Boolean {
        val dx = tx - entity.position.x()
        val dz = tz - entity.position.z()
        return dx * dx + dz * dz < 1.0
    }

    private fun findPanicTarget(entity: EntityCreature) {
        if (entity !is IntelligentEntity) return
        val instance = entity.instance ?: return

        repeat(10) {
            val angle = Random.nextDouble() * 2 * Math.PI
            val dist = Random.nextInt(range / 2, range + 1).toDouble()
            val tx = entity.position.x() + dist * cos(angle)
            val tz = entity.position.z() + dist * sin(angle)
            val ty = entity.position.y()

            val chunk = instance.getChunkAt(tx, tz)
            if (chunk == null || !chunk.isLoaded) return@repeat

            val block = instance.getBlock(floor(tx).toInt(), floor(ty).toInt() - 1, floor(tz).toInt())
            if (block == Block.WATER || block == Block.LAVA) return@repeat

            EntityControlHelper.setRouteTarget(entity, Pos(tx, ty, tz))
            return
        }
    }
}