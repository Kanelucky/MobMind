package org.kanelucky.mobmind.core.entity.ai.sensor

import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.Player
import org.kanelucky.mobmind.api.entity.IntelligentEntity
import org.kanelucky.mobmind.api.entity.ai.Feedable
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryTypes
import org.kanelucky.mobmind.api.entity.ai.sensor.Sensor

/**
 * Scans for the nearest player holding a valid feeding item.
 *
 * @param range max detection range in blocks (default 8.0)
 *
 * @author Kanelucky || Allay (https://github.com/AllayMC/Allay)
 */
class NearestFeedingPlayerSensor(
    private val range: Double = 8.0,
    private val period: Int = 20
) : Sensor {

    override fun getPeriod() = period

    override fun sense(entity: EntityCreature) {
        if (entity !is IntelligentEntity) return
        if (entity !is Feedable) return

        val entityPos = entity.position
        var nearest: Player? = null
        var nearestDistSq = Double.MAX_VALUE

        entity.instance?.getNearbyEntities(entityPos, range)?.forEach { e ->
            if (e !is Player) return@forEach
            if (!entity.isBreedingItem(e.itemInMainHand)) return@forEach
            val distSq = e.position.distanceSquared(entityPos)
            if (distSq < nearestDistSq) {
                nearest = e
                nearestDistSq = distSq
            }
        }

        entity.behaviorGroup.memoryStorage.set(MemoryTypes.NEAREST_FEEDING_PLAYER, nearest)
    }
}