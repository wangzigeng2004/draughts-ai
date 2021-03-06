package com.tsovedenski.ai.draughts.game.evaluators

import com.tsovedenski.ai.draughts.game.elements.Color
import com.tsovedenski.ai.draughts.game.elements.Point
import com.tsovedenski.ai.draughts.game.state.State

/**
 * Created by Tsvetan Ovedenski on 09/05/17.
 *
 * Each cell has some weight (inner have lower number than outer).
 * Inspired from http://tim.hibal.org/blog/playing-checkers-with-minimax-continued/
 */
class WeightedMatrixEvaluator(val factor: Int) : Evaluator {

    companion object {
        private val dict = mutableMapOf<Int, Map<Point, Int>>()

        private fun generate(size: Int) {
            val map = mutableMapOf<Point, Int>()
            (0..size-1).forEach { row ->
                (0..size-1).forEach { col ->
                    val point = Point(row, col)

                    map.put(point, weight(size, point))
                }
            }
            dict.put(size, map)
        }

        private fun weight(size: Int, point: Point): Int {
            val s = size - 1
            val h = size / 2

            for (i in 0..h - 1) {
                if (point.row == s-i || point.row - i == 0 || point.col == s-i || point.col - i == 0) {
                    return h - i
                }
            }

            return 0
        }
    }

    override fun evaluate(state: State, color: Color): Int {
        if (!dict.containsKey(state.size)) {
            generate(state.size)
        }

        val map = dict[state.size] ?: return 0

        return state.points(color).map { map[it]!! * factor }.sum()
    }
}