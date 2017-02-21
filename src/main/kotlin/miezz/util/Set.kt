package miezz.util

interface BasicSet<out E>: BasicCollection<E>

interface BasicMutableSet<E> : BasicMutableCollection<E>, BasicSet<E>

interface SortedSet<out E>: BasicSet<E> {
	val first: E
	val last: E
}

interface SortedMutableSet<E>: SortedSet<E>, BasicMutableSet<E>
