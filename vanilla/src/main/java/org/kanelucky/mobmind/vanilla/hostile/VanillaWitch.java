package org.kanelucky.mobmind.vanilla.hostile;

import net.kyori.adventure.key.Key;

import net.minestom.server.entity.EntityType;
import net.minestom.server.sound.SoundEvent;

import org.kanelucky.mobmind.vanilla.HostileMob;

public class VanillaWitch extends HostileMob {

    public VanillaWitch() {
        super(EntityType.WITCH);
    }

    @Override
    public Key getMobKey() {
        return Key.key("minecraft", "witch");
    }

    @Override
    public SoundEvent getHurtSound() {
        return SoundEvent.ENTITY_WITCH_HURT;
    }

    @Override protected double getBaseHealth() { return 26.0; }
    @Override protected double getBaseAttack() { return 0.0; }
    @Override protected double getBaseMoveSpeed() { return 0.25; }
}
