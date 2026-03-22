package org.kanelucky.mobmind.core.entity.ai.evaluator

import net.minestom.server.entity.EntityCreature
import org.kanelucky.mobmind.api.entity.ai.evaluator.BehaviorEvaluator


/**
 * AND-logic composite evaluator. Returns {@code true} only if all
 * inner evaluators return {@code true}.
 *
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 *
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
class AllMatchEvaluator(private vararg val evaluators: BehaviorEvaluator) : BehaviorEvaluator {
    override fun evaluate(entity: EntityCreature) = evaluators.all { it.evaluate(entity) }
}