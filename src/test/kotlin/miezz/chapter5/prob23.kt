package miezz.chapter5

import org.junit.jupiter.api.Assertions.assertTimeoutPreemptively
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import miezz.utils.doublesSeq
import java.time.Duration.ofMillis
import java.util.*
import kotlin.properties.Delegates


class BinarySearchSolverTests : Prob5_23Test() {
	override fun makeSolver(error: Double) = BinarySearchZeroSolver(error)
}

class LinearInterpolationSolverTests : Prob5_23Test() {
	override fun makeSolver(error: Double) = LinearInterpolationZeroSolver(error)
}

abstract class Prob5_23Test {
	@TestFactory
	fun linearTest(): List<DynamicTest> {
		val random = Random(0xBAD_DAD_FAD)
		val solver = makeSolver(0.001)
		return random.doublesSeq()
			.map { it -> (it - 0.5) * 3 }
			.zip(random.doublesSeq().map { (it - 0.5) * 10 })
			.map { (a, b) -> LinearEquation(a, b) }
			.map { testEquation(solver, it, it.lowHighPair(random)) }
			.take(40).toList()
	}

	@TestFactory
	fun parabolicTest(): List<DynamicTest> {
		val random = Random(0xBAD_DAD_FAD)
		val solver = makeSolver(0.001)
		return random.doublesSeq()
			.map { it -> (it - 0.5) * 10 }
			.zip(random.doublesSeq().map { (it - 0.5) * 10 })
			.map { ParabolicEquation(it.first, it.second) }
			.map { testEquation(solver, it, it.lowHighPair(random)) }
			.take(40).toList()
	}

	@TestFactory
	fun sinusoidalTest(): List<DynamicTest> {
		val random = Random(0xBAD_DAD_FAD)
		val solver = makeSolver(0.001)
		return random.doublesSeq()
			.map { it -> (it - 0.5) * 10 }
			.zip(random.doublesSeq().map { (it - 0.5) * 4 })
			.zip(random.doublesSeq().map { it * 5}) {
				t, r -> Triple(t.first, t.second, r)
			}.map { SinusoidalEquation(it.first, it.second, it.third) }
			.map { testEquation(solver, it, it.lowHighPair(random)) }
			.take(40).toList()
	}

	@TestFactory
	fun tangentTest(): List<DynamicTest> {
		val random = Random(0xBAD_DAD_FAD)
		val solver = makeSolver(0.001)
		return random.doublesSeq()
			.map { it -> (it - 0.5) * 10 }
			.zip(random.doublesSeq().map { (it - 0.5) * 4 })
			.zip(random.doublesSeq().map { it * 5}) {
				t, r -> Triple(t.first, t.second, r)
			}.map { TangentEquation(it.first, it.second, it.third) }
			.map { testEquation(solver, it, it.lowHighPair(random)) }
			.take(40).toList()
	}

	private fun testEquation(
		solver: ZeroSolver,
		equation: Equation,
		lowHigh: LowHighPair
	): DynamicTest {
		val low = lowHigh.low.format(3)
		val high = lowHigh.high.format(3)
		return dynamicTest("$equation: [$low : $high]") {
			val f = equation.make()
			var computed by Delegates.notNull<Double>()
			assertTimeoutPreemptively(ofMillis(200)) {
				computed = solver.findZero(f, lowHigh.low, lowHigh.high)
			}
			val f_computed = f(computed)
			assertTrue { f_computed <= 0.001 }
		}
	}

	abstract fun makeSolver(error: Double): ZeroSolver
}
