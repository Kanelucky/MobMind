package org.kanelucky.mobmind.api.entity;

import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.damage.Damage;
import org.jetbrains.annotations.NotNull;
import org.kanelucky.mobmind.api.entity.ai.behaviorgroup.BehaviorGroup;
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryTypes;

import java.util.Map;

/**
 * Base class for entities with an AI behavior system.
 * Extend this class to create custom intelligent entities.
 *
 * @author Kanelucky
 */
public abstract class IntelligentEntity extends EntityCreature implements MobMindEntity {

    public IntelligentEntity(EntityType entityType) {
        super(entityType);
        Map<Attribute, Double> defaults = entityType.registry()
                                                    .defaultAttributes();
        getAttribute(Attribute.MAX_HEALTH).setBaseValue(getBaseHealth() != -1 ? getBaseHealth() : defaults.getOrDefault(
                Attribute.MAX_HEALTH,
                20.0));
        getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(getBaseAttack() != -1 ? getBaseAttack() : defaults.getOrDefault(
                Attribute.ATTACK_DAMAGE,
                1.0));
        getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(getBaseMoveSpeed() != -1 ? getBaseMoveSpeed() : defaults.getOrDefault(
                Attribute.MOVEMENT_SPEED,
                0.1));
        setHealth((float) getAttribute(Attribute.MAX_HEALTH).getValue());
    }

    public abstract BehaviorGroup getBehaviorGroup();

    protected double getBaseHealth() {
        return -1;
    }

    protected double getBaseAttack() {
        return -1;
    }

    protected double getBaseMoveSpeed() {
        return -1;
    }

    @Override
    public boolean damage(@NotNull Damage damage) {
        boolean result = super.damage(damage);
        if (result && damage.getAttacker() instanceof LivingEntity attacker) {
            getBehaviorGroup().getMemoryStorage()
                              .set(MemoryTypes.HURT_BY, attacker);
            getBehaviorGroup().getMemoryStorage()
                              .set(MemoryTypes.HURT_BY_TICKS, 100);
        }
        return result;
    }

    @Override
    public void update(long time) {
        super.update(time);
        getBehaviorGroup().tick();
        Integer ticks = getBehaviorGroup().getMemoryStorage()
                                          .get(MemoryTypes.HURT_BY_TICKS);
        if (ticks != null) {
            if (ticks <= 0) {
                getBehaviorGroup().getMemoryStorage()
                                  .clear(MemoryTypes.HURT_BY);
                getBehaviorGroup().getMemoryStorage()
                                  .clear(MemoryTypes.HURT_BY_TICKS);
            } else {
                getBehaviorGroup().getMemoryStorage()
                                  .set(MemoryTypes.HURT_BY_TICKS, ticks - 1);
            }
        }
    }
}
