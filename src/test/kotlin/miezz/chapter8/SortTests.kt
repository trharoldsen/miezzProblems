package miezz.chapter8

import kotlinx.support.jdk8.streams.asStream
import miezz.chapter8.ShellSort.Companion.GONNETS
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import java.util.*
import java.util.stream.Stream
import kotlin.collections.ArrayList

abstract class SortTests {
	abstract val sortingAlgorithm: SortingAlgorithm

	private fun makeSortTests(
		numRuns: Int, makeArray: (Int) -> IntArray
	): Stream<DynamicTest> {
		return (1..numRuns).asSequence().map {
			dynamicTest("test $it") {
				val array = makeArray(it)
				val copy = array.clone()
				sortingAlgorithm.sort(array)
				assertArrayEquals(copy.sortedArray(), array)
			}
		}.asStream()
	}

	@TestFactory fun inOrderTests(): Stream<DynamicTest> {
		return makeSortTests(129) { size -> IntArray(size) { it } }
	}

	@TestFactory fun reverseOrderTests(): Stream<DynamicTest> {
		return makeSortTests(129) { size -> IntArray(size) { size - 1 - it } }
	}

	@TestFactory fun randomPrependedTests(): Stream<DynamicTest> {
		return makeSortTests(100) {
			val random = Random(it.toLong())
			val array = IntArray(random.nextInt(129) + 5) { it }.toMutableList()
			repeat(random.nextInt(14) + 1) {
				array.swap(minOf(array.lastIndex, it), random.nextInt(array.size))
			}
			array.toIntArray()
		}
	}

	@TestFactory fun randomAppendedTests(): Stream<DynamicTest> {
		return makeSortTests(100) {
			val random = Random(it.toLong())
			val array = IntArray(random.nextInt(124) + 5) { it }.toMutableList()
			repeat(random.nextInt(14) + 1) {
				array.swap(maxOf(0, array.lastIndex - it), random.nextInt(array.size))
			}
			array.toIntArray()
		}
	}

	@TestFactory fun duplicatesTests(): Stream<DynamicTest> {
		return makeSortTests(100) {
			val random = Random(it.toLong())
			val array = IntArray(random.nextInt(124) + 5) { it }.toMutableList()
			Collections.shuffle(array)
			repeat(random.nextInt(14) + 1) {
				array += array[random.nextInt(array.size)]
			}
			array.toIntArray()
		}
	}

	@TestFactory fun randomizedTests(): Stream<DynamicTest> {
		return makeSortTests(1000) {
			val random = Random(it.toLong())
			val size = random.nextInt(512)
			val list = ArrayList<Int>(size)
			repeat(size) { list.add(random.nextInt(1000)) }
			list.toIntArray()
		}
	}
}

private fun <T> MutableList<T>.swap(i1: Int, i2: Int) {
	val tmp = this[i1]
	this[i1] = this[i2]
	this[i2] = tmp
}

class InsertionSortTests : SortTests() {
	override val sortingAlgorithm: SortingAlgorithm
		get() = InsertionSort()
}

class MergeSortTests : SortTests() {
	override val sortingAlgorithm: SortingAlgorithm
		get() = MergeSort(16)
}

class ShellSortTests : SortTests() {
	override val sortingAlgorithm: SortingAlgorithm
		get() = ShellSort(GONNETS)
}

class QuickSortTests : SortTests() {
	override val sortingAlgorithm: SortingAlgorithm
		get() = QuickSort(16)
}

