package org.kanelucky.mobmind.vanilla.hostile;

import net.kyori.adventure.key.Key;
import net.minestom.server.entity.EntityProjectile;
import net.minestom.server.entity.EntityType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import org.kanelucky.mobmind.api.entity.IntelligentEntity;
import org.kanelucky.mobmind.api.entity.ai.behavior.Behavior;
import org.kanelucky.mobmind.api.entity.ai.behavior.BehaviorImpl;
import org.kanelucky.mobmind.api.entity.ai.behavior.Behaviors;
import org.kanelucky.mobmind.api.entity.ai.behaviorgroup.BehaviorGroup;
import org.kanelucky.mobmind.api.entity.ai.controller.Controllers;
import org.kanelucky.mobmind.api.entity.ai.executor.Executors;
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryTypes;
import org.kanelucky.mobmind.api.entity.ai.sensor.Sensors;
import org.kanelucky.mobmind.vanilla.HostileMob;

import java.util.Set;

public class VanillaSkeleton extends HostileMob {

    public VanillaSkeleton() {
        super(EntityType.SKELETON);
        setItemInMainHand(ItemStack.of(Material.BOW));
    }

    @Override
    public Key getMobKey() {
        return Key.key("minecraft", "skeleton");
    }

    @Override
    public SoundEvent getHurtSound() {
        return SoundEvent.ENTITY_SKELETON_HURT;
    }

    @Override protected double getBaseHealth() { return 20.0; }
    @Override protected double getBaseAttack() { return 2.0; }
    @Override protected double getBaseMoveSpeed() { return 0.1; }

    @Override
    protected BehaviorGroup buildBaseBehaviorGroup() {
        return BehaviorGroup.builder()
                .sensor(Sensors.nearestPlayer())
                .behavior(
                        BehaviorImpl.builder()
                                .executor(Executors.shootProjectile(
                                        MemoryTypes.NEAREST_PLAYER,
                                        0.1, 0.1, 256.0, 60, 20, false,
                                        shooter -> new EntityProjectile(shooter, EntityType.ARROW)
                                ))
                                .evaluator(entity -> {
                                    if (!(entity instanceof IntelligentEntity e)) return false;
                                    return e.getBehaviorGroup().getMemoryStorage()
                                            .get(MemoryTypes.NEAREST_PLAYER) != null;
                                })
                                .priority(3).period(1).build()
                )
                .behavior(
                        BehaviorImpl.builder()
                                .executor(Executors.followEntity(
                                        MemoryTypes.NEAREST_PLAYER, 0.1, 0.1, 256.0, 36.0
                                ))
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
                                                .executor(Executors.roam(0.08, 0.08, 8, 20, false, 100, false, 10))
                                                .evaluator(entity -> true)
                                                .priority(1).weight(3).period(1).build()
                                ),
                                1, 40
                        )
                )
                .controller(Controllers.walk())
                .controller(Controllers.look())
                .build();
    }
}
