package miezz.chapter5

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class Naive5_21_ImplTests : prob5_21Tests {
	override val uut: Problem5_21 = Naive5_21()
}

class NaiveWhile5_21_ImplTests : prob5_21Tests {
	override val uut: Problem5_21 = NaiveWhile5_21()
}

class Jump5_21_ImplTests : prob5_21Tests {
	override val uut: Problem5_21 = JumpForward5_21()
}

interface prob5_21Tests {
	val uut: Problem5_21

	@Test
	fun oneAhead() {
		val case = intArrayOf(1, 2, 3, 4, 5, 6, 7)
		assertFalse { uut.solve(case) }
	}

	@Test
	fun oneBehind() {
		val case = intArrayOf(-1, 0, 1, 2, 3, 4)
		assertFalse { uut.solve(case) }
	}

	@Test
	fun all() {
		val case = intArrayOf(0, 1, 2, 3, 4)
		assertTrue { uut.solve(case) }
	}
	
	@Test
	fun first() {
		val case = intArrayOf(0, 3, 4, 5, 6)
		assertTrue { uut.solve(case) }
	}
	
	@Test
	fun last() {
		val case = intArrayOf(-1, 0, 1, 2, 4)
		assertTrue { uut.solve(case) }
	}
	
	@Test
	fun holdingNumber() {
		val case = intArrayOf(3, 3, 3, 3, 3, 4)
		assertTrue( uut.solve(case))
	}

	@TestFactory
	fun randomizedUsuallyLow(): List<DynamicTest> {
		val testFactory = RandomizedTestFactory(-10, -5, listOf(30, 50, 10, 10))
		val gold = Naive5_21()
		return (1..20)
			.map { testFactory.make(it.toLong() * 0xFFEF, it * 30) }
			.withIndex()
			.map { DynamicTest.dynamicTest("randomizedUsuallyLow: ${it.value.joinToString { "$it" }}") {
				assertEquals(gold.solve(it.value), uut.solve(it.value))
			} }
	}

	@TestFactory
	fun randomizedUsuallyHigh(): List<DynamicTest> {
		val testFactory = RandomizedTestFactory(5, 10, listOf(30, 50, 10, 10))
		val gold = Naive5_21()
		return (1..20)
			.map { testFactory.make(it.toLong() * 0xFFEF, it * 30) }
			.withIndex()
			.map { DynamicTest.dynamicTest("randomizedUsuallyHigh: ${it.value.joinToString { "$it" }}") {
				assertEquals(gold.solve(it.value), uut.solve(it.value))
			} }
	}

	@TestFactory
	fun randomizedSlowGrowth(): List<DynamicTest> {
		val testFactory = RandomizedTestFactory(10, 20, listOf(35, 50, 10, 5))
		val gold = Naive5_21()
		return (1..20)
			.map { testFactory.make(it.toLong() * 0xFFEF, it * 30) }
			.withIndex()
			.map { DynamicTest.dynamicTest("randomizedSlowGrowth: ${it.value.joinToString { "$it" }}") {
				assertEquals(gold.solve(it.value), uut.solve(it.value))
			} }
	}

	@TestFactory
	fun randomizedFastGrowth(): List<DynamicTest> {
		val testFactory = RandomizedTestFactory(-20, -10, listOf(30, 40, 20, 10))
		val gold = Naive5_21()
		return (1..20)
			.map { testFactory.make(it.toLong() * 0xFFEF, it * 30) }
			.withIndex()
			.map { DynamicTest.dynamicTest("randomizedSlowGrowth: ${it.value.joinToString { "$it" }}") {
				assertEquals(gold.solve(it.value), uut.solve(it.value))
			} }
	}
}

