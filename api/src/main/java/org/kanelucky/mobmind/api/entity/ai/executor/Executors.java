package org.kanelucky.mobmind.api.entity.ai.executor;

import net.minestom.server.entity.Entity;
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryType;

public final class Executors {

    private static ExecutorFactory factory;

    private Executors() {}

    public static void register(ExecutorFactory f) { factory = f; }

    private static ExecutorFactory factory() {
        if (factory == null) throw new IllegalStateException(
                "No ExecutorFactory registered. Did you include mobmind-core?"
        );
        return factory;
    }

    public static BehaviorExecutor panic() { return factory().panic(0.15, 0.1, 8); }
    public static BehaviorExecutor panic(double panicSpeed, double normalSpeed, int range) {
        return factory().panic(panicSpeed, normalSpeed, range);
    }

    public static BehaviorExecutor idle() { return factory().idle(20, 60); }
    public static BehaviorExecutor idle(int minTicks, int maxTicks) {
        return factory().idle(minTicks, maxTicks);
    }

    public static BehaviorExecutor roam() { return factory().roam(0.1, 0.1, 10, 20, true, 100, true, 10); }
    public static BehaviorExecutor roam(double speed, double normalSpeed, int maxRange, int frequency,
                                        boolean calNextImmediately, int runningTime,
                                        boolean avoidWater, int maxRetry) {
        return factory().roam(speed, normalSpeed, maxRange, frequency, calNextImmediately, runningTime, avoidWater, maxRetry);
    }

    public static BehaviorExecutor followEntity(MemoryType<? extends Entity> memory) {
        return factory().followEntity(memory, 0.125, 0.1, 256.0, 4.0);
    }
    public static BehaviorExecutor followEntity(MemoryType<? extends Entity> memory,
                                                double speed, double normalSpeed,
                                                double maxRangeSq, double minRangeSq) {
        return factory().followEntity(memory, speed, normalSpeed, maxRangeSq, minRangeSq);
    }

    public static BehaviorExecutor lookAtEntity(MemoryType<? extends Entity> memory) {
        return factory().lookAtEntity(memory, 60);
    }
    public static BehaviorExecutor lookAtEntity(MemoryType<? extends Entity> memory, int duration) {
        return factory().lookAtEntity(memory, duration);
    }

    public static BehaviorExecutor lookAround() { return factory().lookAround(20, 60); }
    public static BehaviorExecutor lookAround(int minTicks, int maxTicks) {
        return factory().lookAround(minTicks, maxTicks);
    }

    public static BehaviorExecutor wander() { return factory().wander(10.0); }
    public static BehaviorExecutor wander(double radius) { return factory().wander(radius); }

    public static BehaviorExecutor eatGrass() { return factory().eatGrass(40); }
    public static BehaviorExecutor eatGrass(int duration) { return factory().eatGrass(duration); }
    public static BehaviorExecutor eatGrass(int duration, EatGrassCallback callback) {
        return factory().eatGrass(duration, callback);
    }

    public static BehaviorExecutor breeding() { return factory().breeding(60, 0.3, 0.1); }
    public static BehaviorExecutor breeding(int duration, double speed, double normalSpeed) {
        return factory().breeding(duration, speed, normalSpeed);
    }

    public static BehaviorExecutor moveToTarget(MemoryType<? extends net.minestom.server.coordinate.Point> memory) {
        return factory().moveToTarget(memory, 0.2, 0.1, 256.0, 0.0);
    }
    public static BehaviorExecutor moveToTarget(MemoryType<? extends net.minestom.server.coordinate.Point> memory,
                                                double speed, double normalSpeed,
                                                double maxRangeSq, double minRangeSq) {
        return factory().moveToTarget(memory, speed, normalSpeed, maxRangeSq, minRangeSq);
    }

//    public static BehaviorExecutor meleeAttack(MemoryType<?> memory) {
//        return factory().meleeAttack(memory, 0.15, 0.1, 256.0, 2.5, 20, false);
//    }
//
//    public static BehaviorExecutor meleeAttack(MemoryType<?> memory, double speed, double normalSpeed,
//                                               double maxSenseRangeSq, double attackRangeSq,
//                                               int attackCooldown, boolean clearDataWhenLose) {
//        return factory().meleeAttack(memory, speed, normalSpeed, maxSenseRangeSq, attackRangeSq, attackCooldown, clearDataWhenLose);
//    }

    public static BehaviorExecutor meleeAttack(MemoryType<?> memory) {
        return factory().meleeAttack(memory, 0.1, 0.1, 256.0, 2.5, 20, false, null);
    }

    public static BehaviorExecutor meleeAttack(MemoryType<?> memory, double speed, double normalSpeed,
                                               double maxSenseRangeSq, double attackRangeSq,
                                               int attackCooldown, boolean clearDataWhenLose) {
        return factory().meleeAttack(memory, speed, normalSpeed, maxSenseRangeSq,
                attackRangeSq, attackCooldown, clearDataWhenLose, null);
    }

    public static BehaviorExecutor meleeAttack(MemoryType<?> memory, double speed, double normalSpeed,
                                               double maxSenseRangeSq, double attackRangeSq,
                                               int attackCooldown, boolean clearDataWhenLose,
                                               MeleeAttackCallback callback) {
        return factory().meleeAttack(memory, speed, normalSpeed, maxSenseRangeSq,
                attackRangeSq, attackCooldown, clearDataWhenLose, callback);
    }

    public static BehaviorExecutor beamAttack(MemoryType<?> targetMemory) {
        return factory().beamAttack(targetMemory, 256.0, 40, 20, false);
    }

    public static BehaviorExecutor beamAttack(MemoryType<?> targetMemory, double maxRangeSq,
                                              int coolDownTick, int attackDelay,
                                              boolean clearDataWhenLose) {
        return factory().beamAttack(targetMemory, maxRangeSq, coolDownTick, attackDelay, clearDataWhenLose);
    }

    public static BehaviorExecutor shootProjectile(MemoryType<?> targetMemory,
                                                   ProjectileSupplier projectileSupplier) {
        return factory().shootProjectile(targetMemory, 0.1, 0.1, 256.0, 40, 20, false, projectileSupplier);
    }

    public static BehaviorExecutor shootProjectile(MemoryType<?> targetMemory,
                                                   double speed, double normalSpeed,
                                                   double maxShootRangeSq, int coolDownTick,
                                                   int attackDelay, boolean clearDataWhenLose,
                                                   ProjectileSupplier projectileSupplier) {
        return factory().shootProjectile(targetMemory, speed, normalSpeed, maxShootRangeSq,
                coolDownTick, attackDelay, clearDataWhenLose, projectileSupplier);
    }

    public static BehaviorExecutor jump() {
        return factory().jump(80, 10, 0.6, 0.5);
    }

    public static BehaviorExecutor jump(int jumpInterval, int jumpDelay,
                                        double jumpPower, double jumpPowerVariance) {
        return factory().jump(jumpInterval, jumpDelay, jumpPower, jumpPowerVariance);
    }
}