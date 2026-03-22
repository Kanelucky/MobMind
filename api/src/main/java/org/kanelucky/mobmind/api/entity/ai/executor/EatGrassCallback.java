package org.kanelucky.mobmind.api.entity.ai.executor;

import net.minestom.server.entity.EntityCreature;

@FunctionalInterface
public interface EatGrassCallback {
    void onEatGrass(EntityCreature entity);
}
