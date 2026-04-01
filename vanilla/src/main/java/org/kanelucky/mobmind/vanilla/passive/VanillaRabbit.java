package org.kanelucky.mobmind.vanilla.passive;

import net.kyori.adventure.key.Key;

import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import org.kanelucky.mobmind.vanilla.PassiveMob;

import java.util.Set;

public class VanillaRabbit extends PassiveMob {

    private static final Set<Material> BREEDING_ITEMS = Set.of(
            Material.CARROT,
            Material.GOLDEN_CARROT,
            Material.DANDELION
    );

    public VanillaRabbit() {
        super(EntityType.RABBIT);
    }

    @Override
    public Key getMobKey() {
        return Key.key("minecraft", "rabbit");
    }

    @Override
    public Set<Material> getBreedingItems() {
        return BREEDING_ITEMS;
    }

    @Override
    public SoundEvent getHurtSound() {
        return SoundEvent.ENTITY_RABBIT_HURT;
    }

    @Override
    public EntityCreature createOffspring() {
        VanillaRabbit bunny = new VanillaRabbit();
        bunny.setBaby(true);
        return bunny;
    }

    @Override protected double getBaseHealth() { return 3.0; }
    @Override protected double getBaseAttack() { return 0.0; }
    @Override protected double getBaseMoveSpeed() { return 0.3; }

}
