package org.kanelucky.mobmind.core.entity.ai.route.finder

import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.EntityCreature
import net.minestom.server.instance.Instance

import org.kanelucky.mobmind.core.entity.ai.route.Node
import org.kanelucky.mobmind.core.entity.ai.route.posevaluator.SpacePosEvaluator

import kotlin.math.*

/**
 * 3D A* pathfinder for flying/swimming entities.
 *
 * @author Kanelucky || Allay (https://github.com/AllayMC/Allay)
 */
class SpaceAStarRouteFinder(
    private val spacePosEvaluator: SpacePosEvaluator,
    maxExpandedNodes: Int = 100,
    maxFallDistance: Int = 3
) : FlatAStarRouteFinder(null, maxExpandedNodes, maxFallDistance) {

    companion object {
        val SQRT3_MINUS_SQRT2 = sqrt(3.0) - sqrt(2.0)
        val SPACE_NEIGHBORS = arrayOf(
            intArrayOf(1, 0, 0), intArrayOf(-1, 0, 0), intArrayOf(0, 0, 1), intArrayOf(0, 0, -1),
            intArrayOf(1, 0, 1), intArrayOf(1, 0, -1), intArrayOf(-1, 0, 1), intArrayOf(-1, 0, -1),
            intArrayOf(0, 1, 0),
            intArrayOf(1, 1, 0), intArrayOf(-1, 1, 0), intArrayOf(0, 1, 1), intArrayOf(0, 1, -1),
            intArrayOf(1, 1, 1), intArrayOf(1, 1, -1), intArrayOf(-1, 1, 1), intArrayOf(-1, 1, -1),
            intArrayOf(0, -1, 0),
            intArrayOf(1, -1, 0), intArrayOf(-1, -1, 0), intArrayOf(0, -1, 1), intArrayOf(0, -1, -1),
            intArrayOf(1, -1, 1), intArrayOf(1, -1, -1), intArrayOf(-1, -1, 1), intArrayOf(-1, -1, -1)
        )
    }

    override fun getNeighbors(current: Node, instance: Instance, entity: EntityCreature) {
        val cx = floor(current.x).toInt()
        val cy = floor(current.y).toInt()
        val cz = floor(current.z).toInt()

        for (offset in SPACE_NEIGHBORS) {
            val nx = cx + offset[0]
            val ny = cy + offset[1]
            val nz = cz + offset[2]
            val pos = Vec(nx + 0.5, ny + 0.5, nz + 0.5)
            if (!instance.getBlock(nx, ny, nz).isSolid && spacePosEvaluator.evaluate(entity, pos)) {
                neighborBuffer.add(Node(pos.x(), pos.y(), pos.z()))
            }
        }
    }

    override fun isCloseEnough(a: Node, b: Node) = a.vec.distanceSquared(b.vec) < 1.5

    override fun heuristic(a: Node, b: Node): Double {
        val dx = abs(a.x - b.x);
        val dy = abs(a.y - b.y);
        val dz = abs(a.z - b.z)
        val max = maxOf(dx, dy, dz);
        val min = minOf(dx, dy, dz)
        val mid = dx + dy + dz - max - min
        return SQRT3_MINUS_SQRT2 * min + SQRT2_MINUS_1 * mid + max
    }
}