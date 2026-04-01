package org.kanelucky.mobmind.core.entity.ai.behavior

import net.minestom.server.entity.EntityCreature
import org.kanelucky.mobmind.api.entity.ai.behavior.Behavior

import kotlin.random.Random

/**
 * A composite behavior that uses weighted random selection among the highest-priority
 * children that evaluate to true.
 *
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 *
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
class WeightedMultiBehavior(
    private val behaviors: Set<Behavior>,
    private val priority: Int,
    private val weight: Int = 1,
    private val period: Int = 1
) : AbstractBehavior() {

    private var activeBehavior: Behavior? = null

    override fun getPriority() = priority
    override fun getWeight() = weight
    override fun getPeriod() = period

    override fun evaluate(entity: EntityCreature): Boolean {
        val highestPriority = behaviors
            .filter { it.evaluate(entity) }
            .maxOfOrNull { it.priority } ?: return false

        val candidates = behaviors.filter { it.priority == highestPriority && it.evaluate(entity) }
        val totalWeight = candidates.sumOf { it.weight }
        var random = Random.nextInt(totalWeight)

        for (behavior in candidates) {
            random -= behavior.weight
            if (random < 0) {
                activeBehavior = behavior
                return true
            }
        }
        return false
    }

    override fun execute(entity: EntityCreature) = activeBehavior?.execute(entity) ?: false
    override fun onStart(entity: EntityCreature) { activeBehavior?.onStart(entity) }
    override fun onStop(entity: EntityCreature) { activeBehavior?.onStop(entity); activeBehavior = null }
    override fun onInterrupt(entity: EntityCreature) { activeBehavior?.onInterrupt(entity); activeBehavior = null }
}