package miezz.util

interface BasicList<out E> : BasicCollection<E> {
	fun get(index: Int): E
}

interface BasicMutableList<E> : BasicList<E>, BasicMutableCollection<E>{
	fun set(index: Int, element: E): E
}
