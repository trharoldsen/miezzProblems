package miezz.utils

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.function.Executable

class IndexTest {
	@DisplayName("Negative index row and column are valid")
	@Test
	fun testNegativeIndex() {
		Index(-1, -1)
	}

	@DisplayName("Adding offset to index should return shifted index")
	@Test
	fun testAddOffset() {
		val index = Index(1, 2)
		val offset = Offset(4, 1)
		assertEquals(Index(5, 3), index + offset)
	}

	@DisplayName("Subtracting indices should return offset from second to first")
	@Test
	fun testMinusIndex() {
		val index1 = Index(6,4)
		val index2 = Index(1, 5)
		assertEquals(Offset(5, -1), index1 - index2)
		assertEquals(Offset(-5, 1), index2 - index1)
	}

	@DisplayName("Equivalent indices should be equal")
	@Test
	fun testEquals() {
		val index1 = Index(2, 3)
		val index2 = Index(2, 3)
		assertAll(
			Executable { assertEquals(index1, index2) },
			Executable { assertEquals(index1.hashCode(), index2.hashCode()) }
		)
	}

	@DisplayName("Unequal indices should not be equal")
	@Test
	fun testNotEquals() {
		val index1 = Index(2, 3)
		val index2 = Index(3, 2)
		assertNotEquals(index1, index2)
	}
}
