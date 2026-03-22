package org.kanelucky.mobmind.api.entity.ai.executor;

import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityProjectile;

@FunctionalInterface
public interface ProjectileSupplier {
    EntityProjectile create(EntityCreature shooter);
}