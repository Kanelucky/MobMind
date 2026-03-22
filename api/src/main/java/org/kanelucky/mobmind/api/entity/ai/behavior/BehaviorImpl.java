package org.kanelucky.mobmind.api.entity.ai.behavior;

import net.minestom.server.entity.EntityCreature;
import org.kanelucky.mobmind.api.entity.ai.evaluator.BehaviorEvaluator;
import org.kanelucky.mobmind.api.entity.ai.executor.BehaviorExecutor;

/**
 * A simple behavior that delegates evaluation and execution to separate
 * BehaviorEvaluator and BehaviorExecutor instances.
 *
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 *
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
public class BehaviorImpl implements Behavior {

    private final BehaviorExecutor executor;
    private final BehaviorEvaluator evaluator;
    private final int priority;
    private final int weight;
    private final int period;
    private BehaviorState behaviorState = BehaviorState.STOP;

    private BehaviorImpl(BehaviorExecutor executor, BehaviorEvaluator evaluator, int priority, int weight, int period) {
        this.executor = executor;
        this.evaluator = evaluator;
        this.priority = priority;
        this.weight = weight;
        this.period = period;
    }

    @Override public int getPriority() { return priority; }
    @Override public int getWeight() { return weight; }
    @Override public int getPeriod() { return period; }
    @Override public BehaviorState getBehaviorState() { return behaviorState; }
    @Override public void setBehaviorState(BehaviorState state) { this.behaviorState = state; }

    @Override public boolean evaluate(EntityCreature entity) { return evaluator.evaluate(entity); }
    @Override public boolean execute(EntityCreature entity) { return executor.execute(entity); }
    @Override public void onStart(EntityCreature entity) { executor.onStart(entity); }
    @Override public void onStop(EntityCreature entity) { executor.onStop(entity); }
    @Override public void onInterrupt(EntityCreature entity) { executor.onInterrupt(entity); }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private BehaviorExecutor executor;
        private BehaviorEvaluator evaluator;
        private int priority = 0;
        private int weight = 1;
        private int period = 1;

        public Builder executor(BehaviorExecutor e) { this.executor = e; return this; }
        public Builder evaluator(BehaviorEvaluator e) { this.evaluator = e; return this; }
        public Builder priority(int p) { this.priority = p; return this; }
        public Builder weight(int w) { this.weight = w; return this; }
        public Builder period(int p) { this.period = p; return this; }

        public BehaviorImpl build() {
            if (executor == null) throw new IllegalStateException("executor is required");
            if (evaluator == null) throw new IllegalStateException("evaluator is required");
            return new BehaviorImpl(executor, evaluator, priority, weight, period);
        }
    }
}
