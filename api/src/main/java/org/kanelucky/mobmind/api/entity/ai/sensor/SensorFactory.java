package org.kanelucky.mobmind.api.entity.ai.sensor;

import net.minestom.server.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryType;

import java.util.function.Predicate;

public interface SensorFactory {
    Sensor nearestPlayer(double range, double minRange, int period);

    Sensor nearestFeedingPlayer(double range, int period);

    <T extends LivingEntity> Sensor nearestEntity(
            MemoryType<T> memoryType,
            Class<T> entityClass,
            double range,
            double minRange,
            int period,
            @Nullable Predicate<T> predicate
                                                 );
}
