package miezz.utils

import kotlinx.support.jdk8.streams.asSequence
import java.util.*


/**
 * Created by Haroldsen on 1/23/2017.
 */

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
