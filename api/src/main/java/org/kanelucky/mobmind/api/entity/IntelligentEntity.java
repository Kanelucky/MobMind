package org.kanelucky.mobmind.api.entity;

import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.attribute.Attribute;
import org.kanelucky.mobmind.api.entity.ai.behaviorgroup.BehaviorGroup;

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

    protected double getBaseHealth() { return 20.0; }
    protected double getBaseAttack() { return 1.0; }
    protected double getBaseMoveSpeed() { return 0.1; }

    @Override
    public void update(long time) {
        super.update(time);
        getBehaviorGroup().tick();
    }
}
