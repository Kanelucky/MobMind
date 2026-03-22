package org.kanelucky.mobmind.api.entity.ai;

public interface Breedable {
    boolean isBaby();
    int getBreedCooldown();
    void setBaby(boolean baby);
    void setBreedCooldown(int cooldown);
}