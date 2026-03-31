# MobMind

A flexible, modular entity ai library for [Minestom](https://minestom.net/) servers. Build custom entities with priority-based behaviors, sensors, memory, and pathfinding

---

## Features

- **Priority-based behavior system** — behaviors run in priority order, higher priority interrupts lower
- **Sensor system** — scan the environment independently from behavior logic
- **Type-safe memory** — store and retrieve entity state with `MemoryType<T>`
- **Pathfinding** — 2D A* for ground entities, 3D A* for flying/swimming entities
- **Weighted random behaviors** — pick randomly between behaviors with configurable weights
- **Java & Kotlin friendly** — API is Java-first, works naturally in Kotlin too
- **Extensible** — override hooks in every executor, evaluator, and sensor

---

## Architecture

```
api/     ← Interfaces and factories. Only touch this
core/    ← Implementations. Never import directly
example/ ← Full example Minestom server with some simple entities
```

Dev code depends only on `api`. `core` is a runtime implementation detail

---

## Installation

### JitPack

Add to your `build.gradle.kts`:

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.Kanelucky.MobMind:api:0.1.1")
    implementation("com.github.Kanelucky.MobMind:core:0.1.1")
}
```

### Initialize

Call once at server startup, before spawning any entities:

```java
MobMind.INSTANCE.register(new CoreInitializer());
MobMind.INSTANCE.init();
```

---

## Quick Start

### Create a custom entity

```java
public class MyZombie extends IntelligentEntity {

    private final BehaviorGroup behaviorGroup;

    public MyZombie() {
        super(EntityType.ZOMBIE);
        this.behaviorGroup = buildBehaviorGroup();
        this.behaviorGroup.setEntity(this);
    }

    @Override
    public Key getMobKey() {
        return Key.key("myplugin", "my_zombie");
    }

    @Override
    public BehaviorGroup getBehaviorGroup() { return behaviorGroup; }

    @Override protected double getBaseHealth() { return 20.0; }
    @Override protected double getBaseAttack() { return 3.0; }
    @Override protected double getBaseMoveSpeed() { return 0.1; }

    @Override
    public boolean damage(@NotNull Damage damage) {
        boolean result = super.damage(damage);
        if (result) {
            getBehaviorGroup().getMemoryStorage().set(MemoryTypes.PANIC_TICKS, 100);
        }
        return result;
    }

    private BehaviorGroup buildBehaviorGroup() {
        return BehaviorGroup.builder()
            .sensor(Sensors.nearestPlayer())
            .behavior(
                BehaviorImpl.builder()
                    .executor(Executors.meleeAttack(MemoryTypes.NEAREST_PLAYER))
                    .evaluator(entity -> {
                        if (!(entity instanceof IntelligentEntity e)) return false;
                        return e.getBehaviorGroup().getMemoryStorage().get(MemoryTypes.NEAREST_PLAYER) != null;
                    })
                    .priority(2)
                    .period(1)
                    .build()
            )
            .behavior(
                BehaviorImpl.builder()
                    .executor(Executors.roam())
                    .evaluator(entity -> true)
                    .priority(1)
                    .period(1)
                    .build()
            )
            .controller(Controllers.walk())
            .controller(Controllers.look())
            .build();
    }
}
```

---

## Concepts

### BehaviorGroup

The AI brain. Manages behaviors, sensors, memory, and controllers. Built via `BehaviorGroup.builder()`.

### Behavior

A unit of AI logic with a priority, weight, and period. Uses a `BehaviorExecutor` (what to do) and a `BehaviorEvaluator` (when to activate).

```java
BehaviorImpl.builder()
    .executor(Executors.idle(20, 60))
    .evaluator(entity -> true)
    .priority(1)
    .period(20)
    .build()
```

### Memory

Type-safe key-value storage per entity. Read and written by sensors and executors.

```java
// Built-in types
MemoryTypes.NEAREST_PLAYER
MemoryTypes.NEAREST_FEEDING_PLAYER
MemoryTypes.MOVE_TARGET
MemoryTypes.LOOK_TARGET
MemoryTypes.PANIC_TICKS
MemoryTypes.IS_IN_LOVE

// Custom types
public static final MemoryType<Entity> AGGRO_TARGET =
    new MemoryType<>("myplugin:aggro_target");
```

### Sensor

Scans the environment and writes to memory on a configurable period.

```java
Sensors.nearestPlayer()                    // range 16, period 20
Sensors.nearestPlayer(24.0, 0.0, 10)      // custom range and period
Sensors.nearestFeedingPlayer()             // range 8, period 20
```

Custom sensor:

```java
public class MyCustomSensor implements Sensor {
    @Override
    public int getPeriod() { return 10; }

    @Override
    public void sense(EntityCreature entity) {
        if (!(entity instanceof IntelligentEntity e)) return;
        e.getBehaviorGroup().getMemoryStorage().set(MyMemoryTypes.NEAREST_ENEMY, ...);
    }
}
```

---

## Built-in Executors

| Method | Description |
|---|---|
| `Executors.idle(min, max)` | Stand still for a random duration |
| `Executors.roam(...)` | Wander randomly within a range |
| `Executors.wander(radius)` | Move to a random position once |
| `Executors.lookAround(min, max)` | Look in a random direction |
| `Executors.followEntity(memory, ...)` | Follow an entity stored in memory |
| `Executors.lookAtEntity(memory, duration)` | Look at an entity for a duration |
| `Executors.meleeAttack(memory, ...)` | Move toward and attack a target |
| `Executors.beamAttack(memory, ...)` | Guardian-style delayed beam attack |
| `Executors.shootProjectile(memory, ..., supplier)` | Shoot a projectile at a target |
| `Executors.jump(interval, delay, power, variance)` | Jump periodically |
| `Executors.panic(...)` | Flee in a random direction |
| `Executors.breeding(...)` | Find a mate and spawn offspring |
| `Executors.eatGrass(duration, callback)` | Eat grass block under entity |
| `Executors.moveToTarget(memory, ...)` | Move to a position stored in memory |

---

## Built-in Evaluators

| Method | Description |
|---|---|
| `Evaluators.panic()` | True when `PANIC_TICKS > 0` |
| `Evaluators.inLove()` | True when `IS_IN_LOVE == true` and not baby, no cooldown |
| `Evaluators.probability(chance, outOf)` | True randomly, e.g. `probability(1, 200)` = 0.5% |
| Lambda | `entity -> true` or any custom logic |

---

## Weighted Random Behaviors

Pick randomly between behaviors of equal priority:

```java
Behaviors.weighted(
    Set.<Behavior>of(
        BehaviorImpl.builder()
            .executor(Executors.idle(20, 60))
            .evaluator(entity -> true)
            .priority(1).weight(1).period(1).build(),
        BehaviorImpl.builder()
            .executor(Executors.roam())
            .evaluator(entity -> true)
            .priority(1).weight(3).period(1).build()  // 3x more likely to roam
    ),
    1,  // priority
    40  // period
)
```

---

## Breedable Entities

Implement `Breedable`, `Feedable`, and `Offspring` for full breeding support:

```java
public class MySheep extends IntelligentEntity
    implements Breedable, Feedable, Offspring {

    private boolean baby = false;
    private int breedCooldown = 0;

    @Override public boolean isBaby() { return baby; }
    @Override public void setBaby(boolean baby) { this.baby = baby; }
    @Override public int getBreedCooldown() { return breedCooldown; }
    @Override public void setBreedCooldown(int cooldown) { this.breedCooldown = cooldown; }

    @Override
    public boolean isBreedingItem(ItemStack item) {
        return item.material() == Material.WHEAT;
    }

    @Override
    public EntityCreature createOffspring() {
        MySheep baby = new MySheep();
        baby.setBaby(true);
        return baby;
    }
}
```

---

## Projectile Shooting

```java
Executors.shootProjectile(
    MemoryTypes.NEAREST_PLAYER,
    0.1, 0.1,    // speed, normalSpeed
    256.0,       // maxShootRangeSq
    60, 20,      // cooldown, attackDelay
    false,       // clearDataWhenLose
    shooter -> new EntityProjectile(shooter, EntityType.SNOWBALL)
)
```

## Credits
- [<img src="https://raw.githubusercontent.com/Kanelucky/Minestom4fun/refs/heads/master/minestom4fun-server/src/main/resources/icon.png" width="16"/>](https://github.com/Kanelucky/Minestom4fun) [Minestom4fun](https://github.com/Kanelucky/Minestom4fun)
- [<img src="https://raw.githubusercontent.com/AllayMC/Allay/master/docs/assets/logo/allay-chan-640x.png" width="16"/>](https://github.com/AllayMC/Allay) [AllayMC](https://github.com/AllayMC/Allay)
- [<img src="https://camo.githubusercontent.com/4c5c7b2960e83e04627adf11ddb342d48931dda868a978bd5126129a49a87187/68747470733a2f2f646f63732e706f7765726e756b6b6974782e6f72672f696d672f504e585f4c4f474f5f736d2e706e67" width="16"/>](https://github.com/PowerNukkitX/PowerNukkitX) [PowernukkitX](https://github.com/PowerNukkitX/PowerNukkitX)

---