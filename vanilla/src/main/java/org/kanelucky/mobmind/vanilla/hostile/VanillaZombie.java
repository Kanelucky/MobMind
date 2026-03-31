package org.kanelucky.mobmind.vanilla.hostile;

import net.kyori.adventure.key.Key;
import net.minestom.server.entity.EntityType;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
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
}

