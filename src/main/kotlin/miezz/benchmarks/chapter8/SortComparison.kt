package miezz.benchmarks.chapter8

import miezz.chapter8.MergeSort
import miezz.chapter8.QuickSort
import miezz.chapter8.ShellSort
import org.openjdk.jmh.annotations.*

@State(Scope.Thread)
abstract class SortComparison {
	protected abstract fun makeArray(): IntArray
	private lateinit var input: IntArray

	@Setup(Level.Invocation)
	fun setup() {
		input = makeArray()
	}

	@Benchmark
	@Measurement(iterations = 5)
	@Warmup(iterations = 5)
	fun ShellSortGonnets(): Int {
		ShellSort(ShellSort.GONNETS).sort(input)
		return input.sum()
	}

	@Benchmark
	@Measurement(iterations = 5)
	@Warmup(iterations = 5)
	fun MergeSort4(): Int {
		MergeSort(4).sort(input)
		return input.sum()
	}

	@Benchmark
	@Measurement(iterations = 5)
	@Warmup(iterations = 5)
	fun MergeSort8(): Int {
		MergeSort(8).sort(input)
		return input.sum()
	}

	@Benchmark
	@Measurement(iterations = 5)
	@Warmup(iterations = 5)
	fun MergeSort16(): Int {
		MergeSort(16).sort(input)
		return input.sum()
	}

	@Benchmark
	@Measurement(iterations = 5)
	@Warmup(iterations = 5)
	fun MergeSort32(): Int {
		MergeSort(32).sort(input)
		return input.sum()
	}

	@Benchmark
	@Measurement(iterations = 5)
	@Warmup(iterations = 5)
	fun MergeSort64(): Int {
		MergeSort(64).sort(input)
		return input.sum()
	}

	@Benchmark
	@Measurement(iterations = 5)
	@Warmup(iterations = 5)
	fun MergeSort128(): Int {
		MergeSort(128).sort(input)
		return input.sum()
	}

	@Benchmark
	@Measurement(iterations = 5)
	@Warmup(iterations = 5)
	fun MergeSort256(): Int {
		MergeSort(256).sort(input)
		return input.sum()
	}

	@Benchmark
	@Measurement(iterations = 5)
	@Warmup(iterations = 5)
	fun QuickSort4(): Int {
		QuickSort(4).sort(input)
		return input.sum()
	}

	@Benchmark
	@Measurement(iterations = 5)
	@Warmup(iterations = 5)
	fun QuickSort8(): Int {
		QuickSort(8).sort(input)
		return input.sum()
	}

	@Benchmark
	@Measurement(iterations = 5)
	@Warmup(iterations = 5)
	fun QuickSort16(): Int {
		QuickSort(16).sort(input)
		return input.sum()
	}

	@Benchmark
	@Measurement(iterations = 5)
	@Warmup(iterations = 5)
	fun QuickSort32(): Int {
		QuickSort(32).sort(input)
		return input.sum()
	}

	@Benchmark
	@Measurement(iterations = 5)
	@Warmup(iterations = 5)
	fun QuickSort64(): Int {
		QuickSort(64).sort(input)
		return input.sum()
	}

	@Benchmark
	@Measurement(iterations = 5)
	@Warmup(iterations = 5)
	fun QuickSort128(): Int {
		QuickSort(128).sort(input)
		return input.sum()
	}
}
