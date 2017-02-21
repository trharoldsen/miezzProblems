package miezz.util

import java.util.*
import kotlin.NoSuchElementException

interface Stack<E> {
	val size: Int
	fun push(value: E): Boolean
	fun pop(): E
	fun peek(): E
	fun clear(): Unit
	fun isFull(): Boolean
	fun isEmpty(): Boolean
	fun isNotEmpty() = !isEmpty()
}

class ArrayDequeStack<E> : Stack<E> {
	private val deque: ArrayDeque<E>

	constructor() { deque = ArrayDeque() }
	constructor(defaultCapacity: Int) { deque = ArrayDeque<E>(defaultCapacity)}

	override val size: Int
		get() = deque.size

	override fun push(value: E): Boolean {
		deque.push(value)
		return true
	}

	override fun pop(): E {
		if (deque.isEmpty())
			throw NoSuchElementException()
		return deque.pop()
	}

	override fun peek(): E {
		if (deque.isEmpty())
			throw NoSuchElementException()
		return deque.peek()
	}

	override fun clear() {
		deque.clear()
	}

	override fun isFull() = false

	override fun isEmpty() = deque.isEmpty()
}