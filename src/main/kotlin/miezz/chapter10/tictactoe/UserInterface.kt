package miezz.chapter10.tictactoe

import miezz.utils.Dimensions

interface UserInterface {
	fun updateBoard(gameState: GameState)
	fun updateSquare(gameState: GameState, square: Square, occupant: Occupant)
	fun onWinner(gameState: GameState, winner: Player, squares: List<Square>)
	fun onCatsGame(gameState: GameState)
}
