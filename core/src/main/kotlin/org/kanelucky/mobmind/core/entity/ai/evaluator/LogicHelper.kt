package org.kanelucky.mobmind.core.entity.ai.evaluator

import org.kanelucky.mobmind.api.entity.ai.evaluator.BehaviorEvaluator

/**
 * Utility class for composing behavior evaluators with logical operators
 *
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 *
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
object LogicHelper {
    fun all(vararg evaluators: BehaviorEvaluator) = AllMatchEvaluator(*evaluators)
    fun any(vararg evaluators: BehaviorEvaluator) = AnyMatchEvaluator(*evaluators)
}