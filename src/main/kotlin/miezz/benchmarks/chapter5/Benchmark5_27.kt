package miezz.benchmarks.chapter5

import miezz.chapter5.Prob5_27
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Warmup

open class Benchmark5_27Kt {
	@Benchmark
	@Warmup(iterations = 5)
	@Measurement(iterations = 10)
	open fun testN100(): List<Int> {
		return Prob5_27().findPrimes(100)
	}

	@Benchmark
	@Warmup(iterations = 5)
	@Measurement(iterations = 10)
	open fun testN1000(): List<Int> {
		return Prob5_27().findPrimes(1000)
	}

	@Benchmark
	@Warmup(iterations = 5)
	@Measurement(iterations = 10)
	open fun testN10000(): List<Int> {
		return Prob5_27().findPrimes(10000)
	}

	@Benchmark
	@Warmup(iterations = 5)
	@Measurement(iterations = 10)
	open fun testN100000(): List<Int> {
		return Prob5_27().findPrimes(100000)
	}
}