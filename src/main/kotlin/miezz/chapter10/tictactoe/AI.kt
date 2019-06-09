package miezz.chapter10.tictactoe

import miezz.utils.Index
import java.util.*

interface AI {
	fun chooseMove(): Index
}

interface AIFactory {
	fun make(game: GameState, player: Player, random: Random = Random()): AI
}

