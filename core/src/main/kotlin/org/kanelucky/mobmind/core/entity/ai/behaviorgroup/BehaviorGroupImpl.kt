package org.kanelucky.mobmind.core.entity.ai.behaviorgroup

import net.minestom.server.entity.EntityCreature

import org.kanelucky.mobmind.api.entity.ai.behavior.Behavior
import org.kanelucky.mobmind.api.entity.ai.behavior.BehaviorState
import org.kanelucky.mobmind.api.entity.ai.behaviorgroup.BehaviorGroup
import org.kanelucky.mobmind.api.entity.ai.behaviorgroup.BehaviorGroupBuilder
import org.kanelucky.mobmind.api.entity.ai.controller.Controller
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryStorage
import org.kanelucky.mobmind.api.entity.ai.sensor.Sensor
import org.kanelucky.mobmind.core.entity.ai.memory.MemoryStorageImpl

/**
 * Default implementation of [BehaviorGroup].
 *
 * @author Kanelucky || Allay (https://github.com/AllayMC/Allay)
 */
class BehaviorGroupImpl(
    private val coreBehaviors: Set<Behavior>,
    private val behaviors: Set<Behavior>,
    private val sensors: Set<Sensor>,
    private val controllers: Set<Controller>,
    private val memoryStorage: MemoryStorage
) : BehaviorGroup {

    override fun getMemoryStorage() = memoryStorage

    private var entity: EntityCreature? = null

    private val sensorArray = sensors.toTypedArray()
    private val coreBehaviorArray = coreBehaviors.toTypedArray()
    private val behaviorArray = behaviors.toTypedArray()
    private val controllerArray = controllers.toTypedArray()

    private val sensorCounters = IntArray(sensorArray.size)
    private val coreBehaviorCounters = IntArray(coreBehaviorArray.size)
    private val behaviorCounters = IntArray(behaviorArray.size)

    private val runningCoreBehaviors = ArrayList<Behavior>(4)
    private val runningBehaviors = ArrayList<Behavior>(4)

    private val candidateBuffer = ArrayList<Behavior>(4)

    override fun setEntity(entity: EntityCreature) {
        this.entity = entity
    }

    override fun tick() {
        val entity = entity ?: return
        collectSensorData(entity)
        evaluateCoreBehaviors(entity)
        evaluateBehaviors(entity)
        tickRunning(entity, runningCoreBehaviors)
        tickRunning(entity, runningBehaviors)
        for (controller in controllerArray) controller.control(entity)
    }

    private fun collectSensorData(entity: EntityCreature) {
        for (i in sensorArray.indices) {
            val sensor = sensorArray[i]
            val counter = sensorCounters[i] + 1
            if (counter >= sensor.period) {
                sensor.sense(entity)
                sensorCounters[i] = 0
            } else {
                sensorCounters[i] = counter
            }
        }
    }

    private fun evaluateCoreBehaviors(entity: EntityCreature) {
        for (i in coreBehaviorArray.indices) {
            val behavior = coreBehaviorArray[i]
            if (behavior in runningCoreBehaviors) continue
            val counter = coreBehaviorCounters[i] + 1
            coreBehaviorCounters[i] = if (counter < behavior.period) counter else 0
            if (counter < behavior.period) continue
            if (behavior.evaluate(entity)) {
                behavior.onStart(entity)
                behavior.behaviorState = BehaviorState.ACTIVE
                runningCoreBehaviors.add(behavior)
            }
        }
    }

    private fun evaluateBehaviors(entity: EntityCreature) {
        candidateBuffer.clear()
        var highestPriority = Int.MIN_VALUE

        for (i in behaviorArray.indices) {
            val behavior = behaviorArray[i]
            if (behavior in runningBehaviors) continue
            val counter = behaviorCounters[i] + 1
            behaviorCounters[i] = if (counter < behavior.period) counter else 0
            if (counter < behavior.period) continue
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

        if (candidateBuffer.isEmpty()) return

        val runningPriority = runningBehaviors.firstOrNull()?.priority ?: Int.MIN_VALUE
        when {
            highestPriority > runningPriority -> {
                interruptRunningBehaviors(entity)
                startBehaviors(entity, candidateBuffer)
            }

            highestPriority == runningPriority -> startBehaviors(entity, candidateBuffer)
        }
    }

    private fun interruptRunningBehaviors(entity: EntityCreature) {
        for (behavior in runningBehaviors) {
            behavior.onInterrupt(entity)
            behavior.behaviorState = BehaviorState.STOP
        }
        runningBehaviors.clear()
    }

    private fun startBehaviors(entity: EntityCreature, toStart: List<Behavior>) {
        for (behavior in toStart) {
            behavior.onStart(entity)
            behavior.behaviorState = BehaviorState.ACTIVE
            runningBehaviors.add(behavior)
        }
    }

    private fun tickRunning(entity: EntityCreature, running: MutableList<Behavior>) {
        val iterator = running.iterator()
        while (iterator.hasNext()) {
            val behavior = iterator.next()
            if (!behavior.execute(entity)) {
                behavior.onStop(entity)
                behavior.behaviorState = BehaviorState.STOP
                iterator.remove()
            }
        }
    }

    class Builder : BehaviorGroupBuilder {
        private val coreBehaviors = linkedSetOf<Behavior>()
        private val behaviors = linkedSetOf<Behavior>()
        private val sensors = linkedSetOf<Sensor>()
        private val controllers = linkedSetOf<Controller>()
        private var memoryStorage: MemoryStorage = MemoryStorageImpl()

        override fun coreBehavior(b: Behavior) = apply { coreBehaviors.add(b) }
        override fun behavior(b: Behavior) = apply { behaviors.add(b) }
        override fun sensor(s: Sensor) = apply { sensors.add(s) }
        override fun controller(c: Controller) = apply { controllers.add(c) }
        override fun memoryStorage(m: MemoryStorage) = apply { memoryStorage = m }

        override fun build(): BehaviorGroup =
            BehaviorGroupImpl(coreBehaviors, behaviors, sensors, controllers, memoryStorage)
    }
}