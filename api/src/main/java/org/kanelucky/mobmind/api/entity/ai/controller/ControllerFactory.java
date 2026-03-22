package org.kanelucky.mobmind.api.entity.ai.controller;

public interface ControllerFactory {
    Controller createWalk();
    Controller createLook();
}
