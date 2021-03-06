package com.tsovedenski.ai.draughts.game.evaluators

import com.tsovedenski.ai.draughts.game.elements.Color
import com.tsovedenski.ai.draughts.game.state.State

/**
 * Created by Tsvetan Ovedenski on 15/05/17.
 *
 * Naive distance of each piece until becoming a king.
 * TODO: Should take into account opponent's pieces on the path.
 */
class PathToKingEvaluator(val weight: Int) : Evaluator {

    companion object {
        private fun Double.ceil() = Math.ceil(this).toInt()
    }

    override fun evaluate(state: State, color: Color): Int {
        val goalRow = when (color) {
            Color.Black -> state.size - 1 // black are king at row = size-1
            else -> 0 // white are king at row = 0
        }

        return - state.points(color)
                .filter { !state[it]!!.piece!!.king }
                .filter { state.moves(it).isNotEmpty() }
                .map { it.row }
                .map { Math.abs(goalRow - it) }
                .average().ceil() * weight
    }
}