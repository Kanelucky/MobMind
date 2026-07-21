package org.kanelucky.mobmind.api.entity.ai.memory;

/**
 * Storage for entity AI memory data.
 *
 * @author Kanelucky || Allay (https://github.com/AllayMC/Allay)
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
