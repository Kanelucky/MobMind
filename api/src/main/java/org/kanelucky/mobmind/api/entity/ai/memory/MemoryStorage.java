package org.kanelucky.mobmind.api.entity.ai.memory;

/**
 * Storage for entity AI memory data.
 *
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 *
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
public interface MemoryStorage {
    <T> void set(MemoryType<T> type, T value);
    <T> T get(MemoryType<T> type);
    <T> void clear(MemoryType<T> type);
    void clear();
    <T> boolean isEmpty(MemoryType<T> type);
    boolean isEmpty();
    <T> boolean putIfAbsent(MemoryType<T> type, T value);
}
