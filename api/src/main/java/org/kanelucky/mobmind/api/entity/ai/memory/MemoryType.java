package org.kanelucky.mobmind.api.entity.ai.memory;

/**
 * @author Kanelucky
 */
public final class MemoryType<T> {
    private final String name;

    public MemoryType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemoryType<?> other)) return false;
        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "MemoryType(" + name + ")";
    }
}
