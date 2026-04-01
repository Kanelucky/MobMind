package org.kanelucky.mobmind.api.entity.ai.executor;

import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.LivingEntity;

@FunctionalInterface
public interface MeleeAttackCallback {
    void onAttack(EntityCreature attacker, LivingEntity target);
}
