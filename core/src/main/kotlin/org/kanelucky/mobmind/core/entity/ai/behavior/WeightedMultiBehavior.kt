package org.kanelucky.mobmind.core.entity.ai.behavior

import net.minestom.server.entity.EntityCreature
import org.kanelucky.mobmind.api.entity.ai.behavior.Behavior

import kotlin.random.Random

/**
 * A composite behavior that uses weighted random selection among the highest-priority
 * children that evaluate to true.
 *
 * @author Kanelucky || Allay (https://github.com/AllayMC/Allay)
 */
class WeightedMultiBehavior(
    private val behaviors: Set<Behavior>,
    private val priority: Int,
    private val weight: Int = 1,
    private val period: Int = 1
) : AbstractBehavior() {

    private var activeBehavior: Behavior? = null

    private val candidateBuffer = ArrayList<Behavior>(behaviors.size)

    override fun getPriority() = priority
    override fun getWeight() = weight
    override fun getPeriod() = period

    override fun evaluate(entity: EntityCreature): Boolean {
        candidateBuffer.clear()

        var highestPriority = Int.MIN_VALUE
        for (behavior in behaviors) {
            if (!behavior.evaluate(entity)) continue
            when {
                behavior.priority > highestPriority -> {
                    candidateBuffer.clear()
                    highestPriority = behavior.priority
                    candidateBuffer.add(behavior)
                }

                behavior.priority == highestPriority -> candidateBuffer.add(behavior)
            }
        }

        if (candidateBuffer.isEmpty()) return false

        var totalWeight = 0
        for (b in candidateBuffer) totalWeight += b.weight
        var random = Random.nextInt(totalWeight)

        for (behavior in candidateBuffer) {
            random -= behavior.weight
            if (random < 0) {
                activeBehavior = behavior
                return true
            }
        }
        return false
    }

    override fun execute(entity: EntityCreature) = activeBehavior?.execute(entity) ?: false

    override fun onStart(entity: EntityCreature) {
        activeBehavior?.onStart(entity)
    }

    override fun onStop(entity: EntityCreature) {
        activeBehavior?.onStop(entity); activeBehavior = null
    }

    override fun onInterrupt(entity: EntityCreature) {
        activeBehavior?.onInterrupt(entity); activeBehavior = null
    }
}