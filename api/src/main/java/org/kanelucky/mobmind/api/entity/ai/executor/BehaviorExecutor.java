package org.kanelucky.mobmind.api.entity.ai.executor;

import net.minestom.server.entity.EntityCreature;

/**
 * Defines the execution logic for a behavior.
 * Implement this interface to create custom behavior executors.
 *
 * @author Kanelucky
 */
public interface BehaviorExecutor {
    boolean execute(EntityCreature entity);
    default void onStart(EntityCreature entity) {}
    default void onStop(EntityCreature entity) {}
    default void onInterrupt(EntityCreature entity) {}
}
