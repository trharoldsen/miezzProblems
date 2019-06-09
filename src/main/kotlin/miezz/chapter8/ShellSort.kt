package miezz.chapter8

import java.util.*

interface SortingAlgorithm {
	fun sort(input: IntArray) = sort(input, 0, input.size)
	fun sort(input: IntArray, begin: Int, end: Int)
}

typealias ShellsortGapFactory = (Int) -> Sequence<Int>
class ShellSort(private val gaps: ShellsortGapFactory): SortingAlgorithm {
	override fun sort(input: IntArray, begin: Int, end: Int) {
		for (gap in gaps(end - begin)) {
			for (i in begin+gap until end) {
				var j = i
				val tmp = input[i]
				while (j >= begin+gap && tmp < input[j-gap]) {
					input[j] = input[j-gap]
					j -= gap
				}
				input[j] = tmp
			}
		}
	}

	companion object {
		val ORIGINAL: ShellsortGapFactory = {
			sequence {
				var gap = it / 2
				while (gap > 0) {
					yield(gap)
					gap /= 2
				}
			}
		}

		val ODDS_ONLY: ShellsortGapFactory = {
			sequence {
				var gap = it / 2
				while (gap > 0) {
					if (gap % 2 == 0)
						gap += 1
					yield(gap)
					gap /= 2
				}
			}
		}

		val GONNETS: ShellsortGapFactory = {
			sequence {
				var updated = it / 2
				while (updated > 0) {
					val gap = if (updated == 2) 1 else updated
					yield(gap)
					updated = (gap.toDouble() / 2.2).toInt()
				}
			}
		}

		val HIBBARDS: ShellsortGapFactory = {
			val gaps = ArrayList<Int>()
			val maxGap = it / 2
			var nextPow2 = 2
			while (nextPow2 - 1 < maxGap) {
				gaps += nextPow2 - 1
				nextPow2 *= 2
			}
			gaps.reversed().asSequence()
		}

		val KNUTH: ShellsortGapFactory = {
			val gaps = ArrayList<Int>()
			val maxGap = it / 2
			var nextPow3 = 1
			while (true) {
				nextPow3 *= 3
				val nextGap = (nextPow3 - 1) / 2
				if (nextGap > maxGap)
					break
				gaps += nextGap
			}
			gaps.reversed().asSequence()
		}

		val SEDGEWICK: ShellsortGapFactory = {
			val form1 = ArrayList<Int>()
			val form2 = ArrayList<Int>()
			val maxGap = it / 2

			var nextPow2 = 1
			while (true) {
				val nextGap = 9 * (nextPow2 * nextPow2 - nextPow2) + 1
				if (nextGap > maxGap)
					break
				form1 += nextGap
				nextPow2 *= 2
			}

			nextPow2 = 4
			while (true) {
				val nextGap = (nextPow2 * nextPow2) - 3 * nextPow2 + 1
				if (nextGap > maxGap)
					break
				form2 += nextGap
				nextPow2 *= 2
			}

			sequence<Int> {
				val f1Iter = form1.listIterator(form1.lastIndex + 1)
				val f2Iter = form1.listIterator(form1.lastIndex + 1)

				var f2: Int? = f2Iter.previousOrNull()
				var f1: Int? = f1Iter.previousOrNull()

				while (f2 != null || f1 != null) {
					if (f2 == null) {
						yield(f1!!)
						f1 = f1Iter.previousOrNull()
					} else if (f1 == null || f2 > f1) {
						yield(f2)
						f2 = f2Iter.previousOrNull()
					} else {
						yield(f1)
						f1 = f1Iter.previousOrNull()
					}
				}
			}
		}
	}
}

private fun <T> ListIterator<T>.previousOrNull() =
	if (hasPrevious()) previous() else null

