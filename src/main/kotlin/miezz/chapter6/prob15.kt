package miezz.chapter6

import miezz.util.PriorityQueue

class Prob5_15<E>(
	initialCapacity: Int = 16,
	comparator: Comparator<E>? = null
) : PriorityQueue<E> {
	@Suppress("UNCHECKED_CAST")
	private val comparator = comparator as Comparator<Any?>?

	private var array: Array<Any?> = arrayOfNulls(initialCapacity)
	private var size = 0

	private fun growArray() {
		array = array.copyOf(array.size * 2)
	}

	override fun enqueue(value: E): Boolean {
		if (size == array.size)
			growArray()
		val index = if (comparator != null)
			array.binarySearch(value, comparator, 0, size)
		else
			array.binarySearch(value, 0, size)
		val insertion = if (index >= 0) index else -index - 1
		System.arraycopy(array, insertion, array, insertion+1, size-insertion-1)
		array[insertion] = value
		return true
	}

	override fun dequeue(): E {
		@Suppress("UNCHECKED_CAST")
		val value = array[--size] as E
		array[size] = null // clear out the reference
		return value
	}

	override fun peek(): E {
		@Suppress("UNCHECKED_CAST")
		return array[size-1] as E
	}

	override fun clear() {
		array = arrayOfNulls(array.size)
	}

	override fun isFull(): Boolean {
		return false
	}

	override fun isEmpty(): Boolean {
		return size == 0
	}

}