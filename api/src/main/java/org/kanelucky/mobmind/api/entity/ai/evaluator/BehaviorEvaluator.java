package org.kanelucky.mobmind.api.entity.ai.evaluator;

import net.minestom.server.entity.EntityCreature;

/**
 * Evaluates whether a behavior should activate for the given entity.
 *
 * @author Kanelucky || Allay (https://github.com/AllayMC/Allay)
 */
@FunctionalInterface
public interface BehaviorEvaluator {
    boolean evaluate(EntityCreature entity);
}
