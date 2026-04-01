package org.kanelucky.mobmind.core.entity.ai.executor

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.attribute.Attribute
import net.minestom.server.entity.damage.Damage

import org.kanelucky.mobmind.api.entity.IntelligentEntity
import org.kanelucky.mobmind.api.entity.ai.executor.BehaviorExecutor
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryType

/**
 * Universal melee attack executor.
 * Moves toward target, then attacks when in range.
 *
 * @param targetMemory memory type holding the attack target
 * @param speed movement speed toward target (default 0.15)
 * @param normalSpeed movement speed restored after (default 0.1)
 * @param maxSenseRangeSq max squared distance to pursue target (default 256.0)
 * @param attackRangeSq squared distance to trigger attack (default 2.5)
 * @param attackCooldown ticks between attacks (default 20)
 * @param clearDataWhenLose clear memory when target is lost (default false)
 *
 * Originally inspired by PowerNukkitX MeleeAttackExecutor
 * Ported and adapted to Minestom by Kanelucky
 */
open class MeleeAttackExecutor(
    private val targetMemory: MemoryType<out Any?>,
    private val speed: Double = 0.15,
    private val normalSpeed: Double = 0.1,
    private val maxSenseRangeSq: Double = 256.0,
    private val attackRangeSq: Double = 2.5,
    private val attackCooldown: Int = 20,
    private val clearDataWhenLose: Boolean = false,
    private val attackCallback: ((EntityCreature, LivingEntity) -> Unit)? = null
) : BehaviorExecutor {

    private var attackTick = 0
    private var lastTargetPos: Pos? = null

    override fun onStart(entity: EntityCreature) {
        attackTick = 0
        lastTargetPos = null
        entity.getAttribute(Attribute.MOVEMENT_SPEED).baseValue = speed
    }

    override fun execute(entity: EntityCreature): Boolean {
        if (entity !is IntelligentEntity) return false
        attackTick++

        val target = entity.behaviorGroup.memoryStorage.get(targetMemory)
            ?.let { it as? net.minestom.server.entity.LivingEntity }
            ?: return false

        if (target.isDead) return false

        val distSq = entity.position.distanceSquared(target.position)
        if (distSq > maxSenseRangeSq) return false

        val targetPos = Pos(target.position.x(), target.position.y(), target.position.z())
        if (lastTargetPos == null || lastTargetPos!!.distanceSquared(targetPos) > 1.0) {
            EntityControlHelper.setRouteTarget(entity, targetPos)
            lastTargetPos = targetPos
        }
        EntityControlHelper.setLookTarget(entity,
            Pos(target.position.x(), target.position.y() + target.eyeHeight, target.position.z())
        )

        if (distSq <= attackRangeSq && attackTick >= attackCooldown) {
            val damage = Damage.fromEntity(entity, getAttackDamage(entity))
            target.damage(damage)
            onAttack(entity, target)
            attackTick = 0
            return target.health > 0
        }

        return true
    }

    override fun onStop(entity: EntityCreature) {
        if (entity !is IntelligentEntity) return
        entity.getAttribute(Attribute.MOVEMENT_SPEED).baseValue = normalSpeed
        EntityControlHelper.removeRouteTarget(entity)
        EntityControlHelper.removeLookTarget(entity)
        if (clearDataWhenLose) {
            entity.behaviorGroup.memoryStorage.clear(targetMemory)
        }
        attackTick = 0
        lastTargetPos = null
    }

    override fun onInterrupt(entity: EntityCreature) = onStop(entity)

    /**
     * Returns the attack damage for this entity.
     * Override to customize damage calculation.
     */
    protected open fun getAttackDamage(entity: EntityCreature): Float =
        entity.getAttribute(Attribute.ATTACK_DAMAGE).value.toFloat()

    /**
     * Called after a successful attack.
     * Override to add effects, sounds, animations, etc.
     */
    protected open fun onAttack(attacker: EntityCreature, target: LivingEntity) {
        attackCallback?.invoke(attacker, target)
    }
}