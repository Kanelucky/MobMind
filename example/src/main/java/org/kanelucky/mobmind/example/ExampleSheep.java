package org.kanelucky.mobmind.example;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.kanelucky.mobmind.api.entity.IntelligentEntity;
import org.kanelucky.mobmind.api.entity.ai.Breedable;
import org.kanelucky.mobmind.api.entity.ai.Feedable;
import org.kanelucky.mobmind.api.entity.ai.Offspring;
import org.kanelucky.mobmind.api.entity.ai.behavior.Behavior;
import org.kanelucky.mobmind.api.entity.ai.behavior.BehaviorImpl;
import org.kanelucky.mobmind.api.entity.ai.behavior.Behaviors;
import org.kanelucky.mobmind.api.entity.ai.behaviorgroup.BehaviorGroup;
import org.kanelucky.mobmind.api.entity.ai.controller.Controllers;
import org.kanelucky.mobmind.api.entity.ai.evaluator.Evaluators;
import org.kanelucky.mobmind.api.entity.ai.executor.EatGrassCallback;
import org.kanelucky.mobmind.api.entity.ai.executor.Executors;
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryTypes;
import org.kanelucky.mobmind.api.entity.ai.sensor.Sensors;

import java.util.Set;

public class ExampleSheep extends IntelligentEntity implements Breedable, Feedable, Offspring {

    private boolean baby = false;
    private int breedCooldown = 0;
    private int ageTicks = 0;
    private final BehaviorGroup behaviorGroup;

    public ExampleSheep() {
        super(EntityType.SHEEP);
        this.behaviorGroup = buildBehaviorGroup();
        this.behaviorGroup.setEntity(this);
    }

    @Override
    public Key getMobKey() {
        return Key.key("mobmind", "example_sheep");
    }

    @Override public boolean isBaby() { return baby; }
    @Override public void setBaby(boolean baby) { this.baby = baby; }
    @Override public int getBreedCooldown() { return breedCooldown; }
    @Override public void setBreedCooldown(int cooldown) { this.breedCooldown = cooldown; }

    @Override
    public boolean isBreedingItem(ItemStack item) {
        return item.material() == Material.WHEAT;
    }

    @Override
    public EntityCreature createOffspring() {
        ExampleSheep baby = new ExampleSheep();
        baby.setBaby(true);
        return baby;
    }

    @Override
    public BehaviorGroup getBehaviorGroup() { return behaviorGroup; }

    protected SoundEvent getHurtSound() {
        return SoundEvent.ENTITY_SHEEP_HURT;
    }

    @Override
    public void update(long time) {
        super.update(time);
        if (isBaby()) {
            ageTicks++;
            if (ageTicks >= 24000) {
                setBaby(false);
                ageTicks = 0;
            }
        }
        if (breedCooldown > 0) breedCooldown--;
    }

    @Override
    public boolean damage(@NotNull Damage damage) {
        boolean result = super.damage(damage);
        if (result) {
            if (getInstance() != null) {
                getInstance().playSound(
                        Sound.sound(getHurtSound(), Sound.Source.NEUTRAL, 1f, 1f),
                        getPosition()
                );
            }
            getBehaviorGroup().getMemoryStorage().set(MemoryTypes.PANIC_TICKS, 100);
        }
        return result;
    }

    private BehaviorGroup buildBehaviorGroup() {
        EatGrassCallback eatSound = entity -> {
            if (entity.getInstance() == null) return;
            entity.getInstance().playSound(
                    Sound.sound(SoundEvent.ENTITY_SHEEP_STEP, Sound.Source.NEUTRAL, 1f, 1f),
                    entity.getPosition()
            );
        };

        return BehaviorGroup.builder()
                .sensor(Sensors.nearestPlayer())
                .sensor(Sensors.nearestFeedingPlayer())
                .behavior(
                        BehaviorImpl.builder()
                                .executor(Executors.panic())
                                .evaluator(Evaluators.panic())
                                .priority(4)
                                .period(1)
                                .build()
                )
                .behavior(
                        BehaviorImpl.builder()
                                .executor(Executors.breeding())
                                .evaluator(Evaluators.inLove())
                                .priority(3)
                                .period(1)
                                .build()
                )
                .behavior(
                        BehaviorImpl.builder()
                                .executor(Executors.followEntity(MemoryTypes.NEAREST_FEEDING_PLAYER, 0.125, 0.1, 256.0, 2.0))
                                .evaluator(entity -> {
                                    if (!(entity instanceof IntelligentEntity e)) return false;
                                    return e.getBehaviorGroup().getMemoryStorage().get(MemoryTypes.NEAREST_FEEDING_PLAYER) != null;
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
                                    if (e.getBehaviorGroup().getMemoryStorage().get(MemoryTypes.NEAREST_PLAYER) == null) return false;
                                    return Math.random() < 0.05;
                                })
                                .priority(2)
                                .period(10)
                                .build()
                )
                .behavior(
                        BehaviorImpl.builder()
                                .executor(Executors.eatGrass(40, eatSound))
                                .evaluator(Evaluators.probability(1, 200))
                                .priority(2)
                                .period(40)
                                .build()
                )
                .behavior(
                        Behaviors.weighted(
                                Set.of(
                                        BehaviorImpl.builder()
                                                .executor(Executors.idle(20, 100))
                                                .evaluator(entity -> true)
                                                .priority(1)
                                                .weight(1)
                                                .period(1)
                                                .build(),
                                        BehaviorImpl.builder()
                                                .executor(Executors.roam(0.1, 0.1, 10, 20, true, 100, true, 10))
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
