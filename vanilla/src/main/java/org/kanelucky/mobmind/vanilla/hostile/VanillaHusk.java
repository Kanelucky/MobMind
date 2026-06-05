package org.kanelucky.mobmind.vanilla.hostile;

import net.kyori.adventure.key.Key;

import net.minestom.server.entity.EntityType;
import net.minestom.server.sound.SoundEvent;

import org.kanelucky.mobmind.vanilla.HostileMob;

public class VanillaHusk extends HostileMob {

    public VanillaHusk() {
        super(EntityType.HUSK);
    }

    @Override
    public Key getMobKey() {
        return Key.key("minecraft", "husk");
    }

    @Override
    public SoundEvent getHurtSound() {
        return SoundEvent.ENTITY_HUSK_HURT;
    }
}
