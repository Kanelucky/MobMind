package org.kanelucky.mobmind.core.entity.ai.evaluator

import net.minestom.server.entity.EntityCreature
import org.kanelucky.mobmind.api.entity.ai.evaluator.BehaviorEvaluator

/**
 * OR-logic composite evaluator. Returns {@code true} if any
 * inner evaluator returns {@code true}.
 *
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 *
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
class AnyMatchEvaluator(private vararg val evaluators: BehaviorEvaluator) : BehaviorEvaluator {
    override fun evaluate(entity: EntityCreature) = evaluators.any { it.evaluate(entity) }
}