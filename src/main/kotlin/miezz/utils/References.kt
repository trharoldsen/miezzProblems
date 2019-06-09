package miezz.utils

class ObjectRef<T>(var value: T) {
	fun get() = value
	fun set(newValue: T) {
		value = newValue
	}
}
