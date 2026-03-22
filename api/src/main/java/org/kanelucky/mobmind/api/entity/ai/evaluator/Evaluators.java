package org.kanelucky.mobmind.api.entity.ai.evaluator;

public final class Evaluators {

    private static EvaluatorFactory factory;

    private Evaluators() {}

    public static void register(EvaluatorFactory f) { factory = f; }

    private static EvaluatorFactory factory() {
        if (factory == null) throw new IllegalStateException(
                "No EvaluatorFactory registered. Did you include core?"
        );
        return factory;
    }

    public static BehaviorEvaluator panic() { return factory().panic(); }
    public static BehaviorEvaluator inLove() { return factory().inLove(); }
    public static BehaviorEvaluator probability(int chance, int outOf) { return factory().probability(chance, outOf); }
}
