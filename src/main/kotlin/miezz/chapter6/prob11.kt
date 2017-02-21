package miezz.chapter6

import miezz.util.SortedMutableSet

class Problem5_11<E>(initialCapacity: Int = 16) : SortedMutableSet<E> {
	var array: Array<Any?> = arrayOfNulls(initialCapacity)

	private fun growArray() {
		array = array.copyOf(array.size * 2)
	}

	override var size: Int = 0
		private set

	override fun contains(element: E): Boolean {
		return array.binarySearch(element, 0, size) >= 0
	}

	@Suppress("UNCHECKED_CAST")
	override val first: E
		get() {
			if (isEmpty())
				throw IndexOutOfBoundsException()
			return array[0] as E
		}

	override fun add(element: E): Boolean {
		val index = array.binarySearch(element, 0, size-1)
		return if (index < 0) {
			if (size == array.size)
				growArray()
			val insertionPoint = -index - 1
			System.arraycopy(array, insertionPoint, array, insertionPoint + 1, size-insertionPoint)
			array[insertionPoint] = element
			size += 1
			true
		} else {
			false
		}
	}

	@Suppress("UNCHECKED_CAST")
	override val last: E
		get() {
			if (isEmpty())
				throw IndexOutOfBoundsException()
			return array[size - 1] as E
		}

	override fun clear() {
		for (i in 0..size-1)
			array[i] = null // prevent leaking references
		size = 0
	}

	override fun remove(element: E): Boolean {
		val index = array.binarySearch(element, 0, size-1)
		return if (index < 0) {
			false
		} else {
			System.arraycopy(array, index+1, array, index, size-index-1)
			array[--size] = null // prevent leaking reference
			true
		}
	}
}
