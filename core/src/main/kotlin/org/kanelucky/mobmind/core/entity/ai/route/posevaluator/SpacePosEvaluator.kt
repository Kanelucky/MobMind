package org.kanelucky.mobmind.core.entity.ai.route.posevaluator

import net.minestom.server.coordinate.Point
import net.minestom.server.entity.EntityCreature

/**
 * Evaluates 3D positions during pathfinding for flying/swimming entities.
 *
 * @author Kanelucky || Allay (https://github.com/AllayMC/Allay)
 */
fun interface SpacePosEvaluator {
    fun evaluate(entity: EntityCreature, pos: Point): Boolean
}