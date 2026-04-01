package org.kanelucky.mobmind.api.entity.ai.executor;

import org.jetbrains.annotations.Nullable;
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryType;

public interface ExecutorFactory {
    BehaviorExecutor panic(double panicSpeed, double normalSpeed, int range);
    BehaviorExecutor idle(int minTicks, int maxTicks);
    BehaviorExecutor roam(double speed, double normalSpeed, int maxRange, int frequency,
                          boolean calNextImmediately, int runningTime,
                          boolean avoidWater, int maxRetry);
    BehaviorExecutor followEntity(MemoryType<?> memory, double speed, double normalSpeed,
                                  double maxRangeSq, double minRangeSq);
    BehaviorExecutor lookAtEntity(MemoryType<?> memory, int duration);
    BehaviorExecutor lookAround(int minTicks, int maxTicks);
    BehaviorExecutor wander(double radius);
    BehaviorExecutor eatGrass(int duration);
    BehaviorExecutor eatGrass(int duration, EatGrassCallback callback);
    BehaviorExecutor breeding(int duration, double speed, double normalSpeed);
    BehaviorExecutor moveToTarget(MemoryType<?> memory, double speed, double normalSpeed,
                                  double maxRangeSq, double minRangeSq);
    BehaviorExecutor meleeAttack(MemoryType<?> memory, double speed, double normalSpeed,
                                 double maxSenseRangeSq, double attackRangeSq,
                                 int attackCooldown, boolean clearDataWhenLose,
                                 @Nullable MeleeAttackCallback callback);

    BehaviorExecutor beamAttack(MemoryType<?> targetMemory, double maxRangeSq,
                                int coolDownTick, int attackDelay, boolean clearDataWhenLose);

    BehaviorExecutor shootProjectile(MemoryType<?> targetMemory, double speed, double normalSpeed,
                                     double maxShootRangeSq, int coolDownTick, int attackDelay,
                                     boolean clearDataWhenLose, ProjectileSupplier projectileSupplier);

    BehaviorExecutor jump(int jumpInterval, int jumpDelay, double jumpPower, double jumpPowerVariance);

}
