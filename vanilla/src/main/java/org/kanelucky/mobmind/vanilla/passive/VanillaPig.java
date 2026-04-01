package org.kanelucky.mobmind.vanilla.passive;

import net.kyori.adventure.key.Key;

import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import org.kanelucky.mobmind.vanilla.PassiveMob;

import java.util.Set;

public class VanillaPig extends PassiveMob {

    private static final Set<Material> BREEDING_ITEMS = Set.of(
            Material.CARROT,
            Material.POTATO,
            Material.BEETROOT
    );

    public VanillaPig() {
        super(EntityType.PIG);
    }

    @Override
    public Key getMobKey() {
        return Key.key("minecraft", "pig");
    }

    @Override
    public Set<Material> getBreedingItems() {
        return BREEDING_ITEMS;
    }

    @Override
    public SoundEvent getHurtSound() {
        return SoundEvent.ENTITY_PIG_HURT;
    }

    @Override
    public EntityCreature createOffspring() {
        VanillaPig piglet = new VanillaPig();
        piglet.setBaby(true);
        return piglet;
    }

    @Override protected double getBaseHealth() { return 10.0; }
    @Override protected double getBaseAttack() { return 0.0; }
    @Override protected double getBaseMoveSpeed() { return 0.1; }

}
