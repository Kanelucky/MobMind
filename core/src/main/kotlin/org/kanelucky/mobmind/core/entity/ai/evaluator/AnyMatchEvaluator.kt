package org.kanelucky.mobmind.core.entity.ai.evaluator

import net.minestom.server.entity.EntityCreature

import org.kanelucky.mobmind.api.entity.ai.evaluator.BehaviorEvaluator

/**
 * OR-logic composite evaluator. Returns {@code true} if any
 * inner evaluator returns {@code true}.
 *
 * @author Kanelucky || Allay (https://github.com/AllayMC/Allay)
 */
class AnyMatchEvaluator(private vararg val evaluators: BehaviorEvaluator) : BehaviorEvaluator {
    override fun evaluate(entity: EntityCreature) = evaluators.any { it.evaluate(entity) }
}