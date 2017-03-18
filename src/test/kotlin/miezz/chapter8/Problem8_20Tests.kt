package miezz.chapter8

import kotlinx.support.jdk8.streams.asStream
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.*
import org.junit.jupiter.api.TestFactory
import java.util.*
import java.util.stream.Stream
import kotlin.collections.HashSet

class Problem8_20Tests {
	@TestFactory fun testArrayWith(): Stream<DynamicTest> {
		return (1..100).asSequence()
			.map { Random(it.toLong()) }
			.map { it.buildArrayWith(500, 2000) }
			.map { dynamicTest("withTest"){
				assertTrue(prob20(it.first, it.second)) }
			}.asStream()
	}

	@TestFactory fun testArrayWithout(): Stream<DynamicTest> {
		return (1..100).asSequence()
			.map { Random(it.toLong()) }
			.map { it.buildArrayWithout(500, 2000) }
			.map { dynamicTest("withoutTest"){
				assertFalse(prob20(it.first, it.second)) }
			}.asStream()
	}
}

private fun Random.buildArrayWith(size: Int, bound: Int): Pair<IntArray, Int> {
	val guaranteed = IntArray(4) { nextInt(bound)}

	val list = ArrayList<Int>(size)
	list += guaranteed.asList()
	for (i in 5..size) {
		list += nextInt(bound)
	}
	Collections.shuffle(list)
	return Pair(list.toIntArray(), guaranteed.sum())
}

private fun Random.buildArrayWithout(size: Int, bound: Int): Pair<IntArray, Int> {
	val list = ArrayList<Int>(size)
	val sum2 = HashSet<Int>()
	val disqualified = HashSet<Int>()

	val sum = nextInt(bound * 4)

	if (sum % 4 == 0)
		disqualified.add(sum / 4)

	for (i in 0..size-1) {
		var v: Int
		do {
			v = nextInt(bound)
		} while (v in disqualified)

		if ((sum - v) % 3 == 0)
			disqualified.add((sum - v) / 3)
		list.add(v)

		val newDoubles = list.map { it + v }
		disqualified.addAll(newDoubles.map { sum - it }.filter { it % 2 == 0 }.map { it / 2 })
		sum2.addAll(newDoubles)

		disqualified.addAll(sum2.map { sum - (it + v) })
	}

	return Pair(list.toIntArray(), sum)
}
