package org.kanelucky.mobmind.core.entity.ai.executor

import net.minestom.server.coordinate.Point

import org.kanelucky.mobmind.api.entity.IntelligentEntity
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryTypes

/**
 * Utility class with static helper methods for controlling entity movement
 * and look targets through memory storage
 *
 * @author Kanelucky || Allay (https://github.com/AllayMC/Allay)
 */
object EntityControlHelper {

    fun setRouteTarget(entity: IntelligentEntity, target: Point) {
        entity.behaviorGroup.memoryStorage.set(MemoryTypes.MOVE_TARGET, target)
    }

    fun removeRouteTarget(entity: IntelligentEntity) {
        entity.behaviorGroup.memoryStorage.clear(MemoryTypes.MOVE_TARGET)
        entity.navigator.reset()
    }

    fun setLookTarget(entity: IntelligentEntity, target: Point) {
        entity.behaviorGroup.memoryStorage.set(MemoryTypes.LOOK_TARGET, target)
    }

    fun removeLookTarget(entity: IntelligentEntity) {
        entity.behaviorGroup.memoryStorage.clear(MemoryTypes.LOOK_TARGET)
    }
}