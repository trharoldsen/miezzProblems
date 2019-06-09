package miezz.chapter9

import java.util.*
import kotlin.collections.HashSet

interface FloydsAlgorithm {
	fun <T> permutation(N: List<T>, M: Int): List<T>
}

class TailRecursiveFloyd(var random: Random = Random()) : FloydsAlgorithm {
	override fun <T> permutation(N: List<T>, M: Int): List<T> {
		return when {
			M < 0 -> throw IllegalArgumentException("Argument M less than 0: $M")
			M > N.size -> throw IllegalArgumentException("Argument M is " +
				"greater than size of N: M=$M, N.size=${N.size}")
			M == 0 -> emptyList()
			else -> {
				val set = HashSet<Int>()
				permutation(N.size - M + 1, M, set)
				set.map { N[it] }
			}
		}
	}

	private tailrec fun permutation(N: Int, M: Int, set: MutableSet<Int>) {
		val nextInt = random.nextInt(N)
		set += if (nextInt in set) N-1 else nextInt

		if (M > 1)
			permutation(N+1, M-1, set)
	}
}

class RecursiveFloyd(var random: Random = Random()) : FloydsAlgorithm {
	override fun <T> permutation(N: List<T>, M: Int): List<T> {
		return when {
			M < 0 -> throw IllegalArgumentException("Argument M less than 0: $M")
			M > N.size -> throw IllegalArgumentException("Argument M is " +
				"greater than size of N: M=$M, N.size=${N.size}")
			M == 0 -> emptyList()
			else -> {
				val set = HashSet<Int>()
				permutation(N.size, M, set)
				set.map { N[it] }
			}
		}
	}

	private fun permutation(N: Int, M: Int, set: MutableSet<Int>) {
		if (M > 1)
			permutation(N-1, M-1, set)

		val nextInt = random.nextInt(N)
		set += if (nextInt in set) N-1 else nextInt
	}
}

class IterativeFloyd(var random: Random = Random()) : FloydsAlgorithm {
	override fun <T> permutation(N: List<T>, M: Int): List<T> {
		val set = HashSet<Int>()

		for (i in 1..M) {
			val max = N.size - M + i - 1
			val nextInt = random.nextInt(max + 1)
			set += if (nextInt in set) max else nextInt
		}
		return set.map { N[it] }
	}
}
