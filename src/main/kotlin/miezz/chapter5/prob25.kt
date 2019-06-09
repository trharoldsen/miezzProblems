package miezz.chapter5

import miezz.utils.*
import java.util.*

interface MatrixValueSearch {
	fun find(value: Int, matrix: Matrix<Int>): Boolean
}

class NaiveAny5_25 : MatrixValueSearch {
	override fun find(value: Int, matrix: Matrix<Int>): Boolean =
		matrix.any { it == value }

}

class NaiveIterator5_25 : MatrixValueSearch {
	override fun find(value: Int, matrix: Matrix<Int>): Boolean {
		for (e in matrix) {
			if (value == e)
				return true
		}
		return false
	}
}

class NaiveForLoops5_25 : MatrixValueSearch {
	override fun find(value: Int, matrix: Matrix<Int>): Boolean {
		val size = matrix.dimensions
		for (row in 0..size.height -1) {
			for (col in 0..size.width -1) {
				if (matrix[row, col] == value)
					return true
			}
		}
		return false
	}
}

class LinearSearchIndexed5_25 : MatrixValueSearch {
	override fun find(value: Int, matrix: Matrix<Int>): Boolean {
		val rectangle = matrix.rectangle
		var index = rectangle.topRight + LEFT
		while (index.column > 0 && index.row < rectangle.top) {
			val compared = matrix[index].compareTo(value)
			index = if (compared > 0) {
				index + LEFT
			} else if (compared < 0) {
				index + DOWN
			} else {
				return true
			}
		}
		return false
	}
}

class LinearSearchIndexed5_25v2 : MatrixValueSearch {
	override fun find(value: Int, matrix: Matrix<Int>): Boolean {
		val rectangle = matrix.rectangle
		var index = rectangle.topRight + LEFT
		while (index.column > 0 && index.row < rectangle.top) {
			val compared = matrix[index].compareTo(value)
			val row = index.row
			val col = index.column
			index = if (compared > 0) {
				Index(row, col - 1)
			} else if (compared < 0) {
				Index(row + 1, col)
			} else {
				return true
			}
		}
		return false
	}
}

class LinearSearchCount5_25 : MatrixValueSearch {
	override fun find(value: Int, matrix: Matrix<Int>): Boolean {
		val rectangle = matrix.rectangle
		var (row, column) = rectangle.topRight + LEFT
		while (column > 0 && row < rectangle.top) {
			val compared = matrix[row, column].compareTo(value)
			if (compared > 0) {
				column -= 1
			} else if (compared < 0) {
				row += 1
			} else {
				return true
			}
		}
		return false
	}
}

fun makeProb25Matrix(
	dimensions: Dimensions,
	distribution: Map<Int, Percentage>,
	random: Random
): ArrayMatrix<Int> {
	val matrix = ArrayMatrix(dimensions, { -1 })
	val rectangle = matrix.rectangle
	if (matrix.isEmpty())
		return matrix

	matrix[0, 0] = 0
	matrix.submatrix(Rectangle(0, 1, 1, rectangle.right), true).let { row ->
		val it = row.iterator()
		while (it.hasNext()) {
			val index = it.nextIndex()!!
			it.next()
			it.set(row[index + LEFT] + random.nextItemPercent(distribution))
		}
	}
	matrix.submatrix(Rectangle(1, 0, rectangle.bottom, 1), true).let { col ->
		val it = col.iterator()
		while (it.hasNext()) {
			val index = it.nextIndex()!!
			it.next()
			it.set(col[index + UP] + random.nextItemPercent(distribution))
		}
	}
	val remainingRect = Rectangle(Index(1, 1), rectangle.bottomRight)
	matrix.submatrix(remainingRect, true).let {
		for (row in (0..it.dimensions.height -1)) {
			for (col in (0..it.dimensions.width -1)) {
				val maxAdjacent = maxOf(it[row-1, col], it[row, col-1])
				it[row, col] = maxAdjacent + random.nextItemPercent(distribution)
			}
		}
	}
	return matrix
}
