package org.kanelucky.mobmind.api.entity.ai.sensor;

public final class Sensors {

    private static SensorFactory factory;

    private Sensors() {}

    public static void register(SensorFactory f) { factory = f; }

    private static SensorFactory factory() {
        if (factory == null) throw new IllegalStateException(
                "No SensorFactory registered. Did you include core?"
        );
        return factory;
    }

    public static Sensor nearestPlayer() { return factory().nearestPlayer(16.0, 0.0, 20); }
    public static Sensor nearestPlayer(double range, double minRange, int period) {
        return factory().nearestPlayer(range, minRange, period);
    }

    public static Sensor nearestFeedingPlayer() { return factory().nearestFeedingPlayer(8.0, 20); }
    public static Sensor nearestFeedingPlayer(double range, int period) {
        return factory().nearestFeedingPlayer(range, period);
    }
}
