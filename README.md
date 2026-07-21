# MobMind

A flexible, modular entity AI library for [Minestom](https://minestom.net/) servers. Build custom entities with priority-based behaviors, sensors, memory, and pathfinding

---

## Features

- **Priority-based behavior system** — behaviors run in priority order, higher priority interrupts lower
- **Sensor system** — scan the environment independently from behavior logic
- **Type-safe memory** — store and retrieve entity state with `MemoryType<T>`
- **Pathfinding** — 2D A* for ground entities, 3D A* for flying/swimming entities
- **Weighted random behaviors** — pick randomly between behaviors with configurable weights
- **Vanilla registry attributes** — entities automatically use Minecraft's default stats
- **Java & Kotlin friendly** — API is Java-first, works naturally in Kotlin too
- **Fully extensible** — create your own sensors, memory types, executors, and evaluators

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

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.Kanelucky.MobMind:api:0.1.3")
    implementation("com.github.Kanelucky.MobMind:core:0.1.3")
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

### Vanilla mob

Stats are automatically loaded from Minecraft's entity registry. No need to override anything unless you want custom values:

```java
public class MyZombie extends HostileMob {
    public MyZombie() { super(EntityType.ZOMBIE); }

    @Override public Key getMobKey() { return Key.key("myplugin", "my_zombie"); }
    @Override public SoundEvent getHurtSound() { return SoundEvent.ENTITY_ZOMBIE_HURT; }

    // Health, attack, speed automatically from Minecraft registry
    // Override only if you want custom values:
    // @Override protected double getBaseHealth() { return 200.0; }
}
```

### Custom entity

```java
public class MyZombie extends IntelligentEntity {

    private final BehaviorGroup behaviorGroup;

    public MyZombie() {
        super(EntityType.ZOMBIE);
        this.behaviorGroup = buildBehaviorGroup();
        this.behaviorGroup.setEntity(this);
    }

    @Override public Key getMobKey() { return Key.key("myplugin", "my_zombie"); }
    @Override public BehaviorGroup getBehaviorGroup() { return behaviorGroup; }

    private BehaviorGroup buildBehaviorGroup() {
        return BehaviorGroup.builder()
            .sensor(Sensors.nearestPlayer())
            .sensor(Sensors.hurtBy())
            .behavior(
                BehaviorImpl.builder()
                    .executor(Executors.meleeAttack(MemoryTypes.HURT_BY))
                    .evaluator(entity -> {
                        if (!(entity instanceof IntelligentEntity e)) return false;
                        return e.getBehaviorGroup().getMemoryStorage().get(MemoryTypes.HURT_BY) != null;
                    })
                    .priority(4).period(1).build()
            )
            .behavior(
                BehaviorImpl.builder()
                    .executor(Executors.meleeAttack(MemoryTypes.NEAREST_PLAYER))
                    .evaluator(entity -> {
                        if (!(entity instanceof IntelligentEntity e)) return false;
                        return e.getBehaviorGroup().getMemoryStorage().get(MemoryTypes.NEAREST_PLAYER) != null;
                    })
                    .priority(3).period(1).build()
            )
            .behavior(
                BehaviorImpl.builder()
                    .executor(Executors.roam())
                    .evaluator(entity -> true)
                    .priority(1).period(1).build()
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

Type-safe key-value storage per entity, read and written by sensors and executors.

```java
// Built-in types
MemoryTypes.NEAREST_PLAYER           // Player
MemoryTypes.NEAREST_FEEDING_PLAYER   // Player
MemoryTypes.ATTACK_TARGET            // LivingEntity
MemoryTypes.MOVE_TARGET              // Point
MemoryTypes.LOOK_TARGET              // Point
MemoryTypes.PANIC_TICKS              // Integer
MemoryTypes.IS_IN_LOVE               // Boolean
MemoryTypes.HURT_BY                  // LivingEntity — set when damaged
MemoryTypes.HURT_BY_TICKS            // Integer — ticks remaining
```

#### Custom memory types

You can define any memory type for your own use cases:

```java
public static final MemoryType<LivingEntity> AGGRO_TARGET =
    new MemoryType<>("myplugin:aggro_target");

public static final MemoryType<Integer> RAGE_LEVEL =
    new MemoryType<>("myplugin:rage_level");
```

---

## Sensors

Scans the environment and writes to memory at a configurable interval.

### Built-in sensors

```java
Sensors.nearestPlayer()                        // range 16, period 20
Sensors.nearestPlayer(24.0, 0.0, 10)          // custom range and period
Sensors.nearestFeedingPlayer()                 // range 8, period 20
Sensors.hurtBy()                               // tracks who last damaged this entity
Sensors.hurtBy(200)                            // custom clearAfterTicks
Sensors.nearestEntity(MY_TARGET, Wolf.class)   // nearest entity of any type
Sensors.nearestEntity(MY_TARGET, Wolf.class, 24.0, 0.0, 10, wolf -> !wolf.isBaby())
```

### HurtBySensor

Tracks who last damaged the entity. Stores the attacker in `MemoryTypes.HURT_BY` for a configurable duration. Useful for aggro systems — making mobs retaliate against their attacker with higher priority than their normal target:

```java
BehaviorGroup.builder()
    .sensor(Sensors.nearestPlayer())
    .sensor(Sensors.hurtBy(100)) // remember attacker for 100 ticks
    // Retaliate against attacker — highest priority
    .behavior(
        BehaviorImpl.builder()
            .executor(Executors.meleeAttack(MemoryTypes.HURT_BY))
            .evaluator(entity -> {
                if (!(entity instanceof IntelligentEntity e)) return false;
                return e.getBehaviorGroup().getMemoryStorage().get(MemoryTypes.HURT_BY) != null;
            })
            .priority(4).period(1).build()
    )
    // Normal attack — lower priority
    .behavior(
        BehaviorImpl.builder()
            .executor(Executors.meleeAttack(MemoryTypes.NEAREST_PLAYER))
            .evaluator(entity -> {
                if (!(entity instanceof IntelligentEntity e)) return false;
                return e.getBehaviorGroup().getMemoryStorage().get(MemoryTypes.NEAREST_PLAYER) != null;
            })
            .priority(3).period(1).build()
    )
    .build();
```

### NearestEntitySensor

Detects the nearest entity of any type. Not limited to players — useful for mobs that interact with other mobs:

```java
// Detect nearest wolf
public static final MemoryType<EntityCreature> NEAREST_WOLF =
        new MemoryType<>("myplugin:nearest_wolf");

// With predicate — only adult wolves
.sensor(Sensors.nearestEntity(NEAREST_WOLF, EntityCreature.class, 16.0, 0.0, 20,
        e -> e.getEntityType() == EntityType.WOLF))

// Sheep flees from nearby wolf
.behavior(
    BehaviorImpl.builder()
        .executor(Executors.flee(NEAREST_WOLF))
        .evaluator(entity -> {
            if (!(entity instanceof IntelligentEntity e)) return false;
            return e.getBehaviorGroup().getMemoryStorage().get(NEAREST_WOLF) != null;
        })
        .priority(5).period(1).build()
)
```

### Custom sensor

You have full control — create any sensor for any use case:

```java
public class LowHealthSensor implements Sensor {
    @Override
    public int getPeriod() { return 5; } // check every 5 ticks

    @Override
    public void sense(EntityCreature entity) {
        if (!(entity instanceof IntelligentEntity e)) return;
        boolean isLowHealth = entity.getHealth() / entity.getAttributeValue(Attribute.MAX_HEALTH) < 0.3;
        e.getBehaviorGroup().getMemoryStorage().set(MyMemoryTypes.IS_LOW_HEALTH, isLowHealth);
    }
}

// Register
.sensor(new LowHealthSensor())
```

---

## Executors

### Built-in executors

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
| `Executors.flee(memory, ...)` | Run away from a specific entity |
| `Executors.breeding(...)` | Find a mate and spawn offspring |
| `Executors.eatGrass(duration, callback)` | Eat grass block under entity |
| `Executors.moveToTarget(memory, ...)` | Move to a position stored in memory |

### Custom executor

Implement `BehaviorExecutor` directly — or extend a built-in one and override its hooks:

```java
// From scratch
public class SummonLightningExecutor implements BehaviorExecutor {
    private int cooldown = 0;

    @Override
    public boolean execute(EntityCreature entity) {
        if (cooldown-- > 0) return true;
        // strike lightning at target
        cooldown = 60;
        return true;
    }

    @Override public void onStart(EntityCreature entity) { cooldown = 0; }
    @Override public void onStop(EntityCreature entity) {}
}

// Extend built-in — add swing arm on melee attack
Executors.meleeAttack(MemoryTypes.NEAREST_PLAYER, (attacker, target) -> {
    if (attacker instanceof LivingEntity le) le.swingMainHand();
})
```

---

## Evaluators

### Built-in evaluators

| Method | Description |
|---|---|
| `Evaluators.panic()` | True when `PANIC_TICKS > 0` |
| `Evaluators.inLove()` | True when `IS_IN_LOVE` and not baby, cooldown is 0 |
| `Evaluators.probability(chance, outOf)` | Random chance, e.g. `probability(1, 200)` |
| Lambda | `entity -> true` or any inline condition |

### Custom evaluator

Any lambda or class implementing `BehaviorEvaluator`:

```java
// Inline lambda
.evaluator(entity -> {
    if (!(entity instanceof IntelligentEntity e)) return false;
    Boolean isLow = e.getBehaviorGroup().getMemoryStorage().get(MyMemoryTypes.IS_LOW_HEALTH);
    return Boolean.TRUE.equals(isLow);
})

// Reusable class
public class IsLowHealthEvaluator implements BehaviorEvaluator {
    @Override
    public boolean evaluate(EntityCreature entity) {
        if (!(entity instanceof IntelligentEntity e)) return false;
        return Boolean.TRUE.equals(
            e.getBehaviorGroup().getMemoryStorage().get(MyMemoryTypes.IS_LOW_HEALTH)
        );
    }
}
```

---

## Weighted Random Behaviors

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
            .priority(1).weight(3).period(1).build()
    ),
    1,  // priority
    40  // period
)
```

---

## Breedable Entities

Implement `Breedable`, `Feedable`, and `Offspring`:

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
        MySheep lamb = new MySheep();
        lamb.setBaby(true);
        return lamb;
    }
}
```

---

## Credits
- [<img src="https://raw.githubusercontent.com/Kanelucky/Minestom4fun/refs/heads/master/minestom4fun-server/src/main/resources/icon.png" width="16"/>](https://github.com/Kanelucky/Minestom4fun) [Minestom4fun](https://github.com/Kanelucky/Minestom4fun)
- [<img src="https://raw.githubusercontent.com/AllayMC/Allay/master/docs/assets/logo/allay-chan-640x.png" width="16"/>](https://github.com/AllayMC/Allay) [AllayMC](https://github.com/AllayMC/Allay)
- [<img src="https://raw.githubusercontent.com/PowerNukkitX/PowerNukkitX/refs/heads/master/.github/logo.png" width="16"/>](https://github.com/PowerNukkitX/PowerNukkitX) [PowernukkitX](https://github.com/PowerNukkitX/PowerNukkitX)