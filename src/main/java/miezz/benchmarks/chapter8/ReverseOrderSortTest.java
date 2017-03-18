package miezz.benchmarks.chapter8;

import org.jetbrains.annotations.NotNull;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class ReverseOrderSortTest extends SortComparison {

	private static final int ARRAY_SIZE = 10000000;

	@NotNull
	@Override
	protected int[] makeArray() {
		int[] array = new int[ARRAY_SIZE];
		for (int i = 0; i < ARRAY_SIZE; i++)
			array[i] = 10000000 - i;
		return array;
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
			.include(ReverseOrderSortTest.class.getSimpleName())
			.forks(1)
			.build();
		new Runner(opt).run();
	}
}
