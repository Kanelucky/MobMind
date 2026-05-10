package org.kanelucky.mobmind.core.entity.ai.route.finder

import net.minestom.server.entity.EntityCreature
import net.minestom.server.instance.Instance

import org.kanelucky.mobmind.core.entity.ai.route.Node
import org.kanelucky.mobmind.core.entity.ai.route.posevaluator.GroundPosEvaluator

import java.util.PriorityQueue

import kotlin.math.*

/**
 * 2D A* pathfinder for ground-walking entities.
 *
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 *
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
open class FlatAStarRouteFinder(
    protected val groundPosEvaluator: GroundPosEvaluator? = null,
    protected val maxExpandedNodes: Int = 100,
    protected val maxFallDistance: Int = 3
) {
    companion object {
        val SQRT2_MINUS_1 = sqrt(2.0) - 1
        val FLAT_NEIGHBORS = arrayOf(
            intArrayOf(1, 0), intArrayOf(-1, 0), intArrayOf(0, 1), intArrayOf(0, -1),
            intArrayOf(1, 1), intArrayOf(1, -1), intArrayOf(-1, 1), intArrayOf(-1, -1)
        )
    }

    private var walkableCache = mutableMapOf<Long, Boolean>()

    fun search(entity: EntityCreature, targetX: Double, targetY: Double, targetZ: Double): List<Node> {
        walkableCache = mutableMapOf()
        return try {
            doSearch(entity, targetX, targetY, targetZ)
        } finally {
            walkableCache = mutableMapOf()
        }
    }

    protected open fun doSearch(entity: EntityCreature, tx: Double, ty: Double, tz: Double): List<Node> {
        val instance = entity.instance ?: return emptyList()
        val pos = entity.position

        val startNode = Node(floor(pos.x()).toInt() + 0.5, pos.y(), floor(pos.z()).toInt() + 0.5)
        val endNode = Node(floor(tx).toInt() + 0.5, ty, floor(tz).toInt() + 0.5)

        startNode.h = heuristic(startNode, endNode)

        val openSet = PriorityQueue<Node>()
        val openMap = HashMap<Node, Node>()
        val closedSet = HashSet<Node>()

        openSet.add(startNode)
        openMap[startNode] = startNode

        var bestNode = startNode
        var depth = 0

        while (openSet.isNotEmpty() && depth < maxExpandedNodes) {
            val current = openSet.poll()
            if (closedSet.contains(current)) continue
            if (openMap[current] !== current) continue
            openMap.remove(current)

            if (isCloseEnough(current, endNode)) return reconstructPath(current)

            closedSet.add(current)
            depth++

            if (current.h < bestNode.h) bestNode = current

            for (neighbor in getNeighbors(current, instance, entity)) {
                if (closedSet.contains(neighbor)) continue
                val tentativeG = current.g + distance(current, neighbor)
                val existing = openMap[neighbor]
                if (existing == null) {
                    neighbor.parent = current
                    neighbor.g = tentativeG
                    neighbor.h = heuristic(neighbor, endNode)
                    openSet.add(neighbor)
                    openMap[neighbor] = neighbor
                } else if (tentativeG < existing.g) {
                    val updated = Node(existing.x, existing.y, existing.z)
                    updated.parent = current
                    updated.g = tentativeG
                    updated.h = existing.h
                    openSet.add(updated)
                    openMap[updated] = updated
                }
            }
        }

        return if (bestNode !== startNode) reconstructPath(bestNode) else emptyList()
    }

    protected open fun getNeighbors(current: Node, instance: Instance, entity: EntityCreature): List<Node> {
        val neighbors = mutableListOf<Node>()
        val cx = floor(current.x).toInt()
        val cy = floor(current.y).toInt()
        val cz = floor(current.z).toInt()

        for (offset in FLAT_NEIGHBORS) {
            val dx = offset[0];
            val dz = offset[1]
            val nx = cx + dx;
            val nz = cz + dz

            if (dx != 0 && dz != 0) {
                val cardinalX = hasWalkableAt(cx + dx, cy, cz, instance, entity)
                val cardinalZ = hasWalkableAt(cx, cy, cz + dz, instance, entity)
                if (!cardinalX && !cardinalZ) continue
            }

            for (dy in 1 downTo -maxFallDistance) {
                if (isWalkable(nx, cy + dy, nz, instance, entity)) {
                    neighbors.add(Node(nx + 0.5, (cy + dy).toDouble(), nz + 0.5))
                    break
                }
            }
        }
        return neighbors
    }

    protected fun hasWalkableAt(x: Int, cy: Int, z: Int, instance: Instance, entity: EntityCreature): Boolean {
        for (dy in 1 downTo -maxFallDistance) {
            if (isWalkable(x, cy + dy, z, instance, entity)) return true
        }
        return false
    }

    protected fun isWalkable(x: Int, y: Int, z: Int, instance: Instance, entity: EntityCreature): Boolean {
        val key = packPos(x, y, z)
        walkableCache[key]?.let { return it }
        val groundBlock = instance.getBlock(x, y - 1, z)
        val result = groundPosEvaluator?.evaluate(entity, groundBlock, x, y - 1, z) ?: groundBlock.isSolid
        return result.also { walkableCache[key] = it }
    }

    protected fun packPos(x: Int, y: Int, z: Int): Long =
        ((x.toLong() and 0x3FFFFFFL) shl 38) or ((y.toLong() and 0xFFFL) shl 26) or (z.toLong() and 0x3FFFFFFL)

    protected open fun isCloseEnough(a: Node, b: Node): Boolean {
        val dx = a.x - b.x;
        val dz = a.z - b.z
        return dx * dx + dz * dz < 1.0 && abs(a.y - b.y) <= maxFallDistance
    }

    protected open fun heuristic(a: Node, b: Node): Double {
        val dx = abs(a.x - b.x);
        val dz = abs(a.z - b.z)
        return max(dx, dz) + SQRT2_MINUS_1 * min(dx, dz)
    }

    protected fun distance(a: Node, b: Node) = a.vec.distance(b.vec)

    protected fun reconstructPath(end: Node): List<Node> {
        val path = ArrayDeque<Node>()
        var current: Node? = end
        while (current != null) {
            path.addFirst(current); current = current.parent
        }
        if (path.size > 1) path.removeFirst()
        return path.toList()
    }
}