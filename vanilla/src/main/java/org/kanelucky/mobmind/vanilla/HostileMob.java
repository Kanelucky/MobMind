package org.kanelucky.mobmind.vanilla;

import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.entity.damage.DamageType;
import org.kanelucky.mobmind.api.entity.IntelligentEntity;
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
import org.kanelucky.mobmind.core.entity.ai.executor.EntityControlHelper;

import java.util.Set;

/**
 * Base class for hostile mobs.
 *
 * @author Kanelucky
 */
public abstract class HostileMob extends MobEntity {

    private final BehaviorGroup behaviorGroup;
    private int attackCooldown = 0;

    public HostileMob(EntityType entityType) {
        super(entityType);
        this.behaviorGroup = buildBaseBehaviorGroup();
        this.behaviorGroup.setEntity(this);
    }

    @Override
    public BehaviorGroup getBehaviorGroup() { return behaviorGroup; }

    protected BehaviorGroupBuilder addBaseBehaviors(BehaviorGroupBuilder builder) {
        return builder
                .sensor(Sensors.nearestPlayer())
                .behavior(
                        BehaviorImpl.builder()
                                .executor(Executors.meleeAttack(MemoryTypes.NEAREST_PLAYER))
                                .evaluator(entity -> {
                                    if (!(entity instanceof IntelligentEntity e)) return false;
                                    return e.getBehaviorGroup().getMemoryStorage().get(MemoryTypes.NEAREST_PLAYER) != null;
                                })
                                .priority(3).period(1).build()
                )
                .behavior(
                        BehaviorImpl.builder()
                                .executor(entity -> {
                                    if (!(entity instanceof LivingEntity mob)) return false;
                                    if (!(entity instanceof IntelligentEntity e)) return false;

                                    var target = e.getBehaviorGroup()
                                            .getMemoryStorage()
                                            .get(MemoryTypes.NEAREST_PLAYER);

                                    if (target instanceof LivingEntity livingTarget) {
                                        double distanceSq = mob.getDistanceSquared(livingTarget.getPosition());
                                        double attackRangeSq = 2.5;

                                        if (distanceSq <= attackRangeSq && attackCooldown <= 0) {
                                            mob.swingMainHand();
                                            livingTarget.damage(Damage.fromEntity(mob, 4f));
                                            attackCooldown = 20;
                                            return true;
                                        }
                                    }

                                    if (attackCooldown > 0) {
                                        attackCooldown--;
                                    }
                                    return false;
                                })
                                .evaluator(entity -> {
                                    if (!(entity instanceof IntelligentEntity e)) return false;
                                    return e.getBehaviorGroup()
                                            .getMemoryStorage()
                                            .get(MemoryTypes.NEAREST_PLAYER) != null;
                                })
                                .priority(3)
                                .period(1)
                                .build()
                )
                .behavior(
                        BehaviorImpl.builder()
                                .executor(Executors.lookAtEntity(MemoryTypes.NEAREST_PLAYER, 60))
                                .evaluator(entity -> {
                                    if (!(entity instanceof IntelligentEntity e)) return false;
                                    return e.getBehaviorGroup().getMemoryStorage()
                                            .get(MemoryTypes.NEAREST_PLAYER) != null;
                                })
                                .priority(2).period(1).build()
                )
                .behavior(
                        Behaviors.weighted(
                                Set.<Behavior>of(
                                        BehaviorImpl.builder()
                                                .executor(Executors.idle(20, 60))
                                                .evaluator(entity -> true)
                                                .priority(1).weight(1).period(1).build(),
                                        BehaviorImpl.builder()
                                                .executor(Executors.roam(0.1, 0.1, 8, 20, false, 100, false, 10))
                                                .evaluator(entity -> true)
                                                .priority(1).weight(3).period(1).build()
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