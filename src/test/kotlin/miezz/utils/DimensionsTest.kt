package miezz.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable

internal class DimensionsTest {
	@DisplayName("throws an IAE when constructed with a negative row dimensions")
	@Test
	fun testNegativeRowSize() {
		assertThrows<IAE>(IAE::class.java) { Dimensions(-1, 1) }
	}

	@DisplayName("throws an IAE when constructed with a negative column dimensions")
	@Test
	fun testNegativeColumnSize() {
		assertThrows<IAE>(IAE::class.java) { Dimensions(1, -1) }
	}

	@DisplayName("num elements should be width * height")
	@Test
	fun test_numElements() {
		val case1 = Pair(Dimensions(0, 0), 0)
		val case2 = Pair(Dimensions(1, 0), 0)
		val case3 = Pair(Dimensions(0, 1), 0)
		val case4 = Pair(Dimensions(3, 4), 12)
		val case5 = Pair(Dimensions(4, 3), 12)

		assertAll(
			Executable { assertEquals(case1.second, case1.first.numElements) },
			Executable { assertEquals(case2.second, case2.first.numElements) },
			Executable { assertEquals(case3.second, case3.first.numElements) },
			Executable { assertEquals(case4.second, case4.first.numElements) },
			Executable { assertEquals(case5.second, case5.first.numElements) }
		)
	}

//	@DisplayName("Test Dimensions.asRectangle")
//	@Test
//	fun test_asRectangle() {
//		val size = Dimensions(3, 4)
//		assertEquals(Rectangle(ZERO_INDEX, Index(3, 4)), size.asRectangle())
//	}
//
	@DisplayName("Test equals")
	@Test
	fun testEquals() {
		val size = Dimensions(3, 4)
		assertAll(
			Executable { assertEquals(Dimensions(3, 4), size) },
			Executable { assertEquals(Dimensions(3, 4).hashCode(), size.hashCode()) }
		)
	}

	@DisplayName("Test !equal")
	@Test
	fun testNotEquals() {
		val size = Dimensions(3, 4)
		assertAll(
			Executable { assertNotEquals(Dimensions(4, 3), size) },
			Executable { assertNotEquals(Dimensions(3, 3), size) },
			Executable { assertNotEquals(Dimensions(4, 4), size) }
		)
	}
}
