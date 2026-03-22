# MobMind API

The public API module for MobMind. This is the only module you should import in your project.

## Dependency
```kotlin
implementation("com.github.Kanelucky.MobMind:api:0.1.1")
```

## What's in here

- `IntelligentEntity` — base class for all AI entities
- `BehaviorGroup` — AI brain interface, built via `BehaviorGroup.builder()`
- `BehaviorImpl` — standard behavior implementation with builder
- `Behavior`, `BehaviorEvaluator`, `BehaviorExecutor` — core interfaces
- `Sensor` — environment scanning interface
- `MemoryType<T>`, `MemoryStorage` — type-safe memory system
- `MemoryTypes` — built-in memory types
- `Executors`, `Evaluators`, `Sensors`, `Controllers`, `Behaviors` — static factories
- `Breedable`, `Feedable`, `Offspring` — opt-in capability interfaces
- `MobMindEntity` — entity identity via `Key`
- `EntityAI` — debug options
- `MobMind` — entry point, call `init()` once at startup