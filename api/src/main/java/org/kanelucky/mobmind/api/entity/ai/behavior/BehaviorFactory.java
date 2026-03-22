package org.kanelucky.mobmind.api.entity.ai.behavior;

import java.util.Set;

public interface BehaviorFactory {
    Behavior weighted(Set<Behavior> behaviors, int priority, int weight, int period);
}
