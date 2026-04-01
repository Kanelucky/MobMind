package org.kanelucky.mobmind.core.entity.ai.route

import net.minestom.server.coordinate.Vec

import kotlin.math.floor

/**
 * Represents a node in the pathfinding graph.
 *
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 *
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
class Node(val x: Double, val y: Double, val z: Double) : Comparable<Node> {

    var g: Double = 0.0
    var h: Double = 0.0
    var parent: Node? = null

    val f get() = g + h
    val vec get() = Vec(x, y, z)

    override fun compareTo(other: Node) = f.compareTo(other.f)

    override fun equals(other: Any?) = other is Node &&
            floor(x).toInt() == floor(other.x).toInt() &&
            floor(z).toInt() == floor(other.z).toInt()

    override fun hashCode() = 31 * floor(x).toInt() + floor(z).toInt()
}