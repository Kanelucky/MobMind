package org.kanelucky.mobmind.api.entity.ai.behaviorgroup;

import net.minestom.server.entity.EntityCreature;
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryStorage;

/**
 * Manages AI behaviors, sensors, and memory for an entity.
 */
public interface BehaviorGroup {
    MemoryStorage getMemoryStorage();
    void tick();
    void setEntity(EntityCreature entity);

    static BehaviorGroupBuilder builder() {
        return BehaviorGroupBuilderProvider.get();
    }
}

