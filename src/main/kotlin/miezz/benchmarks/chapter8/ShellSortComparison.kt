package miezz.benchmarks.chapter8

import miezz.chapter8.ShellSort
import org.openjdk.jmh.annotations.*
import java.util.*

@State(Scope.Thread)
open class ShellSortComparison {
	private val ARRAY_SIZE = 1000000
	private val originalArray = makeArray()
	private lateinit var input: IntArray

	fun makeArray(): IntArray {
		val tmp = ArrayList<Int>()
		val random = Random(0xBAD_DADL)
		for (i in 1..ARRAY_SIZE) {
			tmp += random.nextInt(ARRAY_SIZE )
		}
		return tmp.toIntArray()
	}

	@Setup(Level.Invocation)
	fun setup() {
		input = originalArray.clone()
	}

	@Benchmark
	@Measurement(iterations = 10)
	@Warmup(iterations = 5)
	fun ORIGINAL(): Int {
		ShellSort(ShellSort.ORIGINAL).sort(input)
		return input.sum()
	}

	@Benchmark
	@Measurement(iterations = 10)
	@Warmup(iterations = 5)
	fun ODDS_ONLY(): Int {
		ShellSort(ShellSort.ODDS_ONLY).sort(input)
		return input.sum()
	}

	@Benchmark
	@Measurement(iterations = 10)
	@Warmup(iterations = 5)
	fun GONNETS(): Int {
		ShellSort(ShellSort.GONNETS).sort(input)
		return input.sum()
	}

	@Benchmark
	@Measurement(iterations = 10)
	@Warmup(iterations = 5)
	fun HIBBARDS(): Int {
		ShellSort(ShellSort.HIBBARDS).sort(input)
		return input.sum()
	}

	@Benchmark
	@Measurement(iterations = 10)
	@Warmup(iterations = 5)
	fun KNUTHS(): Int {
		ShellSort(ShellSort.KNUTH).sort(input)
		return input.sum()
	}

	@Benchmark
	@Measurement(iterations = 10)
	@Warmup(iterations = 5)
	fun SEDGEWICKS(): Int {
		ShellSort(ShellSort.SEDGEWICK).sort(input)
		return input.sum()
	}
}





