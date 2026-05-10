package org.kanelucky.mobmind.vanilla.passive;

import net.kyori.adventure.key.Key;

import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import org.kanelucky.mobmind.vanilla.PassiveMob;

import java.util.Set;

public class VanillaHorse extends PassiveMob {

    private static final Set<Material> BREEDING_ITEMS = Set.of(Material.WHEAT,
                                                               Material.HAY_BLOCK);

    public VanillaHorse() {
        super(EntityType.HORSE);
    }

    @Override
    public Key getMobKey() {
        return Key.key("minecraft", "horse");
    }

    @Override
    public Set<Material> getBreedingItems() {
        return BREEDING_ITEMS;
    }

    @Override
    public SoundEvent getHurtSound() {
        return SoundEvent.ENTITY_HORSE_HURT;
    }

    @Override
    public EntityCreature createOffspring() {
        VanillaHorse foal = new VanillaHorse();
        foal.setBaby(true);
        return foal;
    }

    @Override
    protected double getBaseHealth() {
        return 30.0;
    }

    @Override
    protected double getBaseAttack() {
        return 0.0;
    }

    @Override
    protected double getBaseMoveSpeed() {
        return 0.1375;
    }

}
