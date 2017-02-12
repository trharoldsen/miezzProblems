package miezz.benchmarks.chapter5

import miezz.chapter5.*
import miezz.utils.*
import org.openjdk.jmh.annotations.*
import java.util.*

@State(Scope.Thread)
open class Benchmarks5_25Kt {
	private val numInputs = 1000
	private val numSearches = 10

	private lateinit var inputs: List<Problem5_25Builder>

	@Setup(Level.Trial)
	fun setupIteration() {
		val distribution = mapOf(0 to 20, 1 to 20, 2 to 20, 3 to 15,
			4 to 10, 6 to 5, 7 to 5, 8 to 5)
		val nextSize = fun Random.(i: Int): Size {
			return Size(nextPoisson(2 * i.toDouble()), nextPoisson(2 * i.toDouble()))
		}

		inputs = generateSequence(1) { it + 1}
			.map { Random(it.toLong()) }
			.map(::Problem5_25Builder)
			.withIndex()
			.onEach { it.value.size = it.value.random.nextSize(it.index) }
			.onEach { it.value.makeMatrix(distribution) }
			.filter { !it.value.matrix!!.isEmpty() }
			.flatMap { p -> (1..numSearches).asSequence().map { p.value } }
			.onEach { it.value = it.random.nextInt(it.maxValue!! + 2) - 1 }
			.take(numInputs)
			.toList()
	}

//	@Benchmark
//	@Measurement(iterations = 5)
//	@Warmup(iterations = 3)
//	open fun naiveAny(): Int {
//		return inputs.count { NaiveAny5_25().find(it.value!!, it.matrix!!) }
//	}
//
//	@Benchmark
//	@Measurement(iterations = 5)
//	@Warmup(iterations = 3)
//	open fun naiveIterator(): Int {
//		return inputs.count { NaiveIterator5_25().find(it.value!!, it.matrix!!) }
//	}
//
//	@Benchmark
//	@Measurement(iterations = 5)
//	@Warmup(iterations = 3)
//	open fun naiveForLoops(): Int {
//		return inputs.count { NaiveForLoops5_25().find(it.value!!, it.matrix!!) }
//	}
//
	@Benchmark
	@Measurement(iterations = 10)
	@Warmup(iterations = 5)
	open fun linearIndexed(): Int {
		return inputs.count { LinearSearchIndexed5_25().find(it.value!!, it.matrix!!) }
	}

	@Benchmark
	@Measurement(iterations = 10)
	@Warmup(iterations = 5)
	open fun linearIndexedv2(): Int {
		return inputs.count { LinearSearchIndexed5_25v2().find(it.value!!, it.matrix!!) }
	}

	@Benchmark
	@Measurement(iterations = 10)
	@Warmup(iterations = 5)
	open fun linearCount(): Int {
		return inputs.count { LinearSearchCount5_25().find(it.value!!, it.matrix!!) }
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
			if (matrix != null && !matrix.isEmpty()) {
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
