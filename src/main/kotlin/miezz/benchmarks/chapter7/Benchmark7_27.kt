package miezz.benchmarks.chapter7

import miezz.chapter7.*
import org.openjdk.jmh.annotations.*

@State(Scope.Thread)
open class Benchmark7_27Kt {
	open var N: Int = 60
	open var k: Int = 30

//	@Benchmark
//	@Measurement(iterations = 10)
//	@Warmup(iterations = 5)
//	fun recursive(): Long {
//		return Problem7_27Recursive().C(N, k)
//	}

	@Benchmark
	@Measurement(iterations = 10)
	@Warmup(iterations = 5)
	fun dynamic(): Long {
		return Problem7_27Dynamic().C(N, k)
	}

	@Benchmark
	@Measurement(iterations = 30)
	@Warmup(iterations = 5)
	fun dynamic_v2(): Long {
		return Problem7_27Dynamic_v2().C(N, k)
	}

	@Benchmark
	@Measurement(iterations = 30)
	@Warmup(iterations = 5)
	fun dynamic_v3(): Long {
		return Problem7_27Dynamic_v3().C(N, k)
	}

//	@Benchmark
//	@Measurement(iterations = 10)
//	@Warmup(iterations = 5)
//	fun recursiveUnrolled(): Long {
//		return Problem7_27RecursiveUnrolled().C(N, k)
//	}

//	@Benchmark
//	@Measurement(iterations = 10)
//	@Warmup(iterations = 5)
//	fun dynamicUnrolled(): Long {
//		return Problem7_27DynamicUnrolled().C(N, k)
//	}

//	@Benchmark
//	@Measurement(iterations = 10)
//	@Warmup(iterations = 5)
//	fun dynamicUnrolled_v2(): Long {
//		return Problem7_27DynamicUnrolled_v2().C(N, k)
//	}
}
