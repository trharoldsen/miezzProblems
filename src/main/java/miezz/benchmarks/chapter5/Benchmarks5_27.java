package miezz.benchmarks.chapter5;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class Benchmarks5_27 extends Benchmark5_27Kt {
	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
			.include(Benchmarks5_27.class.getSimpleName())
			.forks(1)
			.build();
		new Runner(opt).run();
	}
}
