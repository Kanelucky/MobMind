package org.kanelucky.mobmind.core.entity.ai.memory

import org.kanelucky.mobmind.api.entity.ai.memory.MemoryStorage
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryType

import java.util.concurrent.ConcurrentHashMap

/**
 * ConcurrentHashMap-backed implementation of MemoryStorage.
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

    private val storage = ConcurrentHashMap<MemoryType<*>, Any>()

    override fun <T> set(type: MemoryType<T>, value: T?) {
        storage[type] = value ?: EMPTY_VALUE
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
        var inserted = false
        storage.compute(type) { _, existing ->
            if (existing == null || existing === EMPTY_VALUE) {
                inserted = true
                value ?: EMPTY_VALUE
            } else existing
        }
        return inserted
    }
}