package org.kanelucky.mobmind.vanilla.passive;

import net.kyori.adventure.key.Key;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import org.kanelucky.mobmind.vanilla.PassiveMob;

import java.util.Set;

public class VanillaCow extends PassiveMob {

    public VanillaCow() {
        super(EntityType.COW);
    }

    @Override
    public Key getMobKey() {
        return Key.key("minecraft", "cow");
    }

    @Override
    public Set<Material> getBreedingItems() {
        return Set.of(Material.WHEAT);
    }

    @Override
    public SoundEvent getHurtSound() {
        return SoundEvent.ENTITY_COW_HURT;
    }

    @Override
    public EntityCreature createOffspring() {
        VanillaCow calf = new VanillaCow();
        calf.setBaby(true);
        return calf;
    }

    @Override protected double getBaseHealth() { return 10.0; }
    @Override protected double getBaseAttack() { return 0.0; }
    @Override protected double getBaseMoveSpeed() { return 0.1; }

}
