package miezz.chapter10.tictactoe

import miezz.utils.Index
import miezz.utils.ObjectRef
import java.util.*

class MinimaxAIFactory : AIFactory {
	override fun make(game: GameState, player: Player, random: Random): AI {
		return MinimaxAI(game, player)
	}

	private data class Result(
		val result: MatchResult,
		val move: Index,
		val depth: Int
	)

	private class MinimaxAI(
		val game: GameState,
		val player: Player
	): AI {
		val history = HashMap<Map<Index, Occupant>, Result>()

		override fun chooseMove(): Index {
			val best = ObjectRef<Result?>(null)
			for (square in game.board.unoccupiedSquares) {
				playOutMove(square, player.other, best)
			}
			if (best.value == null)
				throw IllegalStateException("Gameboard is full")
			return best.value!!.move
		}

		private fun playOutMove(square: Square, turn: Player, worst: ObjectRef<Result?>) {
			infix fun Result.isWorseThan(other: Result?): Boolean {
				if (other == null)
					return true
				val comp = result.compareTo(other.result, turn)
				return when {
					comp < 0 -> true
					comp > 0 -> false
					comp == 0 -> depth < other.depth
					else -> throw AssertionError()
				}
			}

//			game.playMove(square)
//
//			val moveResult = game.status()
//			if (moveResult == MatchResult.UNDECIDED) {
//				val best = ObjectRef<Result?>(null)
//				for (next in game.board.unoccupiedSquares) {
//					playOutMove(next, turn.other, best)
//					if (best.value!! isWorseThan worst.value) {
//						worst.value = best.value
//					} else {
//						break
//					}
//				}
//			} else {
//				val result = Result(moveResult, square, 1)
//				if (result isWorseThan worst.value)
//					worst.value = result
//			}
//
//			game.undo()
		}
	}

	companion object {
		fun GameBoard.copyState(): Map<Index, Occupant> {
			return withIndex()
				.filter { it.value != Occupant.UNOCCUPIED }
				.associate { it.index to it.value }
		}
	}
}