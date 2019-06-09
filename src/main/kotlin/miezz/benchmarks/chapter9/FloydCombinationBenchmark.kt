package miezz.benchmarks.chapter9

import miezz.chapter9.TailRecursiveFloyd
import miezz.chapter9.RecursiveFloyd
import miezz.chapter9.IterativeFloyd
import org.openjdk.jmh.annotations.*
import java.util.*

@State(Scope.Thread)
abstract class FloydCombinationBenchmarkKt {
	open val input = makeInput()

	@Benchmark
	@Measurement(iterations = 10)
	@Warmup(iterations = 10)
	fun TailRecursiveFloyd(): List<Int> {
		return TailRecursiveFloyd(Random(0xBAD_FEEL)).permutation(input, 1000)
	}

	@Benchmark
	@Measurement(iterations = 10)
	@Warmup(iterations = 10)
	fun RecursiveFloyd(): List<Int> {
		return RecursiveFloyd(Random(0xBAD_FEEL)).permutation(input, 1000)
	}

	@Benchmark
	@Measurement(iterations = 10)
	@Warmup(iterations = 10)
	fun IterativeFloyd(): List<Int> {
		return IterativeFloyd(Random(0xBAD_FEEL)).permutation(input, 1000)
	}

	companion object {
		fun makeInput(): List<Int> {
			val random = Random(0xBAD_FEEL)
			return IntArray(10000) { random.nextInt(100000) }.toList()
		}
	}
}
