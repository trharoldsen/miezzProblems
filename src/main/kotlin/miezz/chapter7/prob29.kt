package miezz.chapter7

import miezz.utils.ArrayMatrix
import miezz.utils.Index

data class Best(val winner: Winner, val row: Int=0, val column: Int=0)

enum class Player {
	HUMAN {
		override val other
			get() = COMPUTER
		override fun asOccupant() = Occupant.HUMAN
	},
	COMPUTER {
		override val other
			get() = HUMAN
		override fun asOccupant() = Occupant.COMPUTER
	};

	abstract val other: Player
	abstract internal fun asOccupant(): Occupant
}

enum class Occupant {
	HUMAN, COMPUTER, EMPTY
}

enum class Winner {
	ILLEGAL, HUMAN, UNCLEAR, CATS_GAME, COMPUTER
}

class TicTacToe(start: Player, val ai: TicTacToe.() -> Best) {
	private val board = ArrayMatrix(3, 3) { Occupant.EMPTY }
	var turn: Player = start
		private set

	fun chooseMove(): Best = this.ai()

	fun status(): Winner {
		return when {
			isAWin(Player.COMPUTER) -> Winner.COMPUTER
			isAWin(Player.HUMAN) -> Winner.HUMAN
			boardIsFull() -> Winner.CATS_GAME
			else -> Winner.UNCLEAR
		}
	}

	fun playMove(index: Index): Winner? {
		if (board[index] != Occupant.EMPTY)
			throw IllegalMoveException("Square occupied: $index")
		if (status() != Winner.UNCLEAR)
			throw IllegalMoveException("Game complete")
		place(index, turn.asOccupant())
		turn = turn.other
		return status()
	}

	fun place(index: Index, occupant: Occupant) {
		board[index] = occupant
	}

	fun reset(start: Player) {
		val it = board.iterator()
		while (it.hasNext()) {
			it.next()
			it.set(Occupant.EMPTY)
		}
		turn = start
	}

	fun squareIsEmpty(index: Index): Boolean {
		return board[index] === Occupant.EMPTY
	}

	fun boardIsFull(): Boolean {
		return board.none { it == Occupant.EMPTY }
	}

	fun isAWin(side: Player): Boolean {
		val occupant = side.asOccupant()
		// search rows
		for (rowIndex in 0..2) {
			val row = (0..2).map { col -> Index(rowIndex, col) }.toList()
			if (row.all { board[it] == occupant })
				return true
		}

		// search columns
		for (colIndex in 0..2) {
			val col = (0..2).map { row -> Index(row, colIndex) }.toList()
			if (col.all { board[it] == occupant })
				return true
		}

		// search diagonals
		let {
			val indices = (0..2).zip(0..2).map { Index(it.first, it.second) }
			if (indices.all { board[it] == occupant })
				return true
		}
		let {
			val indices = (0..2).zip(2 downTo 0).map { Index(it.first, it.second) }
			if (indices.all { board[it] == occupant })
				return true
		}
		return false
	}
}

class IllegalMoveException(msg: String) : Exception(msg)

fun TicTacToe.defaultMinimaxAI(turn: Player): Best {
	infix fun Winner.isBetterThan(other: Winner): Boolean {
		if (this == Winner.ILLEGAL)
			return false
		if (other == Winner.ILLEGAL)
			return true
		return when (turn) {
			Player.COMPUTER -> this > other
			Player.HUMAN -> this < other
		}
	}

	var best = Best(Winner.ILLEGAL)
	val simpleEval = status()
	if (simpleEval != Winner.UNCLEAR)
		return Best(simpleEval)

	for (row in 0..2) {
		for (col in 0..2) {
			val index = Index(row, col)
			if (squareIsEmpty(index)) {
				place(index, turn.asOccupant())
				val reply = defaultMinimaxAI(turn.other)
				place(index, Occupant.EMPTY)

				if (reply.winner isBetterThan best.winner) {
					best = reply
				}
			}
		}
	}
	assert(best.winner != Winner.ILLEGAL)
	return best
}

fun main(args: Array<String>) {

}