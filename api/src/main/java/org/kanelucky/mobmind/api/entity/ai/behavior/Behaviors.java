package org.kanelucky.mobmind.api.entity.ai.behavior;

import java.util.Set;

public final class Behaviors {

    private static BehaviorFactory factory;

    private Behaviors() {}

    public static void register(BehaviorFactory f) { factory = f; }

    private static BehaviorFactory factory() {
        if (factory == null) throw new IllegalStateException(
                "No BehaviorFactory registered. Did you include mobmind-core?"
        );
        return factory;
    }

    public static Behavior weighted(Set<Behavior> behaviors, int priority, int period) {
        return factory().weighted(behaviors, priority, 1, period);
    }

    public static Behavior weighted(Set<Behavior> behaviors, int priority, int weight, int period) {
        return factory().weighted(behaviors, priority, weight, period);
    }
}
