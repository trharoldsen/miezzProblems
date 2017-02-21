package miezz.chapter6

import miezz.util.Queue
import java.util.*

class Problem5_10<E>(capacity: Int): Queue<E> {
	val list: Array<Any?> = kotlin.arrayOfNulls(capacity)
	var size = 0

	override fun enqueue(value: E): Boolean {
		if (size >= list.size)
			return false
		list[size++] = value
		return true
	}

	@Suppress("UNCHECKED_CAST")
	override fun dequeue(): E {
		if (size == 0)
			throw NoSuchElementException()
		val value = list[0] as E
		for (i in 1..list.lastIndex)
			list[i-1] = list[i]
		return value
	}

	@Suppress("UNCHECKED_CAST")
	override fun peek(): E {
		if (size == 0)
			throw NoSuchElementException()
		return list[0] as E
	}

	override fun clear() {
		size = 0
	}

	override fun isFull(): Boolean {
		return size == list.size
	}

	override fun isEmpty(): Boolean {
		return size == 0
	}
}
