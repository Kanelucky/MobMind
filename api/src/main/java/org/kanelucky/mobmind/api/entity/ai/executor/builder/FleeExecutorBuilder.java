package org.kanelucky.mobmind.api.entity.ai.executor.builder;

import org.kanelucky.mobmind.api.entity.ai.executor.BehaviorExecutor;
import org.kanelucky.mobmind.api.entity.ai.executor.Executors;
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryType;

public final class FleeExecutorBuilder {

    private final MemoryType<?> memory;
    private double fleeSpeed = 0.15;
    private double normalSpeed = 0.1;
    private double minFleeRangeSq = 64.0;
    private double maxFleeRangeSq = 256.0;
    private int recalculateInterval = 10;

    public FleeExecutorBuilder(MemoryType<?> memory) {
        this.memory = memory;
    }

    public FleeExecutorBuilder fleeSpeed(double fleeSpeed) {
        this.fleeSpeed = fleeSpeed;
        return this;
    }

    public FleeExecutorBuilder normalSpeed(double normalSpeed) {
        this.normalSpeed = normalSpeed;
        return this;
    }

    public FleeExecutorBuilder minFleeRangeSq(double minFleeRangeSq) {
        this.minFleeRangeSq = minFleeRangeSq;
        return this;
    }

    public FleeExecutorBuilder maxFleeRangeSq(double maxFleeRangeSq) {
        this.maxFleeRangeSq = maxFleeRangeSq;
        return this;
    }

    public FleeExecutorBuilder recalculateInterval(int recalculateInterval) {
        this.recalculateInterval = recalculateInterval;
        return this;
    }

    public BehaviorExecutor build() {
        return Executors.flee(memory, fleeSpeed, normalSpeed, minFleeRangeSq, maxFleeRangeSq, recalculateInterval);
    }
}