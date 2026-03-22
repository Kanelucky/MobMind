package org.kanelucky.mobmind.core.entity.ai.executor

import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.EntityCreature
import org.kanelucky.mobmind.api.entity.ai.executor.BehaviorExecutor
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Makes the entity jump periodically.
 *
 * @param jumpInterval ticks between jumps (default 80)
 * @param jumpDelay ticks before actually jumping after sequence starts (default 10)
 * @param jumpPower vertical velocity of jump (default 0.6)
 * @param jumpPowerVariance random variance added to jump power (default 0.5)
 *
 * Originally inspired by PNX BreezeJumpExecutor
 * Ported and adapted to Minestom by Kanelucky
 */
open class JumpExecutor(
    private val jumpInterval: Int = 80,
    private val jumpDelay: Int = 10,
    private val jumpPower: Double = 0.6,
    private val jumpPowerVariance: Double = 0.5
) : BehaviorExecutor {

    private var tick = 0
    private var prepareTick = -1

    override fun onStart(entity: EntityCreature) {
        tick = 0
        prepareTick = -1
    }

    override fun execute(entity: EntityCreature): Boolean {
        tick++

        if (tick % jumpInterval == 0) {
            prepareTick = tick
            onPrepareJump(entity)
        } else if (prepareTick != -1 && tick - prepareTick >= jumpDelay) {
            doJump(entity)
            prepareTick = -1
        }

        return true
    }

    override fun onStop(entity: EntityCreature) {
        tick = 0
        prepareTick = -1
    }

    override fun onInterrupt(entity: EntityCreature) = onStop(entity)

    private fun doJump(entity: EntityCreature) {
        val yaw = entity.position.yaw().toDouble()
        val power = jumpPower + Random.nextDouble(jumpPowerVariance)
        val direction = Vec(
            -sin(Math.toRadians(yaw)) * 0.5,
            power,
            cos(Math.toRadians(yaw)) * 0.5
        )
        entity.velocity = direction
        onJump(entity)
    }

    /**
     * Called when jump sequence starts (before delay).
     * Override to play charge animation, sound, etc.
     */
    protected open fun onPrepareJump(entity: EntityCreature) {}

    /**
     * Called when entity actually jumps.
     * Override to play jump sound, particle, etc.
     */
    protected open fun onJump(entity: EntityCreature) {}
}