package org.kanelucky.mobmind.api.entity;

import java.util.EnumSet;
import java.util.Set;

/**
 * Global AI debug options.
 *
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 *
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
public final class EntityAI {

    public enum DebugOption { BEHAVIOR, SENSOR, CONTROLLER, ROUTE }

    private static final Set<DebugOption> debugOptions = EnumSet.noneOf(DebugOption.class);

    private EntityAI() {}

    public static boolean isDebugEnabled(DebugOption option) { return debugOptions.contains(option); }
    public static void enableDebug(DebugOption option) { debugOptions.add(option); }
    public static void disableDebug(DebugOption option) { debugOptions.remove(option); }
}
