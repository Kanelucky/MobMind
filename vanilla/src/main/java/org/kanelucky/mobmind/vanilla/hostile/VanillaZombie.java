package org.kanelucky.mobmind.vanilla.hostile;

import net.kyori.adventure.key.Key;

import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.sound.SoundEvent;

import org.kanelucky.mobmind.api.entity.IntelligentEntity;
import org.kanelucky.mobmind.api.entity.ai.behavior.BehaviorImpl;
import org.kanelucky.mobmind.api.entity.ai.behaviorgroup.BehaviorGroup;
import org.kanelucky.mobmind.api.entity.ai.executor.Executors;
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryTypes;
import org.kanelucky.mobmind.vanilla.HostileMob;

import java.util.Set;

public class VanillaZombie extends HostileMob {

    public VanillaZombie() {
        super(EntityType.ZOMBIE);
    }

    @Override
    public Key getMobKey() {
        return Key.key("minecraft", "zombie");
    }

    @Override
    public SoundEvent getHurtSound() {
        return SoundEvent.ENTITY_ZOMBIE_HURT;
    }

    @Override protected double getBaseHealth() { return 20.0; }
    @Override protected double getBaseAttack() { return 3.0; }
    @Override protected double getBaseMoveSpeed() { return 0.1; }

    @Override
    protected BehaviorGroup buildBaseBehaviorGroup() {
        return addBaseBehaviors(BehaviorGroup.builder())
                .behavior(
                        BehaviorImpl.builder()
                                .executor(Executors.meleeAttack(
                                        MemoryTypes.NEAREST_PLAYER,
                                        0.1,
                                        0.1,
                                        256.0,
                                        2.5,
                                        20,
                                        false,
                                        (attacker, target) -> {
                                            if (attacker instanceof LivingEntity le) le.swingMainHand();
                                        }
                                ))
                                .evaluator(entity -> {
                                    if (!(entity instanceof IntelligentEntity e)) return false;
                                    return e.getBehaviorGroup().getMemoryStorage()
                                            .get(MemoryTypes.NEAREST_PLAYER) != null;
                                })
                                .priority(3).period(1).build()
                )
                .build();
    }
}

