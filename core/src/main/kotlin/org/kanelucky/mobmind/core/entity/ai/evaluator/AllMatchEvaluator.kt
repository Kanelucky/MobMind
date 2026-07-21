package org.kanelucky.mobmind.core.entity.ai.evaluator

import net.minestom.server.entity.EntityCreature

import org.kanelucky.mobmind.api.entity.ai.evaluator.BehaviorEvaluator


/**
 * AND-logic composite evaluator. Returns {@code true} only if all
 * inner evaluators return {@code true}.
 *
 * @author Kanelucky || Allay (https://github.com/AllayMC/Allay)
 */
class AllMatchEvaluator(private vararg val evaluators: BehaviorEvaluator) : BehaviorEvaluator {
    override fun evaluate(entity: EntityCreature) = evaluators.all { it.evaluate(entity) }
}