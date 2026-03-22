package org.kanelucky.mobmind.api.entity.ai.sensor;

public interface SensorFactory {
    Sensor nearestPlayer(double range, double minRange, int period);
    Sensor nearestFeedingPlayer(double range, int period);
}
