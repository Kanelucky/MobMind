package org.kanelucky.mobmind.api;

import org.kanelucky.mobmind.api.entity.MobMindInitializer;

import java.util.ServiceLoader;

public final class MobMind {

    public static final MobMind INSTANCE = new MobMind();

    private MobMind() {}

    public void init() {
        if (initializer == null) throw new IllegalStateException(
                "No initializer registered. Call MobMind.INSTANCE.register() first"
        );
        initializer.initialize();
    }

    public void register(MobMindInitializer initializer) {
        this.initializer = initializer;
    }

    public void init(MobMindInitializer initializer) {
        if (initializer == null) throw new IllegalStateException(
                "Initializer cannot be null"
        );
        initializer.initialize();
    }

    private MobMindInitializer initializer;
}
