package miezz.chapter6

import miezz.util.PriorityQueue

class Problem5_12<E>(
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
		val index = if (comparator == null)
				array.binarySearch(value, 0, size-1)
		else
			array.binarySearch(value, comparator, 0, size-1)

		if (size == array.size)
			growArray()
		val insertionPoint = if (index >= 0) index else -index - 1
		System.arraycopy(array, insertionPoint, array, insertionPoint + 1, size-insertionPoint)
		array[insertionPoint] = value
		size += 1
		return true
	}

	override fun clear() {
		array = kotlin.arrayOfNulls(array.size)
		size = 0
	}

	override fun dequeue(): E {
		if (size == 0)
			throw NoSuchElementException()

		val returnValue = array[--size]
		array[--size] = null // prevent leaking reference
		@Suppress("UNCHECKED_CAST")
		return returnValue as E
	}

	override fun isEmpty(): Boolean = size == 0
	override fun isFull(): Boolean = false
}
