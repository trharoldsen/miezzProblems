package miezz.chapter8

fun prob20(input: IntArray, sum: Int): Boolean {
	val sums = input.indices.asSequence().flatMap { first ->
		input.asList().subList(first, input.lastIndex+1).asSequence()
			.map { input[first] + it }
	}.toList().toIntArray()
	sums.sort()

	var i = 0
	var j = sums.lastIndex
	while (i <= j) {
		val compare = (sums[i] + sums[j]).compareTo(sum)
		when {
			compare < 0 -> i +=1
			compare > 0 -> j -= 1
			else -> return true
		}
	}
	return false
}

fun prob21(input: IntArray, sum: Int): Boolean {
	input.sort()
	for (first in input) {
		var i = 0
		var j = input.lastIndex
		while (i <= j) {
			val compare = (first + input[i] + input[j]).compareTo(sum)
			when {
				compare < 0 -> i +=1
				compare > 0 -> j -= 1
				else -> return true
			}
		}
	}
	return false
}