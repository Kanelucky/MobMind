package org.kanelucky.mobmind.core.entity.ai.memory

import org.kanelucky.mobmind.api.entity.ai.memory.MemoryStorage
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryType

/**
 * @author Kanelucky || Allay (https://github.com/AllayMC/Allay)
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