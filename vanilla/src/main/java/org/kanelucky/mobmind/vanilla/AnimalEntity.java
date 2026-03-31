package org.kanelucky.mobmind.vanilla;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import org.jetbrains.annotations.NotNull;

import org.kanelucky.mobmind.api.entity.IntelligentEntity;
import org.kanelucky.mobmind.api.entity.ai.Breedable;
import org.kanelucky.mobmind.api.entity.ai.Feedable;
import org.kanelucky.mobmind.api.entity.ai.Offspring;
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryTypes;

import java.util.Set;

/**
 * Base class for breedable animals.
 *
 * @author Kanelucky
 */
public abstract class AnimalEntity extends IntelligentEntity implements Breedable, Feedable, Offspring {

    private boolean baby = false;
    private int ageTicks = 0;
    private int breedCooldown = 0;

    public AnimalEntity(EntityType entityType) {
        super(entityType);
    }

    public abstract Set<Material> getBreedingItems();
    public abstract SoundEvent getHurtSound();

    @Override public boolean isBaby() { return baby; }
    @Override public void setBaby(boolean baby) { this.baby = baby; }
    @Override public int getBreedCooldown() { return breedCooldown; }
    @Override public void setBreedCooldown(int cooldown) { this.breedCooldown = cooldown; }

    @Override
    public boolean isBreedingItem(ItemStack item) {
        return getBreedingItems().contains(item.material());
    }

    @Override
    public Key getMobKey() {
        return Key.key("minecraft", entityType.name().toString());
    }

    @Override
    public boolean damage(@NotNull Damage damage) {
        boolean result = super.damage(damage);
        if (result) {
            if (getInstance() != null) {
                getInstance().playSound(
                        Sound.sound(getHurtSound(), Sound.Source.NEUTRAL, 1f, 1f),
                        getPosition()
                );
            }
            getBehaviorGroup().getMemoryStorage().set(MemoryTypes.PANIC_TICKS, 100);
        }
        return result;
    }

    @Override
    public void update(long time) {
        super.update(time);
        if (baby) {
            ageTicks++;
            if (ageTicks >= 24000) {
                baby = false;
                ageTicks = 0;
            }
        }
        if (breedCooldown > 0) breedCooldown--;
    }
}