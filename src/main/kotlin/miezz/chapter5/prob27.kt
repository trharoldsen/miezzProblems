package miezz.chapter5

class Prob5_27 {
	fun findPrimes(N: Int): List<Int> {
		val sqrtN = sqrt(N)
		val primes = ArrayList<Int>()

		val array = BooleanArray(N.coerceAtLeast(2)) { true }
		var low = 2
		while ((low <= sqrtN)) {
			primes += low

			var index = low * 2
			while (index < array.size) {
				array[index] = false
				index += low
			}

			do {
				low += 1
			} while (low <= sqrtN && !array[low])
		}

		return primes
	}

	private fun sqrt(N: Int): Int {
		@Suppress("NAME_SHADOWING")
		fun binarySearch(low: Int, high: Int): Int {
			var low = low
			var high = high

			while(low != high) {
				val pivot = (low + high) / 2
				if (pivot * pivot < N) {
					low = pivot + 1
				} else {
					high = pivot
				}
			}
			return low
		}

		var low = 0
		var high = 128

		while(high * high < N) {
			low = high
			high *= 2
		}

		return binarySearch(low, high)
	}
}