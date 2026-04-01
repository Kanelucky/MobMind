package org.kanelucky.mobmind.vanilla.passive;

import net.kyori.adventure.key.Key;

import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import org.kanelucky.mobmind.api.entity.ai.behavior.BehaviorImpl;
import org.kanelucky.mobmind.api.entity.ai.behaviorgroup.BehaviorGroup;
import org.kanelucky.mobmind.api.entity.ai.evaluator.Evaluators;
import org.kanelucky.mobmind.api.entity.ai.executor.Executors;
import org.kanelucky.mobmind.vanilla.PassiveMob;

import java.util.Set;

public class VanillaSheep extends PassiveMob {

    public VanillaSheep() {
        super(EntityType.SHEEP);
    }

    @Override
    public Key getMobKey() {
        return Key.key("minestom4fun", "sheep");
    }

    @Override
    public Set<Material> getBreedingItems() {
        return Set.of(Material.WHEAT);
    }

    @Override
    public SoundEvent getHurtSound() {
        return SoundEvent.ENTITY_SHEEP_HURT;
    }

    @Override
    public EntityCreature createOffspring() {
        VanillaSheep lamb = new VanillaSheep();
        lamb.setBaby(true);
        return lamb;
    }

    @Override protected double getBaseHealth() { return 8.0; }
    @Override protected double getBaseAttack() { return 0.0; }
    @Override protected double getBaseMoveSpeed() { return 0.1; }

    @Override
    protected BehaviorGroup buildBaseBehaviorGroup() {
        return addBaseBehaviors(BehaviorGroup.builder())
                .behavior(
                        BehaviorImpl.builder()
                                .executor(Executors.eatGrass(40, entity -> {
                                    if (entity.getInstance() == null) return;
                                    entity.getInstance().playSound(
                                            net.kyori.adventure.sound.Sound.sound(
                                                    SoundEvent.ENTITY_SHEEP_STEP,
                                                    net.kyori.adventure.sound.Sound.Source.NEUTRAL,
                                                    1f, 1f
                                            ),
                                            entity.getPosition()
                                    );
                                }))
                                .evaluator(Evaluators.probability(1, 200))
                                .priority(2)
                                .period(40)
                                .build()
                )
                .build();
    }
}