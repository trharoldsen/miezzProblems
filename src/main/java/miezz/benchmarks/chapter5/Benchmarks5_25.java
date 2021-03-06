package miezz.benchmarks.chapter5;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class Benchmarks5_25 extends Benchmarks5_25Kt {
	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
			.include(Benchmarks5_25.class.getSimpleName())
			.forks(1)
			.build();
		new Runner(opt).run();
	}
}
