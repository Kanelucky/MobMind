package org.kanelucky.mobmind.api.entity.ai.sensor;

import net.minestom.server.entity.EntityCreature;

/**
 * Scans the environment and updates entity memory.
 *
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 *
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
public interface Sensor {
    default int getPeriod() { return 20; }
    void sense(EntityCreature entity);
}