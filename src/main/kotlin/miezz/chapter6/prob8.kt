package miezz.chapter6

import miezz.util.Stack

class Problem5_8<E>: Stack<E> {
	val list: MutableList<E> = ArrayList()

	override val size: Int
		get() = list.size

	override fun push(value: E): Boolean {
		list.add(value)
		return true
	}

	override fun pop(): E {
		val ret = list[list.size - 1]
		list.removeAt(list.size - 1)
		return ret
	}

	override fun peek(): E {
		return list[list.size - 1]
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
