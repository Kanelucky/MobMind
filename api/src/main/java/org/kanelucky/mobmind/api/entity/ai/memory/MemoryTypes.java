package org.kanelucky.mobmind.api.entity.ai.memory;

import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;

/**
 * Core memory types used by the AI framework
 *
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 *
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
public final class MemoryTypes {
    public static final MemoryType<Point> MOVE_TARGET = new MemoryType<>("move_target");
    public static final MemoryType<Point> LOOK_TARGET = new MemoryType<>("look_target");
    public static final MemoryType<LivingEntity> ATTACK_TARGET = new MemoryType<>("attack_target");
    public static final MemoryType<Player> NEAREST_PLAYER = new MemoryType<>("nearest_player");
    public static final MemoryType<Player> NEAREST_FEEDING_PLAYER = new MemoryType<>("nearest_feeding_player");
    public static final MemoryType<Boolean> IS_IN_LOVE = new MemoryType<>("is_in_love");
    public static final MemoryType<Long> LAST_IN_LOVE_TIME = new MemoryType<>("last_in_love_time");
    public static final MemoryType<Integer> PANIC_TICKS = new MemoryType<>("panic_ticks");

    private MemoryTypes() {}
}
