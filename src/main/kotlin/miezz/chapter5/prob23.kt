package miezz.chapter5

import java.util.*

interface ZeroSolver {
	fun findZero(f: (Double) -> Double, low: Double, high: Double): Double
}

class BinarySearchZeroSolver(val error: Double): ZeroSolver {
	override fun findZero(f: (Double) -> Double, low: Double, high: Double): Double {
		@Suppress("NAME_SHADOWING") var low = low
		@Suppress("NAME_SHADOWING") var high = high

		val increasing = f(low) <= 0

		while(true) {
			val pivot = (low + high) / 2

			val result = f(pivot)
			if (Math.abs(result) < error)
				return pivot
			if (result < 0) {
				if (increasing)
					low = pivot
				else
					high = pivot
			} else {
				if (increasing)
					high = pivot
				else
					low = pivot
			}
		}
	}
}

class LinearInterpolationZeroSolver(val error: Double): ZeroSolver {
	override fun findZero(f: (Double) -> Double, low: Double, high: Double): Double {
		@Suppress("NAME_SHADOWING") var low = low
		@Suppress("NAME_SHADOWING") var high = high

		var lowResult = f(low)
		var highResult = f(high)
		val increasing = lowResult <= 0

		while(true) {
			val xdistance = high - low
			val ydistance = highResult - lowResult
			val pivot = low - lowResult * (xdistance / ydistance)

			val result = f(pivot)
			if (Math.abs(result) < error)
				return pivot
			if (result < 0) {
				if (increasing)
					low = pivot
				else
					high = pivot
			} else {
				if (increasing)
					high = pivot
				else
					low = pivot
			}
		}
	}

}

data class LowHighPair(val low: Double, val high: Double)

interface Equation {
	val zero: Double
	fun make(): (Double) -> Double
	fun lowHighPair(random: Random): LowHighPair
}

data class LinearEquation(val a: Double, val b: Double): Equation {
	override val zero = b
	override fun make() = {x: Double -> a * (x - b) }
	override fun toString() = "${a.format(3)} * (x - ${b.format(3)})"
	override fun lowHighPair(random: Random): LowHighPair {
		val low = zero - random.nextDouble() * 100
		val high = zero + random.nextDouble() * 100
		return LowHighPair(low, high)
	}
}

data class ParabolicEquation(val zero1: Double, val zero2: Double): Equation {
	override val zero = zero1
	override fun make() = {x: Double -> (x - zero1) * (x - zero2) }
	override fun toString() = "(x - ${zero1.format(3)}) * (x - ${zero2.format(3)})"
	override fun lowHighPair(random: Random): LowHighPair {
		if (zero1 < zero2) {
			val low = zero1 - random.nextDouble() * 100
			val high = random.doubleBetween(zero1, zero2)
			return LowHighPair(low, high)
		} else {
			val high = zero1 + random.nextDouble() * 100
			val low = random.doubleBetween(zero2, zero1)
			return LowHighPair(low, high)
		}
	}
}

data class SinusoidalEquation(
	val shift: Double, val squish: Double, val height: Double
) : Equation {
	override val zero: Double
		get() = shift

	override fun make() = {x:Double -> Math.sin(squish * (x - shift)) * height}

	override fun lowHighPair(random: Random): LowHighPair {
		val squishedPI = Math.abs(Math.PI / squish)
		val low = random.doubleBetween(shift - squishedPI, shift)
		val high = random.doubleBetween(shift, shift + squishedPI)
		return LowHighPair(low, high)
	}

	override fun toString() = "${height.format(3)} * sin(" +
		"${squish.format(3)} * (x - ${shift.format(3)}))"
}

data class TangentEquation(
	val shift: Double, val squish: Double, val growth: Double
) : Equation {
	override val zero: Double
		get() = shift

	override fun make() = {x:Double -> Math.tan(squish * (x - shift)) * growth }

	override fun lowHighPair(random: Random): LowHighPair {
		val squishedPI = Math.abs(Math.PI / (2 * squish))
		val low = random.doubleBetween(shift - squishedPI, shift)
		val high = random.doubleBetween(shift, shift + squishedPI)
		return LowHighPair(low, high)
	}

	override fun toString() = "${growth.format(3)} * sin(" +
		"${squish.format(3)} * (x - ${shift.format(3)}))"
}


fun Random.doubleBetween(low: Double, high: Double): Double {
	val range = high - low
	return nextDouble() * range + low
}
fun Double.format(digits: Int) = String.format("%.${digits}f", this)

