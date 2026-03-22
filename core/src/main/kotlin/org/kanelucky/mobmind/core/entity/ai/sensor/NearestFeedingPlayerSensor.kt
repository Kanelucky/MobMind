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
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 *
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
class NearestFeedingPlayerSensor(
    private val range: Double = 8.0,
    private val period: Int = 20
) : Sensor {

    override fun getPeriod() = period

    override fun sense(entity: EntityCreature) {
        if (entity !is IntelligentEntity) return
        if (entity !is Feedable) return
        val nearest = entity.instance
            ?.getNearbyEntities(entity.position, range)
            ?.filterIsInstance<Player>()
            ?.filter { entity.isBreedingItem(it.itemInMainHand) }
            ?.minByOrNull { it.position.distanceSquared(entity.position) }
        entity.behaviorGroup.memoryStorage.set(MemoryTypes.NEAREST_FEEDING_PLAYER, nearest)
    }
}