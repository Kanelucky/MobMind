package org.kanelucky.mobmind.vanilla;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.sound.SoundEvent;

import org.jetbrains.annotations.NotNull;

import org.kanelucky.mobmind.api.entity.IntelligentEntity;

public abstract class MobEntity extends IntelligentEntity {

    public MobEntity(EntityType entityType) {
        super(entityType);
    }

    public abstract SoundEvent getHurtSound();

    @Override
    public Key getMobKey() {
        return Key.key("minecraft", entityType.name().toString());
    }

    @Override
    public boolean damage(@NotNull Damage damage) {
        boolean result = super.damage(damage);
        if (result && getInstance() != null) {
            getInstance().playSound(
                    Sound.sound(getHurtSound(), Sound.Source.HOSTILE, 1f, 1f),
                    getPosition()
            );
        }
        return result;
    }
}
