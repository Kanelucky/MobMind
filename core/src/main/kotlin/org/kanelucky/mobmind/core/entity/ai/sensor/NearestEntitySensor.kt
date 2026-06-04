package org.kanelucky.mobmind.core.entity.ai.sensor

import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.LivingEntity
import org.kanelucky.mobmind.api.entity.IntelligentEntity
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryType
import org.kanelucky.mobmind.api.entity.ai.sensor.Sensor

/**
 * Scans for the nearest entity of a given type within range.
 *
 * @param memoryType memory type to store the nearest entity
 * @param entityClass the class of entity to search for
 * @param range max detection range in blocks (default 16.0)
 * @param minRange min detection range in blocks (default 0.0)
 * @param predicate optional additional filter condition
 *
 * @author Kanelucky
 */
class NearestEntitySensor<T : LivingEntity>(
    private val memoryType: MemoryType<T>,
    private val entityClass: Class<T>,
    private val range: Double = 16.0,
    private val minRange: Double = 0.0,
    private val period: Int = 20,
    private val predicate: ((T) -> Boolean)? = null
) : Sensor {

    override fun getPeriod() = period

    override fun sense(entity: EntityCreature) {
        if (entity !is IntelligentEntity) return

        val minRangeSq = minRange * minRange
        val entityPos = entity.position
        var nearest: T? = null
        var nearestDistSq = Double.MAX_VALUE

        entity.instance?.getNearbyEntities(entityPos, range)?.forEach { e ->
            if (!entityClass.isInstance(e)) return@forEach
            if (e === entity) return@forEach
            val candidate = entityClass.cast(e)
            if (predicate != null && !predicate.invoke(candidate)) return@forEach
            val distSq = e.position.distanceSquared(entityPos)
            if (distSq < minRangeSq) return@forEach
            if (distSq < nearestDistSq) {
                nearest = candidate
                nearestDistSq = distSq
            }
        }

        entity.behaviorGroup.memoryStorage.set(memoryType, nearest)
    }
}