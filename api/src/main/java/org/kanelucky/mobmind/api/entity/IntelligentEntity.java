package org.kanelucky.mobmind.api.entity;

import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.damage.Damage;
import org.jetbrains.annotations.NotNull;
import org.kanelucky.mobmind.api.entity.ai.behaviorgroup.BehaviorGroup;
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryTypes;

/**
 * Base class for entities with an AI behavior system.
 * Extend this class to create custom intelligent entities.
 *
 * @author Kanelucky
 */
public abstract class IntelligentEntity extends EntityCreature implements MobMindEntity {

    public IntelligentEntity(EntityType entityType) {
        super(entityType);
        getAttribute(Attribute.MAX_HEALTH).setBaseValue(getBaseHealth());
        getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(getBaseAttack());
        getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(getBaseMoveSpeed());
        setHealth((float) getBaseHealth());
    }

    public abstract BehaviorGroup getBehaviorGroup();

    protected double getBaseHealth() {
        return 20.0;
    }

    protected double getBaseAttack() {
        return 1.0;
    }

    protected double getBaseMoveSpeed() {
        return 0.1;
    }

    @Override
    public boolean damage(@NotNull Damage damage) {
        boolean result = super.damage(damage);
        if (result && damage.getAttacker() instanceof LivingEntity attacker) {
            getBehaviorGroup().getMemoryStorage().set(MemoryTypes.HURT_BY, attacker);
            getBehaviorGroup().getMemoryStorage().set(MemoryTypes.HURT_BY_TICKS, 100);
        }
        return result;
    }

    @Override
    public void update(long time) {
        super.update(time);
        getBehaviorGroup().tick();
        Integer ticks = getBehaviorGroup().getMemoryStorage().get(MemoryTypes.HURT_BY_TICKS);
        if (ticks != null) {
            if (ticks <= 0) {
                getBehaviorGroup().getMemoryStorage().clear(MemoryTypes.HURT_BY);
                getBehaviorGroup().getMemoryStorage().clear(MemoryTypes.HURT_BY_TICKS);
            } else {
                getBehaviorGroup().getMemoryStorage().set(MemoryTypes.HURT_BY_TICKS, ticks - 1);
            }
        }
    }
}
