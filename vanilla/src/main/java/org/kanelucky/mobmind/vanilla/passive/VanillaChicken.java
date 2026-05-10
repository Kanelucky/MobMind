package org.kanelucky.mobmind.vanilla.passive;

import net.kyori.adventure.key.Key;

import net.minestom.server.entity.EntityType;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import org.kanelucky.mobmind.vanilla.PassiveMob;

import java.util.Set;

public class VanillaChicken extends PassiveMob {

    private static final Set<Material> BREEDING_ITEMS = Set.of(Material.WHEAT_SEEDS,
                                                               Material.MELON_SEEDS,
                                                               Material.PUMPKIN_SEEDS,
                                                               Material.BEETROOT_SEEDS);

    public VanillaChicken() {
        super(EntityType.CHICKEN);
    }

    @Override
    public Set<Material> getBreedingItems() {
        return BREEDING_ITEMS;
    }

    @Override
    public VanillaChicken createOffspring() {
        return new VanillaChicken();
    }

    @Override
    public SoundEvent getHurtSound() {
        return SoundEvent.ENTITY_CHICKEN_HURT;
    }

    @Override
    public Key getMobKey() {
        return Key.key("minecraft", "chicken");
    }
}
