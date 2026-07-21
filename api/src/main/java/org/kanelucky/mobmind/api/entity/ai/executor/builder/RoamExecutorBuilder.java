package org.kanelucky.mobmind.api.entity.ai.executor.builder;

import org.kanelucky.mobmind.api.entity.ai.executor.BehaviorExecutor;
import org.kanelucky.mobmind.api.entity.ai.executor.Executors;

public final class RoamExecutorBuilder {

    private double speed = 0.1;
    private double normalSpeed = 0.1;
    private int maxRange = 10;
    private int frequency = 20;
    private boolean calNextImmediately = true;
    private int runningTime = 100;
    private boolean avoidWater = true;
    private int maxRetry = 10;

    public RoamExecutorBuilder() {
    }

    public RoamExecutorBuilder speed(double speed) {
        this.speed = speed;
        return this;
    }

    public RoamExecutorBuilder normalSpeed(double normalSpeed) {
        this.normalSpeed = normalSpeed;
        return this;
    }

    public RoamExecutorBuilder maxRange(int maxRange) {
        this.maxRange = maxRange;
        return this;
    }

    public RoamExecutorBuilder frequency(int frequency) {
        this.frequency = frequency;
        return this;
    }

    public RoamExecutorBuilder calNextImmediately(boolean value) {
        this.calNextImmediately = value;
        return this;
    }

    public RoamExecutorBuilder runningTime(int runningTime) {
        this.runningTime = runningTime;
        return this;
    }

    public RoamExecutorBuilder avoidWater(boolean avoidWater) {
        this.avoidWater = avoidWater;
        return this;
    }

    public RoamExecutorBuilder maxRetry(int maxRetry) {
        this.maxRetry = maxRetry;
        return this;
    }

    public BehaviorExecutor build() {
        return Executors.roam(speed, normalSpeed, maxRange, frequency, calNextImmediately, runningTime, avoidWater, maxRetry);
    }
}