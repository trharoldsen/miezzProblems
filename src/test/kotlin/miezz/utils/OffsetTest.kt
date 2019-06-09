package miezz.utils

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.function.Executable

internal class OffsetTest {
	@DisplayName("Addition should return index shifted by offset")
	@Test
	fun testPlusIndex() {
		val offset = Offset(2, 3)
		val index = Index(1, 3)
		assertEquals(Index(3, 6), offset + index)
	}

	@DisplayName("Test adding offsets")
	@Test
	fun testPlusOffset() {
		val offset1 = Offset(2, 3)
		val offset2 = Offset(1, 3)
		assertEquals(Offset(3, 6), offset1 + offset2)
	}

	@DisplayName("Test subtracting offsets")
	@Test
	fun minus() {
		val offset1 = Offset(2, 3)
		val offset2 = Offset(1, 4)
		assertEquals(Offset(1, -1), offset1 - offset2)
	}

	@DisplayName("Multiplying returns scaled value")
	@Test
	fun times() {
		val offset = Offset(2, 4)
		assertEquals(Offset(6, 12), offset * 3)
	}

	@DisplayName("Test Equals")
	@Test
	fun equals() {
		val offset1 = Offset(2, 3)
		val offset2 = Offset(2, 3)
		assertAll(
			Executable { assertEquals(offset2, offset1)},
			Executable { assertEquals(offset2.hashCode(), offset1.hashCode())}
		)
	}

	@DisplayName("Test Not Equals")
	@Test
	fun notEquals() {
		val offset1 = Offset(2, 3)
		val offset2 = Offset(3, 2)
		assertNotEquals(offset2, offset1)
	}
}