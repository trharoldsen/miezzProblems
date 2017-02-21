package miezz.chapter5

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class Prob5_27Tests {
	@Test fun sized1() {
		val p = Prob5_27()
		Assertions.assertEquals(listOf<Int>(), p.findPrimes(1))
	}

	@Test fun sized0() {
		val p = Prob5_27()
		Assertions.assertEquals(listOf<Int>(), p.findPrimes(0))
	}

	@Test fun sized2() {
		val p = Prob5_27()
		Assertions.assertEquals(listOf<Int>(), p.findPrimes(2))
	}

	@Test fun sized3() {
		val p = Prob5_27()
		Assertions.assertEquals(listOf(2), p.findPrimes(3))
	}

	@Test fun sized18() {
		val p = Prob5_27()
		Assertions.assertEquals(listOf(2, 3, 5, 7, 11, 13, 17), p.findPrimes(18))
	}

}