package org.kanelucky.mobmind.api.entity.ai.behavior;

import net.minestom.server.entity.EntityCreature;

public interface Behavior {
    int getPriority();
    int getWeight();
    int getPeriod();
    BehaviorState getBehaviorState();
    void setBehaviorState(BehaviorState state);
    boolean evaluate(EntityCreature entity);
    boolean execute(EntityCreature entity);
    default void onStart(EntityCreature entity) {}
    default void onStop(EntityCreature entity) {}
    default void onInterrupt(EntityCreature entity) {}
}
