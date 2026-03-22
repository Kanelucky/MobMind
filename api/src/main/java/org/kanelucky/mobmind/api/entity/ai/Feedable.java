package org.kanelucky.mobmind.api.entity.ai;

import net.minestom.server.item.ItemStack;

/**
 * Represents an entity that can be fed specific items.
 */
public interface Feedable {
    boolean isBreedingItem(ItemStack item);
}