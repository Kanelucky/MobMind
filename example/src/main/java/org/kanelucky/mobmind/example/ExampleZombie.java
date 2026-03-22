package org.kanelucky.mobmind.example;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.kanelucky.mobmind.api.entity.IntelligentEntity;
import org.kanelucky.mobmind.api.entity.ai.behavior.Behavior;
import org.kanelucky.mobmind.api.entity.ai.behavior.BehaviorImpl;
import org.kanelucky.mobmind.api.entity.ai.behavior.Behaviors;
import org.kanelucky.mobmind.api.entity.ai.behaviorgroup.BehaviorGroup;
import org.kanelucky.mobmind.api.entity.ai.controller.Controllers;
import org.kanelucky.mobmind.api.entity.ai.executor.Executors;
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryTypes;
import org.kanelucky.mobmind.api.entity.ai.sensor.Sensors;

import java.util.Set;

public class ExampleZombie extends IntelligentEntity {

    private final BehaviorGroup behaviorGroup;

    public ExampleZombie() {
        super(EntityType.ZOMBIE);
        this.behaviorGroup = buildBehaviorGroup();
        this.behaviorGroup.setEntity(this);
    }

    @Override
    public Key getMobKey() {
        return Key.key("mobmind", "example_zombie");
    }

    @Override
    public BehaviorGroup getBehaviorGroup() { return behaviorGroup; }

    protected SoundEvent getHurtSound() {
        return SoundEvent.ENTITY_ZOMBIE_HURT;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvent.ENTITY_ZOMBIE_AMBIENT;
    }

    @Override
    protected double getBaseHealth() {
        return 20.0;
    }

    @Override
    protected double getBaseAttack() {
        return 3.0;
    }

    @Override
    protected double getBaseMoveSpeed() {
        return 0.1;
    }

    @Override
    public boolean damage(@NotNull Damage damage) {
        boolean result = super.damage(damage);
        if (result && getInstance() != null) {
            getInstance().playSound(
                    Sound.sound(getHurtSound(), Sound.Source.HOSTILE, 1f, 1f),
                    getPosition()
            );
            getBehaviorGroup().getMemoryStorage().set(MemoryTypes.PANIC_TICKS, 60);
        }
        return result;
    }

    private BehaviorGroup buildBehaviorGroup() {
        return BehaviorGroup.builder()
                .sensor(Sensors.nearestPlayer(16.0, 0.0, 20))
                .behavior(
                        BehaviorImpl.builder()
                                .executor(Executors.meleeAttack(MemoryTypes.NEAREST_PLAYER, 0.1, 0.1, 1024.0, 2.5, 30, false))
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
                                .executor(Executors.followEntity(MemoryTypes.NEAREST_PLAYER, 0.15, 0.1, 256.0, 2.0))
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
                                Set.of(
                                        BehaviorImpl.builder()
                                                .executor(Executors.idle(20, 60))
                                                .evaluator(entity -> true)
                                                .priority(1)
                                                .weight(1)
                                                .period(1)
                                                .build(),
                                        BehaviorImpl.builder()
                                                .executor(Executors.roam(0.08, 0.08, 8, 20, true, 100, false, 10))
                                                .evaluator(entity -> true)
                                                .priority(1)
                                                .weight(3)
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
