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
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 *
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
class BehaviorGroupImpl(
    private val coreBehaviors: Set<Behavior>,
    private val behaviors: Set<Behavior>,
    private val sensors: Set<Sensor>,
    private val controllers: Set<Controller>,
    private val memoryStorage: MemoryStorage
) : BehaviorGroup {

    override fun getMemoryStorage(): MemoryStorage = memoryStorage

    private var entity: EntityCreature? = null

    private val sensorCounters = mutableMapOf<Sensor, Int>()
    private val coreBehaviorCounters = mutableMapOf<Behavior, Int>()
    private val behaviorCounters = mutableMapOf<Behavior, Int>()
    private val runningCoreBehaviors = linkedSetOf<Behavior>()
    private val runningBehaviors = linkedSetOf<Behavior>()

    override fun setEntity(entity: EntityCreature) {
        this.entity = entity
        sensors.forEach { sensorCounters[it] = 0 }
        coreBehaviors.forEach { coreBehaviorCounters[it] = 0 }
        behaviors.forEach { behaviorCounters[it] = 0 }
    }

    override fun tick() {
        val entity = entity ?: return
        collectSensorData(entity)
        evaluateCoreBehaviors(entity)
        evaluateBehaviors(entity)
        tickRunning(entity, runningCoreBehaviors)
        tickRunning(entity, runningBehaviors)
        controllers.forEach { it.control(entity) }
    }

    private fun collectSensorData(entity: EntityCreature) {
        for (sensor in sensors) {
            val counter = (sensorCounters[sensor] ?: 0) + 1
            if (counter >= sensor.period) {
                sensor.sense(entity)
                sensorCounters[sensor] = 0
            } else {
                sensorCounters[sensor] = counter
            }
        }
    }

    private fun evaluateCoreBehaviors(entity: EntityCreature) {
        for (behavior in coreBehaviors) {
            if (behavior in runningCoreBehaviors) continue
            val counter = (coreBehaviorCounters[behavior] ?: 0) + 1
            coreBehaviorCounters[behavior] = if (counter < behavior.period) counter else 0
            if (counter < behavior.period) continue
            if (behavior.evaluate(entity)) {
                behavior.onStart(entity)
                behavior.behaviorState = BehaviorState.ACTIVE
                runningCoreBehaviors.add(behavior)
            }
        }
    }

    private fun evaluateBehaviors(entity: EntityCreature) {
        val candidates = mutableSetOf<Behavior>()
        var highestPriority = Int.MIN_VALUE

        for (behavior in behaviors) {
            if (behavior in runningBehaviors) continue
            val counter = (behaviorCounters[behavior] ?: 0) + 1
            behaviorCounters[behavior] = if (counter < behavior.period) counter else 0
            if (counter < behavior.period) continue
            if (!behavior.evaluate(entity)) continue

            when {
                behavior.priority > highestPriority -> {
                    candidates.clear()
                    highestPriority = behavior.priority
                    candidates.add(behavior)
                }

                behavior.priority == highestPriority -> candidates.add(behavior)
            }
        }

        if (candidates.isEmpty()) return

        val runningPriority = runningBehaviors.firstOrNull()?.priority ?: Int.MIN_VALUE
        when {
            highestPriority > runningPriority -> {
                interruptRunningBehaviors(entity)
                startBehaviors(entity, candidates)
            }

            highestPriority == runningPriority -> startBehaviors(entity, candidates)
        }
    }

    private fun interruptRunningBehaviors(entity: EntityCreature) {
        runningBehaviors.forEach { it.onInterrupt(entity); it.behaviorState = BehaviorState.STOP }
        runningBehaviors.clear()
    }

    private fun startBehaviors(entity: EntityCreature, toStart: Set<Behavior>) {
        toStart.forEach {
            it.onStart(entity)
            it.behaviorState = BehaviorState.ACTIVE
            runningBehaviors.add(it)
        }
    }

    private fun tickRunning(entity: EntityCreature, running: MutableSet<Behavior>) {
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