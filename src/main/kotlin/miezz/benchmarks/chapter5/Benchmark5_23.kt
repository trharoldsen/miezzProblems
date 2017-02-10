package miezz.benchmarks.chapter5

import org.openjdk.jmh.annotations.*
import miezz.chapter5.*
import miezz.utils.doublesSeq
import java.util.*
import java.util.concurrent.TimeUnit


@State(Scope.Thread)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(1)
open class Problem5_23Benchmarks {
	@Param("linear", "parabolic", "sinusoidal", "tangent")
	lateinit var benchmarkSuite: String

	lateinit var random: Random
	lateinit var benchmarkGen: Iterator<Equation>
	lateinit var input: Pair<(Double) -> Double, LowHighPair>

	@Setup(Level.Iteration)
	fun setupIteration() {
		random = Random(0xDEAD_BEEF_FED_DAD)
		benchmarkGen = byName[benchmarkSuite]!!(random)
	}

	@Setup(Level.Invocation)
	fun setupInvocation() {
		val benchmark = benchmarkGen.next()
		input = Pair(benchmark.make(), benchmark.lowHighPair(random))
	}

	@Benchmark
	@Measurement(iterations = 10, batchSize = 50000)
	@Warmup(iterations = 5, batchSize = 40000)
	fun binarySearchSolver(): Double {
		val f = input.first
		val low = input.second.low
		val high = input.second.high
		return BinarySearchZeroSolver(0.000001).findZero(f, low, high)
	}

	@Benchmark
	@Measurement(iterations = 10, batchSize = 50000)
	@Warmup(iterations = 5, batchSize = 40000)
	fun linearEquationSolve(): Double {
		val f = input.first
		val low = input.second.low
		val high = input.second.high
		return LinearInterpolationZeroSolver(0.000001).findZero(f, low, high)
	}

	companion object {
		private val linear = {random: Random ->
			random.doublesSeq()
				.map { it -> (it - 0.5) * 3 }
				.zip(random.doublesSeq().map { (it - 0.5) * 100 })
				.map { (a, b) -> LinearEquation(a, b) }
				.iterator()
		}
		private val parabolic = {random: Random ->
			random.doublesSeq()
				.map { it -> (it - 0.5) * 100 }
				.zip(random.doublesSeq().map { (it - 0.5) * 100 })
				.map { ParabolicEquation(it.first, it.second) }
				.iterator()
		}
		private val sinusoidal = {random: Random ->
			random.doublesSeq()
				.map { it -> (it - 0.5) * 10 }
				.zip(random.doublesSeq().map { (it - 0.5) })
				.zip(random.doublesSeq().map { it * 5}) {
					t, r -> Triple(t.first, t.second, r)
				}.map { SinusoidalEquation(it.first, it.second, it.third) }
				.iterator()
			}
		private val tangent = {random: Random ->
			random.doublesSeq()
				.map { it -> (it - 0.5) * 10 }
				.zip(random.doublesSeq().map { (it - 0.5) })
				.zip(random.doublesSeq().map { it * 5}) {
					t, r -> Triple(t.first, t.second, r)
				}.map { TangentEquation(it.first, it.second, it.third) }
				.iterator()
		}

		val byName = mapOf(
			Pair("linear", linear),
			Pair("parabolic", parabolic),
			Pair("sinusoidal", sinusoidal),
			Pair("tangent", tangent)
		)
	}
}
