package org.kanelucky.mobmind.vanilla.hostile;

import net.kyori.adventure.key.Key;

import net.minestom.server.entity.EntityType;
import net.minestom.server.sound.SoundEvent;

import org.kanelucky.mobmind.vanilla.HostileMob;

public class VanillaZoglin extends HostileMob {

    public VanillaZoglin() {
        super(EntityType.ZOGLIN);
    }

    @Override
    public Key getMobKey() {
        return Key.key("minecraft", "zoglin");
    }

    @Override
    public SoundEvent getHurtSound() {
        return SoundEvent.ENTITY_ZOGLIN_HURT;
    }

    @Override
    protected double getBaseHealth() {
        return 40.0;
    }

    @Override
    protected double getBaseAttack() {
        return 6.0;
    }

    @Override
    protected double getBaseMoveSpeed() {
        return 0.3;
    }
}
