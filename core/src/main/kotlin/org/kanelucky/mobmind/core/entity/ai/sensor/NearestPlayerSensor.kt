package org.kanelucky.mobmind.core.entity.ai.sensor

import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.Player
import org.kanelucky.mobmind.api.entity.IntelligentEntity
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryTypes
import org.kanelucky.mobmind.api.entity.ai.sensor.Sensor

/**
 * Scans for the nearest player within range.
 *
 * @param range max detection range in blocks (default 16.0)
 * @param minRange min detection range in blocks (default 0.0)
 *
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 *
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
class NearestPlayerSensor(
    private val range: Double = 16.0,
    private val minRange: Double = 0.0,
    private val period: Int = 20
) : Sensor {

    override fun getPeriod() = period

    override fun sense(entity: EntityCreature) {
        if (entity !is IntelligentEntity) return
        val minRangeSq = minRange * minRange
        val nearest = entity.instance
            ?.getNearbyEntities(entity.position, range)
            ?.filterIsInstance<Player>()
            ?.filter { it.position.distanceSquared(entity.position) >= minRangeSq }
            ?.minByOrNull { it.position.distanceSquared(entity.position) }
        entity.behaviorGroup.memoryStorage.set(MemoryTypes.NEAREST_PLAYER, nearest)
    }
}