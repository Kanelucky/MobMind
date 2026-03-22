package org.kanelucky.mobmind.core.entity.ai.evaluator

import net.minestom.server.entity.EntityCreature
import org.kanelucky.mobmind.api.entity.ai.evaluator.BehaviorEvaluator
import kotlin.random.Random

/**
 * Random chance evaluator. Returns {@code true} with probability
 * {@code probability / total}.
 *
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 *
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
class ProbabilityEvaluator(
    private val probability: Int,
    private val total: Int
) : BehaviorEvaluator {
    override fun evaluate(entity: EntityCreature) = Random.nextInt(total) < probability
}