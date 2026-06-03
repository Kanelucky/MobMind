package org.kanelucky.mobmind.core.entity.ai.memory

import org.kanelucky.mobmind.api.entity.ai.memory.MemoryStorage
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryType

/**
 * HashMap-backed implementation of MemoryStorage.
 * Uses HashMap instead of ConcurrentHashMap since Minestom ticks on a single thread.
 *
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 *
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
class MemoryStorageImpl : MemoryStorage {

    private companion object {
        val EMPTY_VALUE = Any()
    }

    private val storage = HashMap<MemoryType<*>, Any>(16)

    override fun <T> set(type: MemoryType<T>, value: T?) {
        if (value == null) storage[type] = EMPTY_VALUE
        else storage[type] = value
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(type: MemoryType<T>): T? {
        val value = storage[type] ?: return null
        if (value === EMPTY_VALUE) return null
        return value as T
    }

    override fun <T> clear(type: MemoryType<T>) {
        storage.remove(type)
    }

    override fun clear() {
        storage.clear()
    }

    override fun <T> isEmpty(type: MemoryType<T>): Boolean {
        val v = storage[type]
        return v == null || v === EMPTY_VALUE
    }

    override fun isEmpty() = storage.isEmpty()

    override fun <T> putIfAbsent(type: MemoryType<T>, value: T): Boolean {
        val existing = storage[type]
        if (existing != null && existing !== EMPTY_VALUE) return false
        storage[type] = value ?: EMPTY_VALUE
        return true
    }
}