package org.kanelucky.mobmind.core.entity.ai.executor

import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.attribute.Attribute
import net.minestom.server.entity.damage.Damage

import org.kanelucky.mobmind.api.entity.IntelligentEntity
import org.kanelucky.mobmind.api.entity.ai.executor.BehaviorExecutor
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryType

/**
 * Guardian-style beam attack — locks onto target then damages after delay.
 *
 * @param targetMemory memory type holding the target
 * @param maxRangeSq max squared distance to attack (default 256.0)
 * @param coolDownTick ticks between attacks (default 40)
 * @param attackDelay ticks to charge before dealing damage (default 20)
 * @param clearDataWhenLose clear memory when target lost (default false)
 *
 * Originally inspired by PNX GuardianAttackExecutor
 * Ported and adapted to Minestom by Kanelucky
 */
open class BeamAttackExecutor(
    private val targetMemory: MemoryType<out Any?>,
    private val maxRangeSq: Double = 256.0,
    private val coolDownTick: Int = 40,
    private val attackDelay: Int = 20,
    private val clearDataWhenLose: Boolean = false
) : BehaviorExecutor {

    private var tick1 = 0
    private var tick2 = 0
    private var target: LivingEntity? = null

    override fun onStart(entity: EntityCreature) {
        tick1 = 0
        tick2 = 0
        target = null
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
        if (distSq > maxRangeSq) return false

        EntityControlHelper.setLookTarget(
            entity,
            net.minestom.server.coordinate.Pos(
                currentTarget.position.x(),
                currentTarget.position.y() + currentTarget.eyeHeight,
                currentTarget.position.z()
            )
        )

        if (tick2 == 0 && tick1 > coolDownTick) {
            tick1 = 0
            tick2++
            onStartSequence(entity, currentTarget)
        } else if (tick2 != 0) {
            tick2++
            if (tick2 > attackDelay) {
                val damage = Damage.fromEntity(entity, getBeamDamage(entity))
                currentTarget.damage(damage)
                onAttack(entity, currentTarget)
                onEndSequence(entity)
                tick2 = 0
                return currentTarget.health > 0
            }
        }

        return true
    }

    override fun onStop(entity: EntityCreature) {
        if (entity !is IntelligentEntity) return
        EntityControlHelper.removeLookTarget(entity)
        if (clearDataWhenLose) entity.behaviorGroup.memoryStorage.clear(targetMemory)
        onEndSequence(entity)
        target = null
        tick1 = 0
        tick2 = 0
    }

    override fun onInterrupt(entity: EntityCreature) = onStop(entity)

    protected open fun getBeamDamage(entity: EntityCreature): Float =
        entity.getAttribute(Attribute.ATTACK_DAMAGE).value.toFloat()

    protected open fun onStartSequence(entity: EntityCreature, target: LivingEntity) {}
    protected open fun onEndSequence(entity: EntityCreature) {}
    protected open fun onAttack(entity: EntityCreature, target: LivingEntity) {}
}