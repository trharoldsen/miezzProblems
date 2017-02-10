package miezz.benchmarks.chapter5

import miezz.chapter5.JumpForward5_21
import miezz.chapter5.Naive5_21
import miezz.chapter5.RandomizedTestFactory
import org.openjdk.jmh.annotations.*

@State(Scope.Thread)
open class Benchmark5_21Kt {
	@Param("usuallyLow", "usuallyHigh", "passNearEnd", "passNearMid",
		"regressNearEnd", "regressNearMid", "spread")
	lateinit var benchmarkSuite: String
	private val numInputs = 200

	lateinit var inputs: List<IntArray>

	@Setup(Level.Iteration)
	fun setupIteration() {
		inputs = (1..numInputs).map { byName[benchmarkSuite]!!.make(it * 0xdeadL, 100000) }
	}

	@Benchmark
	@Measurement(iterations = 5)
	@Warmup(iterations = 5)
	open fun naiveWhileline(): Int {
		return inputs.count { JumpForward5_21().solve(it) }
	}

	@Benchmark
	@Measurement(iterations = 5)
	@Warmup(iterations = 5)
	open fun baseline(): Int {
		return inputs.count { Naive5_21().solve(it) }
	}

	companion object {
		private val usuallyLow = RandomizedTestFactory(-50, -20, listOf(14, 80, 2, 2, 0, 2))
		private val usuallyHigh = RandomizedTestFactory(20, 50, listOf(14, 80, 2, 2, 0, 2))
		private val passNearEnd = RandomizedTestFactory(-105000, -95000, listOf(10, 35, 20, 20, 10, 5))
		private val passNearMid = RandomizedTestFactory(-55000, -45000, listOf(10, 35, 20, 20, 10, 5))
		private val regressNearEnd = RandomizedTestFactory(45000, 55000, listOf(70, 20, 5, 3, 1, 1))
		private val regressNearMid = RandomizedTestFactory(22500, 27500, listOf(70, 20, 5, 3, 1, 1))
		private val spread = RandomizedTestFactory(-15000, -15000, listOf(70, 5, 0, 0, 5, 20))

		val byName = mapOf(
			Pair("usuallyLow", usuallyLow),
			Pair("usuallyHigh", usuallyHigh),
			Pair("passNearEnd", passNearEnd),
			Pair("passNearMid", passNearMid),
			Pair("regressNearEnd", regressNearEnd),
			Pair("regressNearMid", regressNearMid),
			Pair("spread", spread)
		)
	}
}
