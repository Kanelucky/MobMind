package org.kanelucky.mobmind.api.entity.ai.evaluator;

public interface EvaluatorFactory {
    BehaviorEvaluator panic();
    BehaviorEvaluator inLove();
    BehaviorEvaluator probability(int chance, int outOf);
}
