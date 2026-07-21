package org.kanelucky.mobmind.api.entity.ai.sensor;

import net.minestom.server.entity.EntityCreature;

/**
 * Scans the environment and updates entity memory.
 *
 * @author Kanelucky || Allay (https://github.com/AllayMC/Allay)
 */
public interface Sensor {
    default int getPeriod() {
        return 20;
    }

    void sense(EntityCreature entity);
}