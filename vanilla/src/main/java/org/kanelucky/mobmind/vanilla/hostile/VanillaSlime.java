package org.kanelucky.mobmind.vanilla.hostile;

import net.kyori.adventure.key.Key;

import net.minestom.server.entity.EntityType;
import net.minestom.server.sound.SoundEvent;

import org.kanelucky.mobmind.vanilla.HostileMob;

public class VanillaSlime extends HostileMob {

    private int size = 4;

    public VanillaSlime() {
        super(EntityType.SLIME);
    }

    @Override
    public Key getMobKey() {
        return Key.key("minecraft", "slime");
    }

    @Override
    public SoundEvent getHurtSound() {
        return SoundEvent.ENTITY_SLIME_HURT;
    }
}
