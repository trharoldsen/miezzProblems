package miezz.chapter10.tictactoe

import miezz.utils.ArrayMatrix
import miezz.utils.Dimensions
import miezz.utils.Index
import miezz.utils.Matrix

enum class Player {
	X {
		override val other
			get() = O
		override fun asOccupant() = Occupant.X
	},
	O {
		override val other
			get() = X
		override fun asOccupant() = Occupant.O
	};

	abstract val other: Player
	abstract internal fun asOccupant(): Occupant
}

enum class Occupant {
	X, O, UNOCCUPIED
}

enum class MatchResult {
	X_WINS, CATS_GAME, O_WINS, UNDECIDED;

	fun compareTo(other: MatchResult, turn: Player): Int {
		return when {
			this == other -> 0
			this == UNDECIDED -> -1
			other == UNDECIDED -> 1
			else -> {
				val cmp = value() - other.value()
				if (turn == Player.X) cmp else -cmp
			}
		}
	}

	private fun value(): Int {
		return when (this) {
			MatchResult.X_WINS -> 2
			MatchResult.CATS_GAME -> 1
			MatchResult.O_WINS -> 0
			MatchResult.UNDECIDED -> throw AssertionError()
		}
	}
}

typealias GameBoard = Matrix<Occupant>
typealias Square = Index
val GameBoard.unoccupiedSquares: Iterable<Square>
	get() = indices().filter { this[it] == Occupant.UNOCCUPIED }

interface GameState {
	val board: GameBoard
	val winCondition: Int
	val turn: Player
	val status: MatchResult
}

class MutableGameState(
	boardDimensions: Dimensions,
	override var winCondition: Int,
	override var turn: Player
) : GameState {
	private var _board = ArrayMatrix(boardDimensions) { Occupant.UNOCCUPIED }
	override val board: GameBoard get() = _board

	// upon setting dimensions, board will be cleared
	var boardDimensions: Dimensions
		get() = board.dimensions
		set(dimensions) {
			_board = ArrayMatrix(boardDimensions) { Occupant.UNOCCUPIED }
		}

	fun place(square: Square, occupant: Occupant) {
		_board[square] = occupant
	}

	fun unplace(square: Square) {
		_board[square] = Occupant.UNOCCUPIED
	}

	fun boardIsFull(): Boolean {
		return _board.none { it == Occupant.UNOCCUPIED }
	}

	override val status: MatchResult
		get() = when {
			isAWin(Player.O) -> MatchResult.O_WINS
			isAWin(Player.X) -> MatchResult.X_WINS
			boardIsFull() -> MatchResult.CATS_GAME
			else -> MatchResult.UNDECIDED
		}

	private fun isAWin(side: Player): Boolean {
		val occupant = side.asOccupant()
		fun check(startIndex: Index, progress: Index.() -> Index): Boolean {
			val rectangle = board.rectangle

			var count = 0
			var index = startIndex
			while (count < winCondition && index in rectangle && board[index] == occupant) {
				count += 1
				index = index.progress()
			}
			return count == winCondition
		}

		val occupiedSquares = board.indices()
			.filter { board[it] == occupant }

		for (index in occupiedSquares) {
			if (check(index) { Index(row + 1, column) }) return true
			if (check(index) { Index(row, column + 1) }) return true
			if (check(index) { Index(row + 1, column - 1) }) return true
			if (check(index) { Index(row + 1, column + 1) }) return true
		}

		return false
	}
}

class IllegalMoveException(msg: String) : Exception(msg)

