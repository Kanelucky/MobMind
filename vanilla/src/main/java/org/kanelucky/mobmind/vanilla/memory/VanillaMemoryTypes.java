package org.kanelucky.mobmind.vanilla.memory;

import net.minestom.server.entity.LivingEntity;
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryType;

public final class VanillaMemoryTypes {
    public static final MemoryType<LivingEntity> NEAREST_WOLF =
            new MemoryType<>("vanilla:nearest_wolf");
}
