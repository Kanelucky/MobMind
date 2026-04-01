package org.kanelucky.mobmind.vanilla.passive;

import net.kyori.adventure.key.Key;

import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import org.kanelucky.mobmind.vanilla.PassiveMob;

import java.util.Set;

public class VanillaCat extends PassiveMob {

    private static final Set<Material> BREEDING_ITEMS = Set.of(
            Material.COD,
            Material.SALMON
    );

    public VanillaCat() {
        super(EntityType.CAT);
    }

    @Override
    public Key getMobKey() {
        return Key.key("minecraft", "cat");
    }

    @Override
    public Set<Material> getBreedingItems() {
        return BREEDING_ITEMS;
    }

    @Override
    public SoundEvent getHurtSound() {
        return SoundEvent.ENTITY_CAT_HURT;
    }

    @Override
    public EntityCreature createOffspring() {
        VanillaCat kitty = new VanillaCat();
        kitty.setBaby(true);
        return kitty;
    }

    @Override protected double getBaseHealth() { return 10.0; }
    @Override protected double getBaseAttack() { return 0.0; }
    @Override protected double getBaseMoveSpeed() { return 0.3; }

}
