package org.kanelucky.mobmind.core

import net.minestom.server.entity.EntityCreature
import org.kanelucky.mobmind.api.entity.MobMindInitializer
import org.kanelucky.mobmind.api.entity.ai.behavior.BehaviorFactory
import org.kanelucky.mobmind.api.entity.ai.behavior.Behaviors
import org.kanelucky.mobmind.api.entity.ai.behaviorgroup.BehaviorGroupBuilderProvider
import org.kanelucky.mobmind.api.entity.ai.controller.ControllerFactory
import org.kanelucky.mobmind.api.entity.ai.controller.Controllers
import org.kanelucky.mobmind.api.entity.ai.evaluator.EvaluatorFactory
import org.kanelucky.mobmind.api.entity.ai.evaluator.Evaluators
import org.kanelucky.mobmind.api.entity.ai.executor.EatGrassCallback
import org.kanelucky.mobmind.api.entity.ai.executor.ExecutorFactory
import org.kanelucky.mobmind.api.entity.ai.executor.Executors
import org.kanelucky.mobmind.api.entity.ai.executor.MeleeAttackCallback
import org.kanelucky.mobmind.api.entity.ai.executor.ProjectileSupplier
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryType
import org.kanelucky.mobmind.api.entity.ai.sensor.SensorFactory
import org.kanelucky.mobmind.api.entity.ai.sensor.Sensors
import org.kanelucky.mobmind.core.entity.ai.behavior.WeightedMultiBehavior
import org.kanelucky.mobmind.core.entity.ai.behaviorgroup.BehaviorGroupImpl
import org.kanelucky.mobmind.core.entity.ai.controller.LookController
import org.kanelucky.mobmind.core.entity.ai.controller.WalkController
import org.kanelucky.mobmind.core.entity.ai.evaluator.InLoveEvaluator
import org.kanelucky.mobmind.core.entity.ai.evaluator.PanicEvaluator
import org.kanelucky.mobmind.core.entity.ai.evaluator.ProbabilityEvaluator
import org.kanelucky.mobmind.core.entity.ai.executor.*
import org.kanelucky.mobmind.core.entity.ai.sensor.NearestFeedingPlayerSensor
import org.kanelucky.mobmind.core.entity.ai.sensor.NearestPlayerSensor

/**
 * Registers all core implementations into the API factories
 *
 * @author Kanelucky
 */
class CoreInitializer : MobMindInitializer {

    override fun initialize() {
        BehaviorGroupBuilderProvider.register { BehaviorGroupImpl.Builder() }

        Controllers.register(object : ControllerFactory {
            override fun createWalk() = WalkController()
            override fun createLook() = LookController()
        })

        Behaviors.register(BehaviorFactory { behaviors, priority, weight, period ->
            WeightedMultiBehavior(behaviors, priority, weight, period)
        })

        Executors.register(object : ExecutorFactory {
            override fun panic(panicSpeed: Double, normalSpeed: Double, range: Int) =
                PanicExecutor(panicSpeed, normalSpeed, range)

            override fun idle(minTicks: Int, maxTicks: Int) =
                IdleExecutor(minTicks, maxTicks)

            override fun roam(speed: Double, normalSpeed: Double, maxRange: Int, frequency: Int,
                              calNextImmediately: Boolean, runningTime: Int,
                              avoidWater: Boolean, maxRetry: Int) =
                FlatRandomRoamExecutor(speed, normalSpeed, maxRange, frequency, calNextImmediately, runningTime, avoidWater, maxRetry)

            override fun followEntity(memory: MemoryType<out Any?>, speed: Double, normalSpeed: Double,
                                      maxRangeSq: Double, minRangeSq: Double) =
                FollowEntityExecutor(memory, speed, normalSpeed, maxRangeSq, minRangeSq)

            override fun lookAtEntity(memory: MemoryType<out Any?>, duration: Int) =
                LookAtEntityExecutor(memory, duration)

            override fun lookAround(minTicks: Int, maxTicks: Int) =
                LookAroundExecutor(minTicks, maxTicks)

            override fun wander(radius: Double) =
                WanderExecutor(radius)

            override fun eatGrass(duration: Int) =
                EatGrassExecutor(duration)

            override fun eatGrass(duration: Int, callback: EatGrassCallback) =
                object : EatGrassExecutor(duration) {
                    override fun onEatGrass(entity: EntityCreature) = callback.onEatGrass(entity)
                }

            override fun breeding(duration: Int, speed: Double, normalSpeed: Double) =
                EntityBreedingExecutor(duration, speed, normalSpeed)

            override fun moveToTarget(memory: MemoryType<out Any?>, speed: Double, normalSpeed: Double,
                                      maxRangeSq: Double, minRangeSq: Double) =
                MoveToTargetExecutor(memory, speed, normalSpeed, maxRangeSq, minRangeSq)

            override fun meleeAttack(
                memory: MemoryType<out Any?>,
                speed: Double,
                normalSpeed: Double,
                maxSenseRangeSq: Double,
                attackRangeSq: Double,
                attackCooldown: Int,
                clearDataWhenLose: Boolean,
                callback: MeleeAttackCallback?
            ) = MeleeAttackExecutor(memory, speed, normalSpeed, maxSenseRangeSq, attackRangeSq, attackCooldown, clearDataWhenLose) {
                    attacker, target -> callback?.onAttack(attacker, target)
            }

            override fun beamAttack(
                targetMemory: MemoryType<out Any?>,
                maxRangeSq: Double,
                coolDownTick: Int,
                attackDelay: Int,
                clearDataWhenLose: Boolean
            ) = BeamAttackExecutor(targetMemory, maxRangeSq, coolDownTick, attackDelay, clearDataWhenLose)

            override fun shootProjectile(
                targetMemory: MemoryType<out Any?>,
                speed: Double,
                normalSpeed: Double,
                maxShootRangeSq: Double,
                coolDownTick: Int,
                attackDelay: Int,
                clearDataWhenLose: Boolean,
                projectileSupplier: ProjectileSupplier
            ) = ProjectileShootExecutor(
                targetMemory, speed, normalSpeed, maxShootRangeSq,
                coolDownTick, attackDelay, clearDataWhenLose
            ) { projectileSupplier.create(it) }

            override fun jump(
                jumpInterval: Int,
                jumpDelay: Int,
                jumpPower: Double,
                jumpPowerVariance: Double
            ) = JumpExecutor(jumpInterval, jumpDelay, jumpPower, jumpPowerVariance)

            override fun flee(
                targetMemory: MemoryType<out Any?>,
                fleeSpeed: Double,
                normalSpeed: Double,
                minFleeRangeSq: Double,
                maxFleeRangeSq: Double,
                recalculateInterval: Int
            ) = FleeExecutor(targetMemory, fleeSpeed, normalSpeed, minFleeRangeSq, maxFleeRangeSq, recalculateInterval)
        })

        Evaluators.register(object : EvaluatorFactory {
            override fun panic() = PanicEvaluator()
            override fun inLove() = InLoveEvaluator()
            override fun probability(chance: Int, outOf: Int) = ProbabilityEvaluator(chance, outOf)
        })

        Sensors.register(object : SensorFactory {
            override fun nearestPlayer(range: Double, minRange: Double, period: Int) =
                NearestPlayerSensor(range, minRange, period)
            override fun nearestFeedingPlayer(range: Double, period: Int) =
                NearestFeedingPlayerSensor(range, period)
        })
    }
}