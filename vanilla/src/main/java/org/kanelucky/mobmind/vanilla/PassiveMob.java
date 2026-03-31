package org.kanelucky.mobmind.vanilla;


import net.minestom.server.entity.EntityType;
import org.kanelucky.mobmind.api.entity.IntelligentEntity;
import org.kanelucky.mobmind.api.entity.ai.Breedable;
import org.kanelucky.mobmind.api.entity.ai.Feedable;
import org.kanelucky.mobmind.api.entity.ai.Offspring;
import org.kanelucky.mobmind.api.entity.ai.behavior.Behavior;
import org.kanelucky.mobmind.api.entity.ai.behavior.BehaviorImpl;
import org.kanelucky.mobmind.api.entity.ai.behavior.Behaviors;
import org.kanelucky.mobmind.api.entity.ai.behaviorgroup.BehaviorGroup;
import org.kanelucky.mobmind.api.entity.ai.behaviorgroup.BehaviorGroupBuilder;
import org.kanelucky.mobmind.api.entity.ai.controller.Controllers;
import org.kanelucky.mobmind.api.entity.ai.evaluator.Evaluators;
import org.kanelucky.mobmind.api.entity.ai.executor.Executors;
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryTypes;
import org.kanelucky.mobmind.api.entity.ai.sensor.Sensors;

import java.util.Set;

/**
 * @author Kanelucky
 */
public abstract class PassiveMob extends AnimalEntity implements Breedable, Feedable, Offspring {

    private final BehaviorGroup behaviorGroup;

    public PassiveMob(EntityType entityType) {
        super(entityType);
        this.behaviorGroup = buildBaseBehaviorGroup();
        this.behaviorGroup.setEntity(this);
    }

    @Override
    public BehaviorGroup getBehaviorGroup() { return behaviorGroup; }

    protected BehaviorGroupBuilder addBaseBehaviors(BehaviorGroupBuilder builder) {
        return builder
                .sensor(Sensors.nearestPlayer())
                .sensor(Sensors.nearestFeedingPlayer())
                .behavior(
                        BehaviorImpl.builder()
                                .executor(Executors.panic())
                                .evaluator(Evaluators.panic())
                                .priority(4).period(1).build()
                )
                .behavior(
                        BehaviorImpl.builder()
                                .executor(Executors.breeding())
                                .evaluator(Evaluators.inLove())
                                .priority(3).period(1).build()
                )
                .behavior(
                        BehaviorImpl.builder()
                                .executor(Executors.followEntity(
                                        MemoryTypes.NEAREST_FEEDING_PLAYER, 0.125, 0.1, 256.0, 2.0
                                ))
                                .evaluator(entity -> {
                                    if (!(entity instanceof IntelligentEntity e)) return false;
                                    return e.getBehaviorGroup().getMemoryStorage()
                                            .get(MemoryTypes.NEAREST_FEEDING_PLAYER) != null;
                                })
                                .priority(4).period(1).build()
                )
                .behavior(
                        BehaviorImpl.builder()
                                .executor(Executors.lookAtEntity(MemoryTypes.NEAREST_PLAYER, 60))
                                .evaluator(entity -> {
                                    if (!(entity instanceof IntelligentEntity e)) return false;
                                    if (e.getBehaviorGroup().getMemoryStorage()
                                            .get(MemoryTypes.NEAREST_PLAYER) == null) return false;
                                    return Math.random() < 0.05;
                                })
                                .priority(2).period(10).build()
                )
                .behavior(
                        Behaviors.weighted(
                                Set.<Behavior>of(
                                        BehaviorImpl.builder()
                                                .executor(Executors.idle(20, 100))
                                                .evaluator(entity -> true)
                                                .priority(1).weight(1).period(1).build(),
                                        BehaviorImpl.builder()
                                                .executor(Executors.roam(0.1, 0.1, 10, 20, true, 100, true, 10))
                                                .evaluator(entity -> true)
                                                .priority(1).weight(2).period(1).build()
                                ),
                                1, 40
                        )
                )
                .controller(Controllers.walk())
                .controller(Controllers.look());
    }

    protected BehaviorGroup buildBaseBehaviorGroup() {
        return addBaseBehaviors(BehaviorGroup.builder()).build();
    }
}