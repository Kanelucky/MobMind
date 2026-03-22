package org.kanelucky.mobmind.api.entity.ai.behaviorgroup;

import java.util.ServiceLoader;

public final class BehaviorGroupBuilderProvider {

    private static BehaviorGroupBuilderFactory factory;

    private BehaviorGroupBuilderProvider() {}

    public static void register(BehaviorGroupBuilderFactory f) {
        factory = f;
    }

    public static BehaviorGroupBuilder get() {
        if (factory == null) throw new IllegalStateException(
                "No BehaviorGroupBuilderFactory registered. Did you include mobmind-core?"
        );
        return factory.create();
    }
}
