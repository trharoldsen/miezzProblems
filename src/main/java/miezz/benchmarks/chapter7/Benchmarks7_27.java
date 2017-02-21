package miezz.benchmarks.chapter7;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class Benchmarks7_27 extends Benchmark7_27Kt {
	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
			.include(Benchmarks7_27.class.getSimpleName())
			.forks(1)
			.build();
		new Runner(opt).run();
	}
}
