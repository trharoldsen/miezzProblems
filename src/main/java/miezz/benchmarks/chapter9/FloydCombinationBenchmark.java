package miezz.benchmarks.chapter9;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class FloydCombinationBenchmark extends FloydCombinationBenchmarkKt {
	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
			.include(FloydCombinationBenchmark.class.getSimpleName())
			.forks(1)
			.build();
		new Runner(opt).run();
	}
}
