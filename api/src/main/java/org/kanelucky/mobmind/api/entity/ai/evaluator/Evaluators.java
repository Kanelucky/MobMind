package org.kanelucky.mobmind.api.entity.ai.evaluator;

import org.kanelucky.mobmind.api.entity.IntelligentEntity;
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryType;

public final class Evaluators {

    private static EvaluatorFactory factory;

    private Evaluators() {
    }

    public static void register(EvaluatorFactory f) {
        factory = f;
    }

    private static EvaluatorFactory factory() {
        if (factory == null) throw new IllegalStateException(
                "No EvaluatorFactory registered. Did you include core?");
        return factory;
    }

    public static BehaviorEvaluator panic() {
        return factory().panic();
    }

    public static BehaviorEvaluator inLove() {
        return factory().inLove();
    }

    public static BehaviorEvaluator probability(int chance, int outOf) {
        return factory().probability(chance, outOf);
    }

    /**
     * Returns true when the given memory type has a non-null value.
     */
    public static BehaviorEvaluator hasMemory(MemoryType<?> type) {
        return entity -> {
            if (!(entity instanceof IntelligentEntity e)) return false;
            return !e.getBehaviorGroup().getMemoryStorage().isEmpty(type);
        };
    }

    /**
     * Returns true when the given memory type is null or empty.
     */
    public static BehaviorEvaluator lacksMemory(MemoryType<?> type) {
        return entity -> {
            if (!(entity instanceof IntelligentEntity e)) return false;
            return e.getBehaviorGroup().getMemoryStorage().isEmpty(type);
        };
    }

    /**
     * Returns true when all given memory types have non-null values.
     */
    public static BehaviorEvaluator hasAllMemory(MemoryType<?>... types) {
        return entity -> {
            if (!(entity instanceof IntelligentEntity e)) return false;
            var storage = e.getBehaviorGroup().getMemoryStorage();
            for (MemoryType<?> type : types) {
                if (storage.isEmpty(type)) return false;
            }
            return true;
        };
    }

    /**
     * Returns true when any of the given memory types has a non-null value.
     */
    public static BehaviorEvaluator hasAnyMemory(MemoryType<?>... types) {
        return entity -> {
            if (!(entity instanceof IntelligentEntity e)) return false;
            var storage = e.getBehaviorGroup().getMemoryStorage();
            for (MemoryType<?> type : types) {
                if (!storage.isEmpty(type)) return true;
            }
            return false;
        };
    }
}
