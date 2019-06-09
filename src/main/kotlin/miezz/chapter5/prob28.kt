package miezz.chapter5

class Prob5_28 {
	fun find(): Quint? {
		val toFive = buildToFives()
		val it = combinations().iterator()
		while (it.hasNext()) {
			val next = it.next()
			val sum = next.map { toFive[it] }.sum()
			val result = toFive.binarySearch(sum)
			if (result >= 0)
				return next
		}
		return null
	}

	fun buildToFives(): LongArray {
		return (0..75).map { (it * it).toLong() * (it * it * it) }.toLongArray()
	}

	private fun combinations(): Sequence<Quint> {
		return (1..74).asSequence()
			.map { QuintBuilder(it) }
			.flatMap { q -> (q.a!!..74).asSequence().map { q.copy(b = it) } }
			.flatMap { q -> (q.b!!..74).asSequence().map { q.copy(c = it) } }
			.flatMap { q -> (q.c!!..74).asSequence().map { q.copy(d = it) } }
			.flatMap { q -> (q.d!!..74).asSequence().map { q.copy(e = it) } }
			.map { it.build() }
	}
}

data class Quint(
	val a: Int, val b: Int, val c: Int,
	val d: Int, val e: Int
) : Iterable<Int> {
	override fun iterator(): Iterator<Int> {
		return listOf(a, b, c, d, e).iterator()
	}
}

private data class QuintBuilder(
	val a: Int? = null, val b: Int? = null,
	val c: Int? = null, val d: Int? = null,
	val e: Int? = null
) {
	fun build(): Quint = Quint(a!!, b!!, c!!, d!!, e!!)
}

fun main() {
	readLine()
	println("solution is: " + Prob5_28().find())
}



