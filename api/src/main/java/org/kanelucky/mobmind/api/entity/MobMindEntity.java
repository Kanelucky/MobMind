package org.kanelucky.mobmind.api.entity;

import net.kyori.adventure.key.Key;

/**
 * Represents a MobMind entity with a unique identifier.
 */
public interface MobMindEntity {
    /**
     * Returns the unique identifier of this entity type.
     * Example: Key.key("myplugin", "custom_zombie")
     */
    Key getMobKey();
}