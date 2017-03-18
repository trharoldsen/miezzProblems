package miezz.chapter8

import java.util.TreeSet

class Problem8_22 {
	val fifths = LongArray(76) { it.toLong() * it * it * it * it }

	fun compute(): List<Int> {
		val sums = ArrayList<ThreeSum>()
		val diffs = ArrayList<ThreeDiff>()
		for (i in 1..75) {
			for (j in i..75) {
				for (k in j..75) {
					sums += ThreeSum(i, j, k)
					diffs += ThreeDiff(i, j, k)
				}
			}
		}

		sums.sort()
		diffs.sort()

		var i = 0
		var j = 0

		while (i <= sums.lastIndex && j <= diffs.lastIndex) {
			val sum = sums[i]
			val diff = diffs[j]

			val cmp = sum.sum.compareTo(diff.diff)
			when {
				cmp < 0 -> {
					i++
				}
				cmp > 0 -> {
					j++
				}
				cmp == 0 -> {
					if (sum.C > diff.D) {
						j++
					} else {
						return listOf(sum.A, sum.B, sum.C,
							diff.D, diff.E, diff.F)
					}
				}
			}
		}
		error("Failed to find it")
	}

	inner class ThreeSum(val A: Int, val B: Int, val C: Int): Comparable<ThreeSum> {
		val sum get() = fifths[A] + fifths[B] + fifths[C]

		override fun compareTo(other: ThreeSum): Int {
			val sumComp = sum.compareTo(other.sum)
			if (sumComp != 0)
				return sumComp
			return C.compareTo(other.C)
		}
	}

	inner class ThreeDiff(val D: Int, val E: Int, val F: Int): Comparable<ThreeDiff> {
		val diff get() = fifths[F] - (fifths[D] + fifths[E])

		override fun compareTo(other: ThreeDiff): Int {
			val diffComp = diff.compareTo(other.diff)
			if (diffComp != 0)
				return diffComp
			return D.compareTo(other.D)
		}
	}
}

fun main(args: Array<String>) {
	val result = Problem8_22().compute()
	println(result)
}