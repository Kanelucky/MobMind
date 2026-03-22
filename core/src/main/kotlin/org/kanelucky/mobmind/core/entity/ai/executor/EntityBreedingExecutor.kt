package org.kanelucky.mobmind.core.entity.ai.executor

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.attribute.Attribute
import org.kanelucky.mobmind.api.entity.IntelligentEntity
import org.kanelucky.mobmind.api.entity.ai.Breedable
import org.kanelucky.mobmind.api.entity.ai.Offspring
import org.kanelucky.mobmind.api.entity.ai.executor.BehaviorExecutor
import org.kanelucky.mobmind.api.entity.ai.memory.MemoryTypes
import org.kanelucky.mobmind.core.entity.ai.memory.EntityMemoryTypes.ENTITY_SPOUSE

/**
 * Finds a nearby in-love entity of the same type, moves toward it, and spawns a baby.
 * Works with any [EntityCreature] implementing [Breedable] and [Offspring].
 *
 * @param duration ticks to approach before spawning baby (default 60)
 * @param speed movement speed while approaching spouse (default 0.3)
 * @param normalSpeed movement speed restored after breeding (default 0.1)
 *
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 *
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
class EntityBreedingExecutor(
    private val duration: Int = 60,
    private val speed: Double = 0.3,
    private val normalSpeed: Double = 0.1
) : BehaviorExecutor {

    companion object {
        private const val FIND_RANGE_SQ = 256.0
        private const val BREED_DIST_SQ = 4.0
        private const val BABY_BREED_COOLDOWN = 24000
        private const val PARENT_BREED_COOLDOWN = 6000
    }

    private var tickCounter = 0
    private var spouse: EntityCreature? = null
    private var isInitiator = false
    private var lastSpousePos: Pos? = null

    override fun onStart(entity: EntityCreature) {
        if (entity !is IntelligentEntity) return
        tickCounter = 0
        spouse = null
        isInitiator = false
        lastSpousePos = null
        entity.getAttribute(Attribute.MOVEMENT_SPEED).baseValue = speed
    }

    override fun execute(entity: EntityCreature): Boolean {
        if (entity !is IntelligentEntity) return false
        if (entity !is Breedable || entity !is Offspring) return false
        tickCounter++

        if (spouse == null || spouse!!.isDead) {
            spouse = resolveSpouse(entity)
        }

        val currentSpouse = spouse ?: return tickCounter < duration + 60

        updateMovement(entity, currentSpouse)

        if (isInitiator) {
            val distSq = entity.position.distanceSquared(currentSpouse.position)
            if (distSq < BREED_DIST_SQ && tickCounter >= duration) {
                spawnBaby(entity, currentSpouse)
                clearLoveState(entity)
                clearLoveState(currentSpouse)
                return false
            }
        }

        return tickCounter < duration + 60
    }

    override fun onStop(entity: EntityCreature) {
        if (entity !is IntelligentEntity) return
        entity.getAttribute(Attribute.MOVEMENT_SPEED).baseValue = normalSpeed
        EntityControlHelper.removeRouteTarget(entity)
        EntityControlHelper.removeLookTarget(entity)
        entity.behaviorGroup.memoryStorage.clear(ENTITY_SPOUSE)
        spouse = null
        isInitiator = false
    }

    override fun onInterrupt(entity: EntityCreature) = onStop(entity)


    /**
     * Override to customize spouse search logic.
     */
    protected open fun isValidSpouse(entity: EntityCreature, candidate: EntityCreature): Boolean {
        if (candidate === entity) return false
        if (candidate.entityType != entity.entityType) return false
        if (candidate.isDead) return false
        if (candidate !is Breedable || candidate.isBaby || candidate.breedCooldown > 0) return false
        if (candidate !is IntelligentEntity) return false
        if (candidate.behaviorGroup.memoryStorage.get(MemoryTypes.IS_IN_LOVE) != true) return false
        if (candidate.behaviorGroup.memoryStorage.get(ENTITY_SPOUSE) != null) return false
        if (entity.position.distanceSquared(candidate.position) > FIND_RANGE_SQ) return false
        return true
    }

    /**
     * Override to customize baby spawn behavior.
     */
    protected open fun onBabySpawned(baby: EntityCreature, parent: EntityCreature, spouse: EntityCreature) {}

    private fun resolveSpouse(entity: EntityCreature): EntityCreature? {
        if (entity !is IntelligentEntity) return null

        val fromMemory = entity.behaviorGroup.memoryStorage.get(ENTITY_SPOUSE)
            ?.takeIf { !it.isDead }
        if (fromMemory != null) return fromMemory

        return findSpouse(entity)?.also { found ->
            isInitiator = true
            entity.behaviorGroup.memoryStorage.set(ENTITY_SPOUSE, found)
            (found as? IntelligentEntity)?.behaviorGroup?.memoryStorage?.set(ENTITY_SPOUSE, entity)
        }
    }

    private fun findSpouse(entity: EntityCreature): EntityCreature? =
        entity.instance?.entities
            ?.filterIsInstance<EntityCreature>()
            ?.filter { isValidSpouse(entity, it) }
            ?.minByOrNull { entity.position.distanceSquared(it.position) }

    private fun updateMovement(entity: EntityCreature, spouse: EntityCreature) {
        val spousePos = spouse.position
        val target = Pos(spousePos.x(), spousePos.y(), spousePos.z())
        if (lastSpousePos == null || lastSpousePos!!.distanceSquared(target) > 1.0) {
            EntityControlHelper.setRouteTarget(entity as IntelligentEntity, target)
            lastSpousePos = target
        }
        EntityControlHelper.setLookTarget(
            entity as IntelligentEntity,
            Pos(spousePos.x(), spousePos.y() + spouse.eyeHeight, spousePos.z())
        )
    }

    private fun spawnBaby(parent: EntityCreature, spouse: EntityCreature) {
        if (parent !is Offspring || parent !is Breedable) return
        if (spouse !is Breedable) return

        val baby = parent.createOffspring()
        if (baby is Breedable) {
            baby.isBaby = true
            baby.breedCooldown = BABY_BREED_COOLDOWN
        }
        baby.setInstance(parent.instance!!, parent.position)

        parent.breedCooldown = PARENT_BREED_COOLDOWN
        spouse.breedCooldown = PARENT_BREED_COOLDOWN

        onBabySpawned(baby, parent, spouse)
    }

    private fun clearLoveState(entity: EntityCreature) {
        if (entity !is IntelligentEntity) return
        entity.behaviorGroup.memoryStorage.set(MemoryTypes.IS_IN_LOVE, false)
        entity.behaviorGroup.memoryStorage.clear(ENTITY_SPOUSE)
    }
}