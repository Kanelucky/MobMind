package org.kanelucky.mobmind.core.entity.ai.sensor

import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.damage.Damage
import org.kanelucky.mobmind.api.entity.IntelligentEntity
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryTypes
import org.kanelucky.mobmind.api.entity.ai.sensor.Sensor

/**
 * Detects when the entity is damaged and stores the attacker in memory.
 * Unlike other sensors, this is event-driven rather than tick-based.
 *
 * @param clearAfterTicks ticks before clearing the attacker from memory (default 100)
 *
 * @author Kanelucky
 */
class HurtBySensor(
    private val clearAfterTicks: Int = 100
) : Sensor {

    override fun getPeriod() = Int.MAX_VALUE

    override fun sense(entity: EntityCreature) {
    }

    fun onEntityDamaged(entity: EntityCreature, damage: Damage) {
        if (entity !is IntelligentEntity) return
        val attacker = damage.attacker as? LivingEntity ?: return
        entity.behaviorGroup.memoryStorage.set(MemoryTypes.HURT_BY, attacker)
        entity.behaviorGroup.memoryStorage.set(MemoryTypes.HURT_BY_TICKS, clearAfterTicks)
    }

    fun tick(entity: EntityCreature) {
        if (entity !is IntelligentEntity) return
        val ticks = entity.behaviorGroup.memoryStorage.get(MemoryTypes.HURT_BY_TICKS) ?: return
        if (ticks <= 0) {
            entity.behaviorGroup.memoryStorage.clear(MemoryTypes.HURT_BY)
            entity.behaviorGroup.memoryStorage.clear(MemoryTypes.HURT_BY_TICKS)
        } else {
            entity.behaviorGroup.memoryStorage.set(MemoryTypes.HURT_BY_TICKS, ticks - 1)
        }
    }
}