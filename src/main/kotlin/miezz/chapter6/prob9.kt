package miezz.chapter6

import miezz.util.Queue
import java.util.*
import kotlin.NoSuchElementException

class Problem5_9<E>: Queue<E> {
	val list: MutableList<E> = LinkedList()

	override fun enqueue(value: E): Boolean {
		list.add(value)
		return true
	}

	override fun dequeue(): E {
		if (list.isEmpty())
			throw NoSuchElementException()
		val ret = list[0]
		list.removeAt(0)
		return ret
	}

	override fun peek(): E {
		if (list.isEmpty())
			throw NoSuchElementException()
		return list[0]
	}

	override fun clear() {
		list.clear()
	}

	override fun isFull(): Boolean {
		return false
	}

	override fun isEmpty(): Boolean {
		return list.isEmpty()
	}
}
