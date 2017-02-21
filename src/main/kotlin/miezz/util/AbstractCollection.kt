package miezz.util

interface BasicCollection<out E> {
	val size: Int
	operator fun contains(element: @UnsafeVariance E): Boolean
	fun isEmpty() = size == 0
}

interface BasicMutableCollection<E> : BasicCollection<E> {
	fun add(element: E): Boolean
	fun clear()
	fun remove(element: E): Boolean
}
