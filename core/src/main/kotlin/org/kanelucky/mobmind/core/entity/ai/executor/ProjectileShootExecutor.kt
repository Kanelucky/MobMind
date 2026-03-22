package org.kanelucky.mobmind.core.entity.ai.executor

import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.EntityProjectile
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.attribute.Attribute
import org.kanelucky.mobmind.api.entity.IntelligentEntity
import org.kanelucky.mobmind.api.entity.ai.executor.BehaviorExecutor
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryType

/**
 * Makes the entity shoot a projectile at its target.
 *
 * @param targetMemory memory type holding the target
 * @param speed movement speed toward target if out of range (default 0.1)
 * @param normalSpeed movement speed restored after (default 0.1)
 * @param maxShootRangeSq max squared distance to shoot (default 256.0)
 * @param coolDownTick ticks between shots (default 40)
 * @param attackDelay ticks before projectile is fired after sequence starts (default 20)
 * @param clearDataWhenLose clear memory when target lost (default false)
 * @param projectileSupplier creates the projectile entity
 *
 * Originally inspired by PNX BreezeShootExecutor
 * Ported and adapted to Minestom by Kanelucky
 */
open class ProjectileShootExecutor(
    private val targetMemory: MemoryType<out Any?>,
    private val speed: Double = 0.1,
    private val normalSpeed: Double = 0.1,
    private val maxShootRangeSq: Double = 256.0,
    private val coolDownTick: Int = 40,
    private val attackDelay: Int = 20,
    private val clearDataWhenLose: Boolean = false,
    private val projectileSupplier: (EntityCreature) -> EntityProjectile
) : BehaviorExecutor {

    private var tick1 = 0 // cooldown counter
    private var tick2 = 0 // attack delay counter
    private var target: LivingEntity? = null

    override fun onStart(entity: EntityCreature) {
        tick1 = 0
        tick2 = 0
        target = null
        entity.getAttribute(Attribute.MOVEMENT_SPEED).baseValue = speed
    }

    override fun execute(entity: EntityCreature): Boolean {
        if (entity !is IntelligentEntity) return false

        if (tick2 == 0) tick1++

        val newTarget = entity.behaviorGroup.memoryStorage.get(targetMemory)
            ?.let { it as? LivingEntity }
            ?: return false

        if (target == null) target = newTarget
        if (newTarget.isDead) return false

        target = newTarget
        val currentTarget = target ?: return false

        val distSq = entity.position.distanceSquared(currentTarget.position)

        // Move toward target if out of range
        if (distSq > maxShootRangeSq) {
            EntityControlHelper.setRouteTarget(entity, currentTarget.position)
        } else {
            EntityControlHelper.removeRouteTarget(entity)
        }
        EntityControlHelper.setLookTarget(entity,
            Pos(currentTarget.position.x(), currentTarget.position.y() + currentTarget.eyeHeight, currentTarget.position.z())
        )

        if (tick2 == 0 && tick1 > coolDownTick) {
            if (distSq <= maxShootRangeSq) {
                tick1 = 0
                tick2++
                onStartSequence(entity, currentTarget)
            }
        } else if (tick2 != 0) {
            tick2++
            if (tick2 > attackDelay) {
                shoot(entity, currentTarget)
                tick2 = 0
                return currentTarget.health > 0
            }
        }

        return true
    }

    override fun onStop(entity: EntityCreature) {
        if (entity !is IntelligentEntity) return
        entity.getAttribute(Attribute.MOVEMENT_SPEED).baseValue = normalSpeed
        EntityControlHelper.removeRouteTarget(entity)
        EntityControlHelper.removeLookTarget(entity)
        if (clearDataWhenLose) entity.behaviorGroup.memoryStorage.clear(targetMemory)
        onEndSequence(entity)
        target = null
        tick1 = 0
        tick2 = 0
    }

    override fun onInterrupt(entity: EntityCreature) = onStop(entity)

    private fun shoot(entity: EntityCreature, target: LivingEntity) {
        val instance = entity.instance ?: return
        val projectile = projectileSupplier(entity)

        val eyePos = Pos(
            entity.position.x(),
            entity.position.y() + entity.eyeHeight,
            entity.position.z()
        )

        val dx = target.position.x() - eyePos.x()
        val dy = (target.position.y() + target.eyeHeight / 2) - eyePos.y()
        val dz = target.position.z() - eyePos.z()
        val length = Math.sqrt(dx * dx + dy * dy + dz * dz)

        val velocity = net.minestom.server.coordinate.Vec(
            dx / length * 1.5,
            dy / length * 1.5,
            dz / length * 1.5
        )

        projectile.setInstance(instance, eyePos).thenRun {
            projectile.velocity = velocity
            onShoot(entity, target, projectile)
        }
    }

    /**
     * Called when the shoot sequence starts (before delay).
     * Override to play charge sound, animation, etc.
     */
    protected open fun onStartSequence(entity: EntityCreature, target: LivingEntity) {}

    /**
     * Called when the shoot sequence ends.
     * Override to play end animation, etc.
     */
    protected open fun onEndSequence(entity: EntityCreature) {}

    /**
     * Called after projectile is spawned.
     * Override to play shoot sound, add effects, etc.
     */
    protected open fun onShoot(entity: EntityCreature, target: LivingEntity, projectile: EntityProjectile) {}
}