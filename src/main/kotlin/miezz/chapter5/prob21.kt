package miezz.chapter5

import miezz.utils.nextItemPercent
import java.util.*

interface Problem5_21 {
	fun solve(input: IntArray): Boolean
}

class Naive5_21 : Problem5_21 {
	override fun solve(input: IntArray) =
			input.withIndex().any { it.index == it.value }
}

class NaiveWhile5_21 : Problem5_21 {
	override fun solve(input: IntArray): Boolean {
		for (i in (0..input.lastIndex)) {
			if (input[i] == i)
				return true
		}
		return false
	}
}


class JumpForward5_21 : Problem5_21 {
	override fun solve(input: IntArray): Boolean {
		var i = 0
		val lastIndex = input.lastIndex
		while (i <= lastIndex) {
			val input_i = input[i]
			if (input_i >= i) {
				if (input_i == i)
					return true
				else
					i = input_i
			} else {
				i++
			}
		}
		return false
	}
}

class RandomizedTestFactory(
	val lowMin: Int, val lowMax: Int,
	distribution: List<Int>
) {
	val distribution = distribution.withIndex()
		.associate { Pair(it.index, it.value) }
		.toMap()

	fun make(seed: Long, numElements: Int): IntArray {
		val random = Random(seed)

		val low = random.nextInt(lowMax - lowMin + 1) + lowMin
		val array = IntArray(numElements)
		array[0] = low

		var i = 1
		while (i < numElements) {
			array[i] = array[i-1] + random.nextItemPercent(distribution)
			i++
		}
		return array
	}
}
