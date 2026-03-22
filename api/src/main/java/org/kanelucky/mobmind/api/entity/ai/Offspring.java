package org.kanelucky.mobmind.api.entity.ai;

import net.minestom.server.entity.EntityCreature;

/**
 * Represents an entity capable of producing offspring.
 */
public interface Offspring {
    EntityCreature createOffspring();
}
