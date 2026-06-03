package org.kanelucky.mobmind.api.entity.ai.evaluator;

import net.minestom.server.entity.EntityCreature;

/**
 * Evaluates whether a behavior should activate for the given entity.
 * <p>
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 * <p>
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
@FunctionalInterface
public interface BehaviorEvaluator {
    boolean evaluate(EntityCreature entity);
}
