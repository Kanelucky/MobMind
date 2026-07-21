package org.kanelucky.mobmind.api;

import org.kanelucky.mobmind.api.entity.MobMindInitializer;

public final class MobMind {

    public static final MobMind INSTANCE = new MobMind();

    private MobMind() {
    }

    /**
     * @deprecated Use {@link #init(MobMindInitializer)} instead
     */
    @Deprecated
    public void register(MobMindInitializer initializer) {
        this.initializer = initializer;
    }

    /**
     * @deprecated Use {@link #init(MobMindInitializer)} instead
     */
    @Deprecated
    public void init() {
        if (initializer == null) throw new IllegalStateException("No initializer registered. Call MobMind.INSTANCE.init(CoreInitializer) instead");
        initializer.initialize();
    }

    public void init(MobMindInitializer initializer) {
        if (initializer == null) throw new IllegalStateException("Initializer cannot be null");
        initializer.initialize();
    }

    private MobMindInitializer initializer;
}