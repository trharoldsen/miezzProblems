package miezz.chapter7

import java.util.*

fun makeD(A: IntArray): IntArray {
	val B = A.indices.asSequence()
		.flatMap { i -> A.indices.asSequence()
			.map { j -> Pair(i, j) } }
		.filter { it.first > it.second }
		.map { A[it.first] - A[it.second] }
		.toList()
	return B.sorted().toIntArray()
}

fun getSizeOfA(sizeOfD: Int): Int {
	return (1 + sqrt(1 + 8 * sizeOfD)) / 2
}

fun sqrt(value: Int): Int {
	return generateSequence(1) { it + 1 }
		.first { it * it  == value }

}

fun recreateA(D: IntArray): Pair<IntArray, Int> {
	val A = IntArray(getSizeOfA(D.size))
	val Dset = D.groupBy { it }
		.mapValues { it.value.size }
		.toSortedMap()
	val dTotal = IntArray(D.last()+1) { Dset[it] ?: 0 }
	val dCount = IntArray(D.last()+1) { 0 }

	var count = 0

	val initialTime = currentTimeSeconds()
	var times = LongArray(A.size) { initialTime }

	fun tryValue(aIndex: Int, nextValue: Int): Boolean {
		return (nextValue != A[aIndex+1])
	}

	fun addElement(aIndex: Int, nextValue: Int): List<Int> {
		A[aIndex] = nextValue
		dCount[nextValue] += 1
		val updatedIndices = (aIndex + 1..A.lastIndex)
			.asSequence()
			.map { A[it] - nextValue }
			.toList()
		updatedIndices.forEach { dCount[it] += 1 }
		return updatedIndices
	}

	fun constructCandidate(aIndex: Int, dIndex: Int): Boolean {
		var dIndex = dIndex
		while(dIndex >= aIndex - 1) {
			count += 1
			val nextValue = D[dIndex]

			if (tryValue(aIndex, nextValue)) {
				val updatedIndices = addElement(aIndex, nextValue)

				if (updatedIndices.all { dCount[it] <= dTotal[it] }) {
					val found = if (aIndex == 1) {
						Arrays.equals(dCount, dTotal)
					} else {
						val result = constructCandidate(aIndex - 1, dIndex - 1)
						if (aIndex > 11) {
							val time = currentTimeSeconds()
							val changed = time - times[aIndex]
							times[aIndex] = time
							println("aIndex($aIndex) decreased after $changed seconds")
						}
						result
					}
					if (found) return true
				}
				updatedIndices.forEach { dCount[it] -= 1 }
				dCount[nextValue] -= 1
			}
			dIndex -= 1
		}
		return false
	}

	A[0] = 0
	if (A.size == 1)
		return Pair(A, 0)

	addElement(A.lastIndex, D.last())
	val found = constructCandidate(A.lastIndex-1, D.lastIndex-1)
	if (!found)
		throw Exception("No possible reconstruction")

	return Pair(A, count)
}

private fun currentTimeSeconds() = System.currentTimeMillis() / 1000

fun main(args: Array<String>) {
	val A = intArrayOf(0, 1, 3, 4, 7, 11, 13, 15, 18, 21, 25, 26, 28, 32, 40, 58, 70, 71, 82)
	println(A.joinToString{ "%3d".format(it) })
	val D = makeD(A)
	println(D.joinToString{ "%3d".format(it) })
	val (A_prime, count) = recreateA(D)
	println(count)
	println(A_prime.joinToString { "%3d".format(it) })
}

