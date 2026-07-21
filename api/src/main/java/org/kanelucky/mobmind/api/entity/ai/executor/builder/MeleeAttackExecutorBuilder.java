package org.kanelucky.mobmind.api.entity.ai.executor.builder;

import org.kanelucky.mobmind.api.entity.ai.executor.BehaviorExecutor;
import org.kanelucky.mobmind.api.entity.ai.executor.Executors;
import org.kanelucky.mobmind.api.entity.ai.executor.MeleeAttackCallback;
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryType;

public final class MeleeAttackExecutorBuilder {

    private final MemoryType<?> memory;
    private double speed = 0.1;
    private double normalSpeed = 0.1;
    private double maxSenseRangeSq = 256.0;
    private double attackRangeSq = 2.5;
    private int attackCooldown = 20;
    private boolean clearDataWhenLose = false;
    private MeleeAttackCallback callback = null;

    public MeleeAttackExecutorBuilder(MemoryType<?> memory) {
        this.memory = memory;
    }

    public MeleeAttackExecutorBuilder speed(double speed) {
        this.speed = speed;
        return this;
    }

    public MeleeAttackExecutorBuilder normalSpeed(double normalSpeed) {
        this.normalSpeed = normalSpeed;
        return this;
    }

    public MeleeAttackExecutorBuilder maxSenseRangeSq(double maxSenseRangeSq) {
        this.maxSenseRangeSq = maxSenseRangeSq;
        return this;
    }

    public MeleeAttackExecutorBuilder attackRangeSq(double attackRangeSq) {
        this.attackRangeSq = attackRangeSq;
        return this;
    }

    public MeleeAttackExecutorBuilder attackCooldown(int attackCooldown) {
        this.attackCooldown = attackCooldown;
        return this;
    }

    public MeleeAttackExecutorBuilder clearDataWhenLose(boolean value) {
        this.clearDataWhenLose = value;
        return this;
    }

    public MeleeAttackExecutorBuilder onAttack(MeleeAttackCallback callback) {
        this.callback = callback;
        return this;
    }

    public BehaviorExecutor build() {
        return Executors.meleeAttack(memory, speed, normalSpeed, maxSenseRangeSq, attackRangeSq, attackCooldown, clearDataWhenLose, callback);
    }
}