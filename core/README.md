# MobMind Core

The internal implementation module for MobMind.

> **Do not import this module directly.** Use `mobmind-api` instead.
> The only exception is registering `CoreInitializer` at server startup.

## Dependency
```kotlin
implementation("com.github.Kanelucky.MobMind:core:0.1.1")
```

## Startup
```java
MobMind.INSTANCE.register(new CoreInitializer());
MobMind.INSTANCE.init();
```

## What's in here

- `BehaviorGroupImpl` — `BehaviorGroup` implementation
- `MemoryStorageImpl` — `MemoryStorage` implementation
- All `BehaviorExecutor` implementations — `PanicExecutor`, `MeleeAttackExecutor`, etc.
- All `BehaviorEvaluator` implementations — `PanicEvaluator`, `InLoveEvaluator`, etc.
- All `Sensor` implementations — `NearestPlayerSensor`, `NearestFeedingPlayerSensor`
- `WalkController`, `LookController` — movement controllers
- `FlatAStarRouteFinder`, `SpaceAStarRouteFinder` — pathfinding
- `CoreInitializer` — registers all implementations into API factories