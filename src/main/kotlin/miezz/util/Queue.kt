package miezz.util

interface Queue<E> {
	fun enqueue(value: E): Boolean
	fun dequeue(): E
	fun peek(): E
	fun clear(): Unit
	fun isFull(): Boolean
	fun isEmpty(): Boolean
}

interface PriorityQueue<E> {
	fun enqueue(value: E): Boolean
	fun dequeue(): E
	fun peek(): E
	fun clear(): Unit
	fun isFull(): Boolean
	fun isEmpty(): Boolean
}

interface Deque<E> : Queue<E>, Stack<E> {
	fun insertFront(value: E): Boolean
	fun peekFront(): E
	fun removeFront(): E
	fun insertBack(value: E): Boolean
	fun peekBack(): E
	fun removeBack(): E
}

interface PriorityDeque<E> : PriorityQueue<E> {
	fun peekMin(): E = peek()
	fun removeMin(): E = dequeue()
	fun peekMax(): E
	fun removeMax(): E
}

