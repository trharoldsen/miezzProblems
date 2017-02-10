package miezz.utils

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.function.Executable

internal class RectangleTest {

	@DisplayName("BottomRight index must be right and below TopLeft index")
	@Test
	fun testIndexRanges() {
		val case1 = Pair(Index(2, 3), Index(1, 4))
		val case2 = Pair(Index(2, 3), Index(4, 2))

		assertAll(
			Executable { assertThrows<IAE>(IAE::class.java, {Rectangle(case1.first, case1.second) }) },
			Executable { assertThrows<IAE>(IAE::class.java, {Rectangle(case2.first, case2.second) }) }
		)
	}

	@DisplayName("Rectangle may have zero height and width")
	@Test
	fun testEmptyRectangles() {
		val case1 = Pair(Index(2, 3), Index(2, 3))
		val case2 = Pair(Index(2, 3), Index(4, 3))
		val case3 = Pair(Index(2, 3), Index(2, 4))

		assertAll(
			Executable { Rectangle(case1.first, case1.second) },
			Executable { Rectangle(case2.first, case2.second) },
			Executable { Rectangle(case3.first, case3.second) }
		)
	}

	@DisplayName("Size should be (left-right)x(top-bottom)")
	@Test
	fun testSize() {
		val rect = Rectangle(Index(1, 2), Index(7, 9))
		assertEquals(Size(6, 7), rect.size)
	}

	@DisplayName("Rectangle should contains points inside")
	@Test
	fun testContains() {
		val rectangle = Rectangle(Index(1, 1), Index(3, 4))
		assertAll(
			Executable { assertTrue { Index(1, 1) in rectangle } },
			Executable { assertTrue { Index(2, 1) in rectangle } },
			Executable { assertTrue { Index(1, 3) in rectangle } },
			Executable { assertTrue { Index(2, 3) in rectangle } },
			Executable { assertTrue { Index(1, 2) in rectangle } },
			Executable { assertTrue { Index(2, 2) in rectangle } }
		)
	}

	@DisplayName("Rectangle should contains points outside")
	@Test
	fun testNotContains() {
		val rectangle = Rectangle(Index(1, 1), Index(3, 4))
		assertAll(
			Executable { assertFalse { Index(0, 1) in rectangle } },
			Executable { assertFalse { Index(0, 3) in rectangle } },
			Executable { assertFalse { Index(1, 0) in rectangle } },
			Executable { assertFalse { Index(1, 4) in rectangle } },
			Executable { assertFalse { Index(2, 0) in rectangle } },
			Executable { assertFalse { Index(2, 4) in rectangle } },
			Executable { assertFalse { Index(3, 1) in rectangle } },
			Executable { assertFalse { Index(3, 3) in rectangle } },
			Executable { assertFalse { Index(2, 4) in rectangle } }
		)
	}

	@DisplayName("Equals if bounds are identical")
	@Test
	fun testEquals() {
		val r1 = Rectangle(Index(1, 1), Index(2, 3))
		val r2 = Rectangle(Index(1, 1), Index(2, 3))
		assertAll(
			Executable { assertEquals(r2, r1) },
			Executable { assertEquals(r2.hashCode(), r1.hashCode()) }
		)
	}

	@DisplayName("Test not equals")
	@Test
	fun testNotEquals() {
		val r1 = Rectangle(Index(1, 1), Index(2, 3))
		val r2 = Rectangle(Index(1, 1), Index(2, 4))
		val r3 = Rectangle(Index(1, 0), Index(2, 3))

		assertAll(
			Executable { assertNotEquals(r2, r1) },
			Executable { assertNotEquals(r3, r1) }
		)
	}
}