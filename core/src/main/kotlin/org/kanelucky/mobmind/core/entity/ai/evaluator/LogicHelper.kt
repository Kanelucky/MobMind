package org.kanelucky.mobmind.core.entity.ai.evaluator

import org.kanelucky.mobmind.api.entity.ai.evaluator.BehaviorEvaluator

/**
 * Utility class for composing behavior evaluators with logical operators
 *
 * @author Kanelucky || Allay (https://github.com/AllayMC/Allay)
 */
object LogicHelper {
    fun all(vararg evaluators: BehaviorEvaluator) = AllMatchEvaluator(*evaluators)
    fun any(vararg evaluators: BehaviorEvaluator) = AnyMatchEvaluator(*evaluators)
}