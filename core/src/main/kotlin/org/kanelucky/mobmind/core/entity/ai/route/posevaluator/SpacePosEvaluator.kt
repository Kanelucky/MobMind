package org.kanelucky.mobmind.core.entity.ai.route.posevaluator

import net.minestom.server.coordinate.Point
import net.minestom.server.entity.EntityCreature

/**
 * Evaluates 3D positions during pathfinding for flying/swimming entities.
 *
 * Originally developed in AllayMC (https://github.com/AllayMC/Allay)
 * Ported and adapted to this project by Kanelucky
 *
 * Original author: daoge_cmd (AllayMC)
 * Port author: Kanelucky
 */
fun interface SpacePosEvaluator {
    fun evaluate(entity: EntityCreature, pos: Point): Boolean
}