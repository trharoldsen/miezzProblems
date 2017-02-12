package miezz.chapter5

import kotlinx.support.jdk8.streams.asStream
import miezz.utils.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.util.*
import java.util.stream.Stream

interface Prob5_25Tests {
	val searcher: MatrixValueSearch

	@Test fun testValueAtTopLeft() {
		val matrix = matrixOf(
			listOf(0, 1, 2),
			listOf(1, 2, 3),
			listOf(2, 3, 4))
		Assertions.assertTrue { searcher.find(0, matrix) }
	}

	@Test fun testValueAtBottomRight() {
		val matrix = matrixOf(
			listOf(0, 1, 2),
			listOf(1, 2, 3),
			listOf(2, 3, 4))
		Assertions.assertTrue { searcher.find(4, matrix) }
	}

	@Test fun testValueAtTopRight() {
		val matrix = matrixOf(
			listOf(0, 1, 2),
			listOf(1, 3, 3),
			listOf(4, 5, 6))
		Assertions.assertTrue { searcher.find(2, matrix) }
	}

	@Test fun testValueAtBottomLeft() {
		val matrix = matrixOf(
			listOf(0, 1, 2),
			listOf(1, 2, 3),
			listOf(4, 5, 6))
		Assertions.assertTrue { searcher.find(4, matrix) }
	}

	@Test fun testValueMissingTooLow() {
		val matrix = matrixOf(
			listOf(0, 1, 2),
			listOf(1, 2, 3),
			listOf(4, 5, 6))
		Assertions.assertFalse { searcher.find(-1, matrix) }
	}

	@Test fun testValueMissingTooHigh() {
		val matrix = matrixOf(
			listOf(0, 1, 2),
			listOf(1, 2, 3),
			listOf(4, 5, 7))
		Assertions.assertFalse { searcher.find(-1, matrix) }
	}

	@Test fun testValueMissingJumpedOver() {
		val matrix = matrixOf(
			listOf(0, 1, 2),
			listOf(4, 4, 4),
			listOf(4, 5, 7))
		Assertions.assertFalse { searcher.find(3, matrix) }
	}

	@Test fun testValueMissingJumpedUnder() {
		val matrix = matrixOf(
			listOf(0, 1, 2),
			listOf(1, 2, 5),
			listOf(3, 3, 7))
		Assertions.assertFalse { searcher.find(4, matrix) }
	}

	@TestFactory fun testRandomExists(): Stream<DynamicTest> {
		val numMatrices = 500
		val numSearches = 10
		val distribution = mapOf(0 to 20, 2 to 30, 4 to 30, 6 to 10, 8 to 10)
		val nextSize = fun Random.(): Size {
			return Size(nextPoisson(2.5) * 4 + nextInt(4),
				nextPoisson(2.5) * 4 + nextInt(4))
		}

		return (1..numMatrices).asSequence()
			.map { Random(it.toLong()) }
			.map(::Problem5_25Builder)
			.onEach { it.size = it.random.nextSize() }
			.onEach { it.makeMatrix(distribution) }
			.flatMap { p -> (1..numSearches).asSequence().map { p } }
			.onEach {
				it.value = with(it.matrix!!) {
					this[it.random.nextIndex(this)]
				}
			}.map { DynamicTest.dynamicTest("RandomExists (${it.size!!})") {
				assertTrue(searcher.find(it.value!!, it.matrix!!))
			} }.asStream()
	}

	@TestFactory fun testRandomMissing(): Stream<DynamicTest> {
		val numMatrices = 500
		val numSearches = 10
		val distribution = mapOf(0 to 20, 2 to 30, 4 to 30, 6 to 10, 8 to 10)
		val nextSize = fun Random.(): Size {
			return Size(nextPoisson(2.5) * 4 + nextInt(4),
				nextPoisson(2.5) * 4 + nextInt(4))
		}

		return (1..numMatrices).asSequence()
			.map { Random(it.toLong()) }
			.map(::Problem5_25Builder)
			.onEach { it.size = it.random.nextSize() }
			.onEach { it.makeMatrix(distribution) }
			.flatMap { p -> (1..numSearches).asSequence().map { p } }
			.onEach {
				it.value = with(it.matrix!!) {
					this[it.random.nextIndex(this)] + 1
				}
			}.map { DynamicTest.dynamicTest("RandomExists (${it.size!!})") {
				assertFalse(searcher.find(it.value!!, it.matrix!!))
			} }.asStream()
	}

	@TestFactory fun testRandom(): Stream<DynamicTest> {
		val gold = NaiveAny5_25()
		val numMatrices = 500
		val numSearches = 10
		val distribution = mapOf(0 to 20, 1 to 25, 2 to 30, 3 to 15, 4 to 10)
		val nextSize = fun Random.(): Size {
			return Size(nextPoisson(2.5) * 4 + nextInt(4),
				nextPoisson(2.5) * 4 + nextInt(4))
		}

		return (1..numMatrices).asSequence()
			.map { Random(it.toLong()) }
			.map(::Problem5_25Builder)
			.onEach { it.size = it.random.nextSize() }
			.onEach { it.makeMatrix(distribution) }
			.flatMap { p -> (1..numSearches).asSequence().map { p } }
			.onEach { it.value = it.random.nextInt(it.maxValue!! + 1) }
			.map { DynamicTest.dynamicTest("RandomExists (${it.size!!})") {
				val expected = gold.find(it.value!!, it.matrix!!)
				val actual = searcher.find(it.value!!, it.matrix!!)
				assertEquals(expected, actual)
			} }.asStream()
	}
}

private class Problem5_25Builder(val random: Random) {
	var size: Size? = null
	var matrix: Matrix<Int>? = null
	var value: Int? = null

	fun makeMatrix(distribution: Map<Int, Int>) {
		matrix = makeProb25Matrix(size!!, distribution, random)
	}

	val maxValue: Int?
		get() {
			val matrix = this.matrix
			if (matrix != null) {
				val br = matrix.rectangle.bottomRight + UP + LEFT
				return matrix[br]
			}
			return null
		}
}

private fun <T> Random.nextIndex(matrix: Matrix<T>): Index {
	val row = nextInt(matrix.size.rows)
	val col = nextInt(matrix.size.columns)
	return Index(row, col)
}

class NaiveAny5_25Test : Prob5_25Tests {
	override val searcher: MatrixValueSearch
		get() = NaiveAny5_25()
}

class NaiveIterator5_25Test : Prob5_25Tests {
	override val searcher: MatrixValueSearch
		get() = NaiveIterator5_25()
}

class LinearSearchIndexed5_25Test : Prob5_25Tests {
	override val searcher: MatrixValueSearch
		get() = LinearSearchIndexed5_25()
}

class LinearSearchCount5_25Test : Prob5_25Tests {
	override val searcher: MatrixValueSearch
		get() = NaiveAny5_25()
}
