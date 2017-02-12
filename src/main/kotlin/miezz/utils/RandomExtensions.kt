package miezz.utils

import kotlinx.support.jdk8.streams.asSequence
import java.util.*


typealias Percentage = Int

fun <K> Random.nextItemPercent(distribution: Map<K, Percentage>): K {
	val random = nextInt(100)
	var percentSum = 0
	for ((value, percent) in distribution) {
		percentSum += percent
		if (random < percentSum)
			return value
	}
	throw IllegalArgumentException("distribution does not add to 100")
}

fun <K> Random.nextItemPermil(distribution: Map<K, Percentage>): K {
	val random = nextInt(1000)
	var percentSum = 0
	for ((value, percent) in distribution) {
		percentSum += percent
		if (random < percentSum)
			return value
	}
	throw IllegalArgumentException("distribution does not add to 1000")
}

fun Random.doublesSeq() = doubles().asSequence()

fun Random.nextPoisson(lambda: Double): Int {
	val L = Math.exp(-lambda)
	var k = 0
	var p = 1.0

	do {
		val u = nextDouble()
		k += 1
		p *= u
	} while (p > L)
	return k - 1
}

fun Random.nextInt(cdf: (Int) -> Double): Int {
	val u = nextDouble()

	@Suppress("NAME_SHADOWING")
	fun binarySearch(low: Int, high: Int): Int {
		var low = low
		var high = high

		while(low != high) {
			val pivot = (low + high) / 2
			val prob = cdf(pivot)

			if (prob < u) {
				low = pivot + 1
			} else {
				high = pivot
			}
		}
		return low
	}

	val direction = if (cdf(0) > u) 1 else -1
	var low = 0
	var high = 128

	while(cdf(high * direction) < u) {
		low = high
		high *= 2
	}
	return if (direction == 1)
		binarySearch(low, high)
	else
		-binarySearch(-high, -low)
}
