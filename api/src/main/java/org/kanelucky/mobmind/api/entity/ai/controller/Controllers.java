package org.kanelucky.mobmind.api.entity.ai.controller;

public final class Controllers {

    private static ControllerFactory factory;

    private Controllers() {}

    public static void register(ControllerFactory f) { factory = f; }

    private static ControllerFactory factory() {
        if (factory == null) throw new IllegalStateException(
                "No ControllerFactory registered. Did you include core?"
        );
        return factory;
    }

    public static Controller walk() { return factory().createWalk(); }
    public static Controller look() { return factory().createLook(); }
}
