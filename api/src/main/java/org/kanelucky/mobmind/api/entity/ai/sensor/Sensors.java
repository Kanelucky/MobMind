package org.kanelucky.mobmind.api.entity.ai.sensor;

import net.minestom.server.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryType;

import java.util.function.Predicate;

public final class Sensors {

    private static SensorFactory factory;

    private Sensors() {
    }

    public static void register(SensorFactory f) {
        factory = f;
    }

    private static SensorFactory factory() {
        if (factory == null) throw new IllegalStateException(
                "No SensorFactory registered. Did you include core?");
        return factory;
    }

    public static Sensor nearestPlayer() {
        return factory().nearestPlayer(16.0, 0.0, 20);
    }

    public static Sensor nearestPlayer(
            double range,
            double minRange,
            int period
                                      ) {
        return factory().nearestPlayer(range, minRange, period);
    }

    public static Sensor nearestFeedingPlayer() {
        return factory().nearestFeedingPlayer(8.0, 20);
    }

    public static Sensor nearestFeedingPlayer(double range, int period) {
        return factory().nearestFeedingPlayer(range, period);
    }

    public static <T extends LivingEntity> Sensor nearestEntity(
            MemoryType<T> memoryType,
            Class<T> entityClass
                                                               ) {
        return factory().nearestEntity(memoryType,
                                       entityClass,
                                       16.0,
                                       0.0,
                                       20,
                                       null);
    }

    public static <T extends LivingEntity> Sensor nearestEntity(
            MemoryType<T> memoryType,
            Class<T> entityClass,
            double range,
            double minRange,
            int period
                                                               ) {
        return factory().nearestEntity(memoryType,
                                       entityClass,
                                       range,
                                       minRange,
                                       period,
                                       null);
    }

    public static <T extends LivingEntity> Sensor nearestEntity(
            MemoryType<T> memoryType,
            Class<T> entityClass,
            double range,
            double minRange,
            int period,
            @Nullable Predicate<T> predicate
                                                               ) {
        return factory().nearestEntity(memoryType,
                                       entityClass,
                                       range,
                                       minRange,
                                       period,
                                       predicate);
    }
}
