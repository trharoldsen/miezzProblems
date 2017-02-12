package miezz.chapter5

class Prob5_26 {
	fun plus(arr: IntArray): Int {
		val max = arr.max()!!
		return max + max
	}

	fun minus(arr: IntArray): Int {
		var low = Int.MAX_VALUE
		var max = Int.MIN_VALUE
		for (i in arr) {
			low = minOf(low, i)
			val diff = i - low
			max = maxOf(max, diff)
		}
		return max
	}

	fun times(arr: IntArray): Int {
		val max = arr.max()!!
		return max * max
	}

	fun div(arr: IntArray): Double {
		var low = Int.MAX_VALUE
		var max = Double.MIN_VALUE
		for (i in arr) {
			low = minOf(low, i)
			val diff = i.toDouble() / low
			max = maxOf(max, diff)
		}
		return max
	}

	fun goldInt(arr: IntArray, op: (Int, Int) -> Int): Int {
		return arr.indices.asSequence()
			.flatMap {
				i -> (i..arr.lastIndex).asSequence()
				.map { j -> Pair(i, j) }
			}.map { (i, j) -> op(arr[j], arr[i])}
			.max()!!
	}

	fun goldDiv(arr: IntArray): Double {
		return arr.indices.asSequence()
			.flatMap {
				i -> (i..arr.lastIndex).asSequence()
				.map { j -> Pair(i, j) }
			}.map { (i, j) -> arr[j].toDouble() / arr[i]}
			.max()!!
	}
}
