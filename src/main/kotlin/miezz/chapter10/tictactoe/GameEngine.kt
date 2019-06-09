package miezz.chapter10.tictactoe

import miezz.utils.Dimensions
import miezz.chapter10.tictactoe.GameBoard
import miezz.utils.ArrayMatrix
import miezz.utils.MutableMatrix
import java.util.*

enum class Controller {
	HUMAN, AI
}

internal sealed class PlayerType(val controller: Controller) {
	class Human : PlayerType(Controller.HUMAN)
	class AI(val factory: AIFactory) : PlayerType(Controller.AI)

	companion object {
		private val HUMAN = Human()

		operator fun get(
			controller: Controller,
			aiFactory: AIFactory?
		): PlayerType {
			val factory = requireNotNull(aiFactory)
			return when (controller) {
				Controller.HUMAN -> HUMAN
				Controller.AI -> AI(factory)
			}
		}
	}
}

val availableAIs = mapOf(
	"RANDOM" to RandomAIFactory(),
	"MINIMAX" to MinimaxAIFactory()
)

class GameEngine(val ui: UserInterface) {
	var gameState: MutableGameState? = null
	var players = mutableMapOf(
		Player.X to Controller.HUMAN,
		Player.O to Controller.HUMAN
	)
	var boardDimensions: Dimensions? = null
	var winCondition: Int? = null
	private val moveStack = ArrayDeque<Move>()

	fun setPlayer(player: Player, controller: Controller, ai: AIFactory? = null): Boolean {
		TODO()
	}

	fun setBoardDimensions(boardDimensions: Dimensions): Boolean {
		if (gameState != null)
			return false
		this.boardDimensions = boardDimensions
//		ui.drawGameBoard(boardDimensions)
		return true
	}

	fun setWinCondition(condition: Int): Boolean {
		TODO()
	}

	fun newGame(boardDimensions: Dimensions, winCondition: Int) {
		val state = MutableGameState(boardDimensions, winCondition, Player.X)
		gameState = state
//		ui.onNewGame(state)
	}

	fun clearGame() {
		gameState = null
//		ui.onClearGame()
	}

	fun makeMove(row: Int, column: Int): Boolean {
		val gameState = checkNotNull(gameState) { "No game initialized" }
		val square = Square(row, column)

		// check if it is a players turn
		var turn = gameState.turn
		val controller = players[turn]!!
		if (controller != Controller.HUMAN)
			return false

		// check if valid move
		if (gameState.board[square] != Occupant.UNOCCUPIED)
			return false

		if (gameState.board[square] != Occupant.UNOCCUPIED)
			throw IllegalMoveException("Square occupied: $square")

		gameState.place(square, turn.asOccupant())
//		ui.onMove(square, turn)

		turn = turn.other
//		ui.updateTurn(turn)

//		moveStack.push(square)

		val status = gameState.status
		when (status) {
//			MatchResult.X_WINS -> ui.onWinner(Player.X)
//			MatchResult.O_WINS -> ui.onWinner(Player.O)
//			MatchResult.CATS_GAME -> ui.onCatsGame()
		}

		return true
	}

	fun undo(): Boolean {
		val gameState = checkNotNull(gameState) { "No game initialized" }
		// if empty, don't undo anything
//		if (moveStack.isEmpty())
//			return null
		val lastMove = moveStack.pop()
//		revertTurn()
//		gameState.unplace(lastMove)

//		ui.onUndo(lastMove)

		return true
	}

	private enum class State {
		START, IN_GAME
	}
}

private interface Move {
	fun undo(gameState: MutableGameState)
}

private class MultiTurnMove() : Move {
	private val moves = ArrayDeque<Move>()

	fun addMove(move: Move) {
		moves.push(move)
	}

	override fun undo(gameState: MutableGameState) {
		while (moves.isNotEmpty()) {
			val move = moves.pop()
			move.undo(gameState)
		}
	}
}

private class BasicMove(val originalTurn: Player, val square: Square) : Move {
	override fun undo(gameState: MutableGameState) {
		gameState.unplace(square)
		gameState.turn = originalTurn
	}
}

