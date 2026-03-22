package org.kanelucky.mobmind.api.entity.ai.behaviorgroup;

import net.minestom.server.entity.EntityCreature;
import org.kanelucky.mobmind.api.entity.ai.behavior.Behavior;
import org.kanelucky.mobmind.api.entity.ai.controller.Controller;
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryStorage;
import org.kanelucky.mobmind.api.entity.ai.sensor.Sensor;

public interface BehaviorGroupBuilder {
    BehaviorGroupBuilder coreBehavior(Behavior behavior);
    BehaviorGroupBuilder behavior(Behavior behavior);
    BehaviorGroupBuilder sensor(Sensor sensor);
    BehaviorGroupBuilder controller(Controller controller);
    BehaviorGroupBuilder memoryStorage(MemoryStorage memoryStorage);
    BehaviorGroup build();
}