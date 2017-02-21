package miezz.chapter6

import miezz.util.PriorityQueue

class Problem5_13<E>(
	initialCapacity: Int = 16,
	comparator: Comparator<E>? = null
) : PriorityQueue<E> {
	private var array: Array<Any?> = arrayOfNulls(initialCapacity)
	private var size: Int = 0
	@Suppress("UNCHECKED_CAST")
	private val comparator = comparator?.reversed() as Comparator<Any?>?

	private fun growArray() {
		array = array.copyOf(array.size * 2)
	}

	@Suppress("UNCHECKED_CAST")
	override fun peek(): E {
		if (isEmpty())
			throw NoSuchElementException()
		return array[0] as E
	}

	override fun enqueue(value: E): Boolean {
		if (size == array.size)
			growArray()
		array[size++] = value
		return true
	}

	override fun clear() {
		array = kotlin.arrayOfNulls(array.size)
		size = 0
	}

	override fun dequeue(): E {
		if (size == 0)
			throw NoSuchElementException()

		var least = 0
		for (i in 1..size-1) {
			if (array[i] lessThan array[least])
				least = i
		}

		val returnValue = array[least]
		System.arraycopy(array, least+1, array, least, size-least-1)
		array[--size] = null // prevent leaking references
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
