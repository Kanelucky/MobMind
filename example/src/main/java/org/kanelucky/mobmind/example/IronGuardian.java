package org.kanelucky.mobmind.example;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.kanelucky.mobmind.api.entity.IntelligentEntity;
import org.kanelucky.mobmind.api.entity.ai.behavior.Behavior;
import org.kanelucky.mobmind.api.entity.ai.behavior.BehaviorImpl;
import org.kanelucky.mobmind.api.entity.ai.behavior.Behaviors;
import org.kanelucky.mobmind.api.entity.ai.behaviorgroup.BehaviorGroup;
import org.kanelucky.mobmind.api.entity.ai.controller.Controllers;
import org.kanelucky.mobmind.api.entity.ai.evaluator.Evaluators;
import org.kanelucky.mobmind.api.entity.ai.executor.Executors;
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryTypes;
import org.kanelucky.mobmind.api.entity.ai.sensor.Sensors;

import java.util.Set;

/**
 * A custom entity that looks like a Blaze but behaves like an aggressive melee fighter
 * Demonstrates how to create a fully custom entity with MobMind
 *
 * @author Kanelucky
 */
public class IronGuardian extends IntelligentEntity {

    private final BehaviorGroup behaviorGroup;

    public IronGuardian() {
        super(EntityType.BLAZE); // visual model
        this.behaviorGroup = buildBehaviorGroup();
        this.behaviorGroup.setEntity(this);
    }


    @Override
    public Key getMobKey() {
        return Key.key("mobmind", "iron_guardian");
    }

    @Override protected double getBaseHealth() { return 60.0; }
    @Override protected double getBaseAttack() { return 8.0; }
    @Override protected double getBaseMoveSpeed() { return 0.12; }

    protected SoundEvent getHurtSound() {
        return SoundEvent.ENTITY_IRON_GOLEM_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvent.ENTITY_IRON_GOLEM_DEATH;
    }

    @Override
    public boolean damage(@NotNull Damage damage) {
        boolean result = super.damage(damage);
        if (result && getInstance() != null) {
            getInstance().playSound(
                    Sound.sound(getHurtSound(), Sound.Source.HOSTILE, 1f, 1f),
                    getPosition()
            );
        }
        return result;
    }

    @Override
    public void update(long time) {
        super.update(time);
    }

    @Override
    public BehaviorGroup getBehaviorGroup() { return behaviorGroup; }

    private BehaviorGroup buildBehaviorGroup() {
        return BehaviorGroup.builder()
                .sensor(Sensors.nearestPlayer(24.0, 0.0, 20))
                .behavior(
                        BehaviorImpl.builder()
                                .executor(Executors.beamAttack(MemoryTypes.NEAREST_PLAYER, 576.0, 40, 20, false))
                                .evaluator(entity -> {
                                    if (!(entity instanceof IntelligentEntity e)) return false;
                                    return e.getBehaviorGroup().getMemoryStorage().get(MemoryTypes.NEAREST_PLAYER) != null;
                                })
                                .priority(3)
                                .period(1)
                                .build()
                )
                .behavior(
                        BehaviorImpl.builder()
                                .executor(Executors.shootProjectile(
                                        MemoryTypes.NEAREST_PLAYER,
                                        0.1, 0.1, 576.0, 60, 20, false,
                                        shooter -> {
                                            var projectile = new net.minestom.server.entity.EntityProjectile(shooter, EntityType.SNOWBALL);
                                            var meta = (net.minestom.server.entity.metadata.item.SnowballMeta) projectile.getEntityMeta();
                                            meta.setItem(ItemStack.of(Material.SNOWBALL));
                                            return projectile;
                                        }
                                ))
                                .evaluator(entity -> {
                                    if (!(entity instanceof IntelligentEntity e)) return false;
                                    return e.getBehaviorGroup().getMemoryStorage().get(MemoryTypes.NEAREST_PLAYER) != null;
                                })
                                .priority(2)
                                .period(1)
                                .build()
                )
                .behavior(
                        BehaviorImpl.builder()
                                .executor(Executors.jump(80, 10, 0.8, 0.4))
                                .evaluator(entity -> true)
                                .priority(1)
                                .period(1)
                                .build()
                )
                .behavior(
                        BehaviorImpl.builder()
                                .executor(Executors.meleeAttack(
                                        MemoryTypes.NEAREST_PLAYER,
                                        0.12, 0.1,
                                        576.0, // 24 blocks
                                        3.0,   // attack range
                                        15,    // fast attack
                                        false
                                ))
                                .evaluator(entity -> {
                                    if (!(entity instanceof IntelligentEntity e)) return false;
                                    return e.getBehaviorGroup().getMemoryStorage().get(MemoryTypes.NEAREST_PLAYER) != null;
                                })
                                .priority(2)
                                .period(1)
                                .build()
                )
                .behavior(
                        Behaviors.weighted(
                                Set.<Behavior>of(
                                        BehaviorImpl.builder()
                                                .executor(Executors.idle(40, 80))
                                                .evaluator(entity -> true)
                                                .priority(1)
                                                .weight(1)
                                                .period(1)
                                                .build(),
                                        BehaviorImpl.builder()
                                                .executor(Executors.roam(0.08, 0.08, 6, 20, true, 100, true, 10))
                                                .evaluator(entity -> true)
                                                .priority(1)
                                                .weight(2)
                                                .period(1)
                                                .build()
                                ),
                                1, 40
                        )
                )
                .controller(Controllers.walk())
                .controller(Controllers.look())
                .build();
    }
}