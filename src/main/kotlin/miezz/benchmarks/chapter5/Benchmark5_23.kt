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
open class Benchmarks5_23Kt {
	private val INPUT_SIZE = 400

	@Param("linear", "parabolic", "sinusoidal", "tangent")
	lateinit var benchmarkSuite: String

	lateinit var inputs: List<Pair<(Double) -> Double, LowHighPair>>

	@Setup(Level.Iteration)
	fun setupIteration() {
		val random = Random(0xDEAD_BEEF_FED_DAD)
		val builder = byName[benchmarkSuite]!!(random)
		inputs = builder.asSequence()
			.map { Pair(it.make(), it.lowHighPair(random)) }
			.take(INPUT_SIZE)
			.toList()
	}

	@Benchmark
	@Measurement(iterations = 10)
	@Warmup(iterations = 5)
	fun binarySearchSolver(): Double {
		return inputs.map {
			val f = it.first
			val low = it.second.low
			val high = it.second.high
			BinarySearchZeroSolver(0.00000001).findZero(f, low, high)
		}.average()
	}

	@Benchmark
	@Measurement(iterations = 10)
	@Warmup(iterations = 5)
	fun linearEquationSolve(): Double {
		return inputs.map {
			val f = it.first
			val low = it.second.low
			val high = it.second.high
			LinearInterpolationZeroSolver(0.00000001).findZero(f, low, high)
		}.average()
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
