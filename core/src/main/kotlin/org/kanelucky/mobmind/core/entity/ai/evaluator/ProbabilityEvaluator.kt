package org.kanelucky.mobmind.core.entity.ai.evaluator

import net.minestom.server.entity.EntityCreature

import org.kanelucky.mobmind.api.entity.ai.evaluator.BehaviorEvaluator

import kotlin.random.Random

/**
 * Random chance evaluator. Returns {@code true} with probability
 * {@code probability / total}.
 *
 * @author Kanelucky || Allay (https://github.com/AllayMC/Allay)
 */
class ProbabilityEvaluator(
    private val probability: Int,
    private val total: Int
) : BehaviorEvaluator {
    override fun evaluate(entity: EntityCreature) = Random.nextInt(total) < probability
}