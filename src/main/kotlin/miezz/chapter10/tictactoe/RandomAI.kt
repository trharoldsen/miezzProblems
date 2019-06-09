package miezz.chapter10.tictactoe

import miezz.utils.Index
import java.util.*

class RandomAIFactory : AIFactory {
	override fun make(game: GameState, player: Player, random: Random): AI {
		return RandomAI(game, random)
	}

	private class RandomAI(
		val game: GameState,
		val random: Random
	): AI {
		override fun chooseMove(): Index {
			val unoccupiedSquares = game.board.withIndex()
				.filter { it.value == Occupant.UNOCCUPIED }
				.map { it.index }
			val randInt = random.nextInt(unoccupiedSquares.size)
			return unoccupiedSquares[randInt]
		}
	}
}
