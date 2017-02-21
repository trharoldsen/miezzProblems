package miezz.chapter6

import miezz.util.PriorityQueue

class Problem5_14<E>(
	initialCapacity: Int = 16,
	comparator: Comparator<E>? = null
) : PriorityQueue<E> {
	@Suppress("UNCHECKED_CAST")
	private val comparator = comparator as Comparator<Any?>?

	private var array: Array<Any?> = arrayOfNulls(initialCapacity)
	private var size = 0
	private var minimum = -1


	private fun growArray() {
		array = array.copyOf(array.size * 2)
	}

	override fun peek(): E {
		if (isEmpty())
			throw NoSuchElementException()
		@Suppress("UNCHECKED_CAST")
		return array[minimum] as E
	}

	override fun enqueue(value: E): Boolean {
		if (size == array.size)
			growArray()
		array[size] = value
		if (minimum == -1 || value lessThan array[minimum])
			minimum = size
		size += 1
		return true
	}

	override fun clear() {
		array = arrayOfNulls(array.size)
		size = 0
	}

	override fun dequeue(): E {
		if (size == 0)
			throw NoSuchElementException()

		val returnValue = array[minimum]
		System.arraycopy(array, minimum+1, array, minimum, size-minimum-1)
		array[--size] = null // prevent leaking references

		minimum = 0
		for (i in 1..size-1) {
			if (array[i] lessThan array[minimum])
				minimum = i
		}

		@Suppress("UNCHECKED_CAST")
		return returnValue as E
	}

	override fun isEmpty(): Boolean = size == 0
	override fun isFull(): Boolean = false

	private infix fun Any?.lessThan(other: Any?): Boolean {
		@Suppress("UNCHECKED_CAST")
		if (comparator == null)
			return (this as Comparable<E>) < (other as E)
		else
			return comparator.compare(this, other) < 0
	}
}
