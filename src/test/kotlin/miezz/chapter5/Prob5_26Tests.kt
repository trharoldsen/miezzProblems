package miezz.chapter5

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.util.*

class Prob5_26Tests {
	fun randomIntArray(random: Random) =
		random.ints(100, 1, 50).toArray()

	@TestFactory fun testAdd(): List<DynamicTest> {
		val p = Prob5_26()
		return (1..10000).map { Random(it.toLong()) }
			.map { randomIntArray(it) }
			.map { DynamicTest.dynamicTest(it.toString()) {
				assertEquals(p.goldInt(it, { a, b -> a + b}), p.plus(it))
			} }
	}

	@TestFactory fun testMinus(): List<DynamicTest> {
		val p = Prob5_26()
		return (1..10000).map { Random(it.toLong()) }
			.map { randomIntArray(it) }
			.map { DynamicTest.dynamicTest(it.toString()) {
				assertEquals(p.goldInt(it, { a, b -> a - b}), p.minus(it))
			} }
	}

	@TestFactory fun testTimes(): List<DynamicTest> {
		val p = Prob5_26()
		return (1..10000).map { Random(it.toLong()) }
			.map { randomIntArray(it) }
			.map { DynamicTest.dynamicTest(it.toString()) {
				assertEquals(p.goldInt(it, { a, b -> a * b}), p.times(it))
			} }
	}

	@TestFactory fun testDivide(): List<DynamicTest> {
		val p = Prob5_26()
		return (1..10000).map { Random(it.toLong()) }
			.map { randomIntArray(it) }
			.map { DynamicTest.dynamicTest(it.toString()) {
				assertEquals(p.goldDiv(it), p.div(it))
			} }
	}
}
