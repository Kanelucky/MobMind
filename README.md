# MobMind

A modular entity AI library for [Minestom](https://minestom.net/) servers.

---

## Features

- Priority-based behavior system
- Sensor system — scan the environment independently from behavior logic
- Type-safe memory with `MemoryType<T>`
- 2D A* pathfinding for ground entities, 3D A* for flying/swimming entities
- Weighted random behavior selection
- Vanilla registry attributes — entities use Minecraft's default stats automatically
- Java & Kotlin compatible
- Extensible — create your own sensors, memory types, executors, and evaluators

---

## Architecture

```
api/     ← Interfaces and factories. Only touch this.
core/    ← Implementations. Never import directly.
vanilla/ ← Vanilla entity implementations.
example/ ← Example Minestom server.
```

---

## Installation

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.Kanelucky.MobMind:api:0.2.0")
    implementation("com.github.Kanelucky.MobMind:core:0.2.0")
}
```

### Initialize

```java
MobMind.INSTANCE.init(new CoreInitializer());
```

---

## Quick Start

### Vanilla mob

Stats are loaded from Minecraft's entity registry automatically:

```java
public class MyZombie extends HostileMob {
    public MyZombie() { super(EntityType.ZOMBIE); }

    @Override public Key getMobKey() { return Key.key("myplugin", "my_zombie"); }
    @Override public SoundEvent getHurtSound() { return SoundEvent.ENTITY_ZOMBIE_HURT; }

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
        this.behaviorGroup = buildBehaviorGroup().withEntity(this);
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
                    .evaluator(Evaluators.hasMemory(MemoryTypes.HURT_BY))
                    .priority(4).period(1).build()
            )
            .behavior(
                BehaviorImpl.builder()
                    .executor(Executors.meleeAttack(MemoryTypes.NEAREST_PLAYER))
                    .evaluator(Evaluators.hasMemory(MemoryTypes.NEAREST_PLAYER))
                    .priority(3).period(1).build()
            )
            .behavior(
                BehaviorImpl.builder()
                    .executor(Executors.roamBuilder().speed(0.1).maxRange(8).build())
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

The AI brain. Manages behaviors, sensors, memory, and controllers.

```java
BehaviorGroup.builder()
    .sensor(...)
    .behavior(...)
    .controller(Controllers.walk())
    .controller(Controllers.look())
    .build()
    .withEntity(this); // bind entity in one line
```

### Behavior

A unit of AI logic. Has an executor (what to do) and an evaluator (when to activate).

```java
BehaviorImpl.builder()
    .executor(Executors.idle(20, 60))
    .evaluator(entity -> true)
    .priority(1)
    .period(20)
    .build()
```

### Memory

Type-safe key-value storage per entity.

```java
// Built-in types
MemoryTypes.NEAREST_PLAYER           // Player
MemoryTypes.NEAREST_FEEDING_PLAYER   // Player
MemoryTypes.ATTACK_TARGET            // LivingEntity
MemoryTypes.MOVE_TARGET              // Point
MemoryTypes.LOOK_TARGET              // Point
MemoryTypes.PANIC_TICKS              // Integer
MemoryTypes.IS_IN_LOVE               // Boolean
MemoryTypes.HURT_BY                  // LivingEntity
MemoryTypes.HURT_BY_TICKS            // Integer

// Custom types
public static final MemoryType<LivingEntity> AGGRO_TARGET =
    new MemoryType<>("myplugin:aggro_target");
```

---

## Sensors

### Built-in

```java
Sensors.nearestPlayer()
Sensors.nearestPlayer(24.0, 0.0, 10)
Sensors.nearestFeedingPlayer()
Sensors.hurtBy()
Sensors.hurtBy(200)
Sensors.nearestEntity(MY_TARGET, EntityCreature.class)
Sensors.nearestEntity(MY_TARGET, EntityCreature.class, 16.0, 0.0, 20,
    e -> e.getEntityType() == EntityType.WOLF)
```

### HurtBySensor

Tracks the last entity that damaged this entity. Stores it in `MemoryTypes.HURT_BY` for a configurable duration:

```java
.sensor(Sensors.hurtBy(100))

// Retaliate against attacker
.behavior(
    BehaviorImpl.builder()
        .executor(Executors.meleeAttack(MemoryTypes.HURT_BY))
        .evaluator(Evaluators.hasMemory(MemoryTypes.HURT_BY))
        .priority(4).period(1).build()
)
```

### NearestEntitySensor

Finds the nearest entity of any type — not limited to players:

```java
public static final MemoryType<EntityCreature> NEAREST_WOLF =
    new MemoryType<>("myplugin:nearest_wolf");

.sensor(Sensors.nearestEntity(NEAREST_WOLF, EntityCreature.class, 16.0, 0.0, 20,
    e -> e.getEntityType() == EntityType.WOLF))

.behavior(
    BehaviorImpl.builder()
        .executor(Executors.flee(NEAREST_WOLF))
        .evaluator(Evaluators.hasMemory(NEAREST_WOLF))
        .priority(5).period(1).build()
)
```

### Custom sensor

```java
public class LowHealthSensor implements Sensor {
    @Override public int getPeriod() { return 5; }

    @Override
    public void sense(EntityCreature entity) {
        if (!(entity instanceof IntelligentEntity e)) return;
        boolean isLow = entity.getHealth() / entity.getAttributeValue(Attribute.MAX_HEALTH) < 0.3;
        e.getBehaviorGroup().getMemoryStorage().set(MyMemoryTypes.IS_LOW_HEALTH, isLow);
    }
}
```

---

## Evaluators

### Built-in

| Method | Description |
|---|---|
| `Evaluators.panic()` | True when `PANIC_TICKS > 0` |
| `Evaluators.inLove()` | True when `IS_IN_LOVE` and ready to breed |
| `Evaluators.probability(chance, outOf)` | Random chance |
| `Evaluators.hasMemory(type)` | True when memory type has a value |
| `Evaluators.lacksMemory(type)` | True when memory type is empty |
| `Evaluators.hasAllMemory(types...)` | True when all memory types have values |
| `Evaluators.hasAnyMemory(types...)` | True when any memory type has a value |

```java
// Before
.evaluator(entity -> {
    if (!(entity instanceof IntelligentEntity e)) return false;
    return e.getBehaviorGroup().getMemoryStorage().get(MemoryTypes.NEAREST_PLAYER) != null;
})

// After
.evaluator(Evaluators.hasMemory(MemoryTypes.NEAREST_PLAYER))
```

### Custom evaluator

```java
// Lambda
.evaluator(entity -> entity.getHealth() < 5)

// Class
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

## Executors

### Built-in

| Method | Description |
|---|---|
| `Executors.idle(min, max)` | Stand still |
| `Executors.roam(...)` | Wander randomly |
| `Executors.wander(radius)` | Move to one random position |
| `Executors.lookAround(min, max)` | Look in a random direction |
| `Executors.followEntity(memory, ...)` | Follow an entity |
| `Executors.lookAtEntity(memory, duration)` | Look at an entity |
| `Executors.meleeAttack(memory, ...)` | Chase and attack |
| `Executors.beamAttack(memory, ...)` | Delayed beam attack |
| `Executors.shootProjectile(memory, ..., supplier)` | Shoot a projectile |
| `Executors.jump(...)` | Jump periodically |
| `Executors.panic(...)` | Flee in a random direction |
| `Executors.flee(memory, ...)` | Run away from a specific entity |
| `Executors.breeding(...)` | Find a mate and spawn offspring |
| `Executors.eatGrass(duration, callback)` | Eat grass |
| `Executors.moveToTarget(memory, ...)` | Move to a position |

### Builder pattern

For executors with many parameters, builders are available:

```java
// Verbose
Executors.roam(0.1, 0.1, 8, 20, false, 100, false, 10)

// Builder
Executors.roamBuilder()
    .speed(0.1)
    .maxRange(8)
    .avoidWater(false)
    .build()

Executors.meleeAttackBuilder(MemoryTypes.NEAREST_PLAYER)
    .speed(0.1)
    .attackCooldown(15)
    .onAttack((attacker, target) -> {
        if (attacker instanceof LivingEntity le) le.swingMainHand();
    })
    .build()

Executors.fleeBuilder(MY_THREAT)
    .fleeSpeed(0.2)
    .maxFleeRangeSq(400.0)
    .build()
```

### Custom executor

```java
public class SummonLightningExecutor implements BehaviorExecutor {
    private int cooldown = 0;

    @Override
    public boolean execute(EntityCreature entity) {
        if (cooldown-- > 0) return true;
        // custom logic here
        cooldown = 60;
        return true;
    }

    @Override public void onStart(EntityCreature entity) { cooldown = 0; }
    @Override public void onStop(EntityCreature entity) {}
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

## License
This project is licensed under Apache License. Please see the [LICENSE](LICENSE) file for details.

---

## Credits
- [<img src="https://raw.githubusercontent.com/Kanelucky/Minestom4fun/refs/heads/master/minestom4fun-server/src/main/resources/icon.png" width="16"/>](https://github.com/Kanelucky/Minestom4fun) [Minestom4fun](https://github.com/Kanelucky/Minestom4fun)
- [<img src="https://raw.githubusercontent.com/AllayMC/Allay/master/docs/assets/logo/allay-chan-640x.png" width="16"/>](https://github.com/AllayMC/Allay) [AllayMC](https://github.com/AllayMC/Allay)
- [<img src="https://raw.githubusercontent.com/PowerNukkitX/PowerNukkitX/refs/heads/master/.github/logo.png" width="16"/>](https://github.com/PowerNukkitX/PowerNukkitX) [PowernukkitX](https://github.com/PowerNukkitX/PowerNukkitX)