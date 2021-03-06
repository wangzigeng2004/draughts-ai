package com.tsovedenski.ai.draughts.game

import com.tsovedenski.ai.draughts.game.elements.Move
import com.tsovedenski.ai.draughts.game.state.State
import com.tsovedenski.ai.draughts.players.Player
import org.slf4j.LoggerFactory

/**
 * Created by Tsvetan Ovedenski on 30/04/2017.
 */
class Draughts(val size: Int = 8, val pieces: Int = 12, val forcedCapture: Boolean = true, val maxMoves: Int = 256) : Game {

    var listener: Game.ActionListener? = null

    init {
        if (pieces % (size / 2) != 0) {
            throw IllegalArgumentException("Board with size $size cannot have $pieces pieces")
        }
    }

    override fun play(vararg players: Player) {
        if (players.size != 2) {
            throw IllegalArgumentException("Expected 2 players (got ${players.size})")
        } else if (players[0].color == players[1].color) {
            throw IllegalArgumentException("Players cannot have the same color")
        }

        listener?.beforeStart()

        val moves = mutableListOf<Move>()
        var state = State(size, pieces, forcedCapture)
        var winner: Player? = null

        let gameloop@ {
            while (true) {
                players.forEach { player ->
                    if (moves.size >= maxMoves) {
                        log.warn("Moves about the limit ($maxMoves). It's a tie.")
                        return@gameloop
                    }

                    listener?.beforeMove(state, player)

                    if (state.isStalemate(player.color)) {
                        log.warn("$player does not have possible moves. Winner is the opponent!")
                        winner = players.find { it != player }
                        return@gameloop
                    }

                    val move = player.move(state)

                    if (move == null) {
                        log.warn("$player chose to give up. Winner is the opponent!")
                        winner = players.find { it != player }
                        return@gameloop
                    }

                    moves.add(move)
                    state = state.apply(move)

                    listener?.afterMove(state, player, move)

                    state.winner(player.color)?.let { color ->
                        log.warn("$player's opponent is left without pieces. Winner is $player!")
                        winner = players.find { it.color == color }
                    }

                    if (winner != null) {
                        return@gameloop
                    }
                }
            }
        }

        log.warn("${moves.size} move(s):\n${moves.map { "Move(${it.from.row},${it.from.col},${it.to.row},${it.to.col})," }.joinToString("\n")}")
        listener?.afterFinish(state, winner)
    }

    companion object {
        private val log = LoggerFactory.getLogger(Draughts::class.java)
    }
}