package miezz.chapter7

import miezz.chapter7.Problem7_27DynamicUnrolled_v2.Operation.Type.*
import miezz.util.ArrayDequeStack
import miezz.util.Stack
import miezz.utils.ArrayMatrix
import miezz.utils.Matrix
import miezz.utils.MutableMatrix
import miezz.utils.Size

interface Problem7_27 {
	fun C(N: Int, k: Int): Long
}

class Problem7_27Recursive : Problem7_27 {
	override fun C(N: Int, k: Int): Long {
		return when(k) {
			0, N -> 1
			else -> C(N-1, k) + C(N-1, k-1)
		}
	}
}

class Problem7_27Dynamic : Problem7_27 {
	val cache = HashMap<Pair<Int, Int>, Long>()

	override fun C(N: Int, k: Int): Long {
		return when (k) {
			0, N -> 1
			else -> cache.computeIfAbsent(Pair(N, k)) { C(N-1, k) + C(N-1, k-1) }
		}
	}
}

class Problem7_27Dynamic_v2 : Problem7_27 {
	lateinit var cache: MutableMatrix<Long>
	data class Subproblem(val N: Int, val k: Int)

	override fun C(N: Int, k: Int): Long {
		cache = ArrayMatrix(Size(N+1, k+1)) { -1L }
		return C_recurse(Subproblem(N, k))
	}

	private fun C_recurse(p: Subproblem): Long {
		val (N, k) = p
		return when (k) {
			0, N -> 1
			else -> {
				val existing = cache[N, k]
				if (existing == -1L) {
					val v = C_recurse(Subproblem(N-1, k)) + C_recurse(Subproblem(N-1, k-1))
					cache[N, k] = v
					v  /* <- */
				} else {
					existing
				}
			}
		}
	}
}

class Problem7_27Dynamic_v3 : Problem7_27 {
	lateinit var cache: MutableMatrix<Long>
	data class Subproblem(val N: Int, val k: Int)

	override fun C(N: Int, k: Int): Long {
		cache = ArrayMatrix(Size(N+1, k+1)) {
			if (it.column == 0 || it.column == it.row) 1L else -1L
		}
		return C_recurse(Subproblem(N, k))
	}

	private fun C_recurse(p: Subproblem): Long {
		val (N, k) = p
		val existing = cache[N, k]
		return if (existing == -1L) {
			val v = C_recurse(Subproblem(N-1, k)) + C_recurse(Subproblem(N-1, k-1))
			cache[N, k] = v
			v  /* <- */
		} else {
			existing
		}
	}
}

class Problem7_27RecursiveUnrolled : Problem7_27 {
	override fun C(N: Int, k: Int): Long {
		val opStack = ArrayDequeStack<() -> Unit>()
		val valueStack = ArrayDequeStack<Long>()

		fun makeComputation(N: Int, k: Int): () -> Unit {
			return {
				when(k) {
					0, N -> valueStack.push(1)
					else -> {
						opStack.push {
							val sum = valueStack.pop() + valueStack.pop()
							valueStack.push(sum)
						}
						opStack.push(makeComputation(N-1, k-1))
						opStack.push(makeComputation(N-1, k))
					}
				}
			}
		}

		opStack.push(makeComputation(N, k))
		while (opStack.isNotEmpty()) {
			opStack.pop().invoke()
		}
		return valueStack.pop()
	}
}

class Problem7_27DynamicUnrolled : Problem7_27 {
	val cache = HashMap<Pair<Int, Int>, Long>()

	override fun C(N: Int, k: Int): Long {
		val opStack = ArrayDequeStack<() -> Unit>()
		val valueStack = ArrayDequeStack<Long>()

		fun makeComputation(N: Int, k: Int): () -> Unit {
			return {
				when {
					Pair(N, k) in cache -> valueStack.push(cache[Pair(N, k)]!!)
					k == 0 || k == N -> valueStack.push(1)
					else -> {
						opStack.push {
							val sum = valueStack.pop() + valueStack.pop()
							valueStack.push(sum)
							cache[Pair(N, k)] = sum
						}
						opStack.push(makeComputation(N-1, k-1))
						opStack.push(makeComputation(N-1, k))
					}
				}
			}
		}

		opStack.push(makeComputation(N, k))
		while (opStack.isNotEmpty()) {
			opStack.pop().invoke()
		}
		return valueStack.pop()
	}
}

class Problem7_27DynamicUnrolled_v2 : Problem7_27 {
	lateinit var cache: MutableMatrix<Long>

	class State(N: Int) {
		val opStack: Stack<Operation> = ArrayDequeStack(N * 2)
		val valueStack: Stack<Long> = ArrayDequeStack(N * 2)

		operator fun component1(): Stack<Operation> = opStack
		operator fun component2(): Stack<Long> = valueStack
	}

	data class Subproblem(val N: Int, val k: Int)

	class Operation(val type: Type, val subproblem: Subproblem) {
		enum class Type {
			COMPUTE,
			SUM
		}
	}

	override fun C(N: Int, k: Int): Long {
		cache = ArrayMatrix(Size(N+1, k+1)) { -1L }
		val state = State(N)
		val (opStack, valueStack) = state
		opStack.push(Operation(COMPUTE, Subproblem(N, k)))
		while (opStack.isNotEmpty()) {
			val op = opStack.pop()
			val (N, k) = op.subproblem
			when (op.type) {
				COMPUTE -> {
					when {
						cache[N, k] != -1L ->
							valueStack.push(cache[N, k])
						k == 0 || k == N -> valueStack.push(1)
						else -> {
							opStack.push(Operation(SUM, op.subproblem))
							opStack.push(Operation(COMPUTE, Subproblem(N-1, k-1)))
							opStack.push(Operation(COMPUTE, Subproblem(N-1, k)))
						}
					}
				}
				SUM -> {
					val sum = valueStack.pop() + valueStack.pop()
					valueStack.push(sum)
					cache[N, k] = sum
				}
			}
		}
		return valueStack.pop()
	}
}

fun main(args: Array<String>) {
	listOf(5, 10, 15, 20, 30).asSequence().flatMap { N ->
		listOf(5, 10, 15, 20, 30).asSequence().map { k -> Pair(N, k) }
	}   .filter { it.first >= it.second }
		.forEach {
			println(it)
			assert(Problem7_27Dynamic().C(it.first, it.second) ==
				Problem7_27Dynamic_v3().C(it.first, it.second))
		}
}
