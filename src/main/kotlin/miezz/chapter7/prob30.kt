package miezz.chapter7

import java.util.*
import kotlin.collections.HashSet

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

fun recreateA(D: IntArray): IntArray {
	val A = IntArray(getSizeOfA(D.size))
	val Dset = D.groupBy { it }
		.mapValues { it.value.size }
		.toSortedMap()
	val dTotal = IntArray(D.last()+1) { Dset[it] ?: 0 }
	val dCount = IntArray(D.last()+1) { 0 }

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
			val nextValue = D[dIndex]

			if (tryValue(aIndex, nextValue)) {
				val updatedIndices = addElement(aIndex, nextValue)

				if (updatedIndices.all { dCount[it] <= dTotal[it] }) {
					val found = if (aIndex == 1) {
						Arrays.equals(dCount, dTotal)
					} else {
						constructCandidate(aIndex - 1, dIndex - 1)
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
		return A

	addElement(A.lastIndex, D.last())
	val found = constructCandidate(A.lastIndex-1, D.lastIndex-1)
	if (!found)
		throw Exception("No possible reconstruction")

	return A
}

private typealias GapPair = Pair<Int, Int>

class RecreateA_v2 private constructor(val D: IntArray){
	val A = HashSet<Int>()
	val remainingGaps: IntArray
	val gapFillers: Array<MutableSet<GapPair>>

	init {
		this.remainingGaps = buildRemainingGaps()
		this.gapFillers = buildGapFillers()
	}

	private fun buildRemainingGaps(): IntArray {
		val Dset = D.groupBy { it }
			.mapValues { it.value.size }
			.toSortedMap()
		val remainingGaps = IntArray(D.last() + 1) { Dset[it] ?: 0 }
		return remainingGaps
	}

	private fun buildGapFillers(): Array<MutableSet<Pair<Int, Int>>> {
		val gapFillers = Array<MutableSet<GapPair>>(D.last() + 1) { HashSet(5) }
		for (i in 0..D.lastIndex - 1) {
			for (j in i + 1..D.lastIndex) {
				val gap = D[j] - D[i]
				if (remainingGaps[gap] != 0) {
					gapFillers[gap].add(GapPair(D[i], D[j]))
				}
			}
			gapFillers[D[i]].add(GapPair(0, D[i]))
		}
		gapFillers[D.last()].add(GapPair(0, D.last()))
		return gapFillers
	}

	private interface Move {
		fun perform()
		fun undo()
	}

	private inner class AddValue(val value: Int) : Move {
		val removedPairs = ArrayList<GapPair>()

		override fun perform() {
			A += value

			gapFillers.withIndex().forEach { (gap, set) ->
				val it = set.iterator()
				while (it.hasNext()) {
					val pair = it.next()
					if (pair.first in A && pair.second in A) {
						assert(value in pair)
						it.remove()
						removedPairs += pair
						remainingGaps[gap] -= 1
					}
				}
			}
		}

		override fun undo() {
			removedPairs.forEach {
				val gap = it.second - it.first
				remainingGaps[gap] += 1
				gapFillers[gap].add(it)
			}

			A -= value
		}
	}

	private inner class DisqualifyValue(val value: Int) : Move {
		val removedPairs = ArrayList<GapPair>()

		override fun perform() {
			gapFillers.forEach { set ->
				val it = set.iterator()
				while (it.hasNext()) {
					val pair = it.next()
					if (value in pair) {
						removedPairs += pair
						it.remove()
					}
				}
			}
		}

		override fun undo() {
			removedPairs.forEach {
				val gap = it.second - it.first
				gapFillers[gap].add(it)
			}
		}

	}

	private fun getRequired(): List<Int> {
		val required = HashSet<Int>()
		remainingGaps.zip(gapFillers).forEach {
			val slack = it.second.size - it.first
			if (slack == 0) {
				it.second.forEach {
					required.add(it.first)
					required.add(it.second)
				}
			}
		}
		return required.filter { it !in A }
	}

	private fun getInvalidated(): List<Int> {
		val invalidated = HashSet<Int>()
		remainingGaps.withIndex().forEach {
			if (it.value == 0) {
				for (pair in gapFillers[it.index]) {
					if (pair.first in A) {
						invalidated += pair.second
					} else if (pair.second in A) {
						invalidated += pair.first
					}
				}
			}
		}
		return invalidated.toList()
	}

	fun getNextCandidate(): Int? {
		var bestGap = Pair(-1, Int.MAX_VALUE)
		for (gap in remainingGaps.indices) {
			val set = gapFillers[gap]
			val remaining = remainingGaps[gap]

			val slack = set.size - remaining
			if (slack < 0)
				return null

			if (set.isNotEmpty() && slack <= bestGap.second)
				bestGap = Pair(gap, slack)
		}

		val candidateList = gapFillers[bestGap.first]
			.flatMap { listOf(it.first, it.second) }
			.filter { it !in A }
		return candidateList.groupBy { it }
			.maxBy { it.value.size }!!.key
	}

	private fun recreateA(): Boolean {
		val moves = ArrayList<Move>()

		var numMoves: Int
		do {
			numMoves = moves.size
			moves += getRequired().map { AddValue(it) }.onEach { it.perform() }
			moves += getInvalidated().map { DisqualifyValue(it) }.onEach { it.perform() }
		} while (moves.size > numMoves)

		if (isValidSolution())
			return true

		while (true) {
			val candidate = getNextCandidate() ?: break
			val move = AddValue(candidate).also { it.perform() }
			if (recreateA())
				return true

			move.undo()
			moves += DisqualifyValue(move.value).also { it.perform() }
		}
		moves.reversed().forEach { it.undo() }
		return false
	}

	private fun isValidSolution(): Boolean {
		return remainingGaps.asSequence()
			.zip(gapFillers.asSequence())
			.map { it.second.size - it.first }
			.all { it == 0 }
	}

	private fun execute(): IntArray {
		if (!recreateA())
			throw AssertionError("No valid solution")
		return A.toIntArray().sortedArray()
	}

	companion object {
		fun recreateA(D: IntArray): IntArray {
			return RecreateA_v2(D).execute()
		}
	}
}

private operator fun Pair<Int, Int>.contains(value: Int): Boolean {
	return value == first || value == second
}

private fun currentTimeSeconds() = System.currentTimeMillis() / 1000

fun main(args: Array<String>) {
	for (i in 0..10) {
		val A = Random(i.toLong()).makeIncreasingArray(45, 10000)
//	val A = intArrayOf(0, 1, 3, 4, 7, 11, 13, 15, 18, 21, 25, 26, 28, 32, 40, 58, 70, 71, 82, 83, 85, 88, 93, 96, 97, 99, 101)
//	val A = intArrayOf(0, 1, 3, 4, 7, 11, 13, 15, 18, 21, 25, 26, 28, 32, 40, 58, 70, 71, 82)
		println(A.joinToString { "%3d".format(it) })
		val D = makeD(A)
		println(D.joinToString { "%3d".format(it) })
		var startTime = currentTimeSeconds()
		val A_prime = RecreateA_v2.recreateA(D)
		println("ran v2 in ${currentTimeSeconds() - startTime} seconds")
//	startTime = currentTimeSeconds()
//	val A_prime2 = recreateA(D)
//	println("ran v1 in ${currentTimeSeconds() - startTime} seconds")

		println(A_prime.joinToString { "%3d".format(it) })
//	println(A_prime2.joinToString { "%3d".format(it) })
	}
}

private fun Random.makeIncreasingArray(size: Int, range: Int): IntArray {
	val set = HashSet<Int>()
	set.add(0)
	while (set.size < size) {
		set.add(nextInt(range))
	}
	return set.toList().sorted().toIntArray()
}
