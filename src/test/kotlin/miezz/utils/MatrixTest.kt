package miezz.utils

import kotlinx.support.jdk8.streams.asStream
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Assumptions.assumingThat
import org.junit.jupiter.api.function.Executable

private val indexInit: (Index) -> Index = { it }

interface MatrixTests {
	fun makeMatrix(dimensions: Dimensions, init: (Index) -> Index): Matrix<Index>

	@DisplayName("Zero sized matrices are allowed and preserved")
	@Test
	fun testZeroSize() {
		assertAll(
			Executable { assertEquals(Dimensions(0, 0), makeMatrix(Dimensions(0, 0), indexInit).dimensions) },
			Executable { assertEquals(Dimensions(0, 4), makeMatrix(Dimensions(0, 4), indexInit).dimensions) },
			Executable { assertEquals(Dimensions(4, 0), makeMatrix(Dimensions(4, 0), indexInit).dimensions) }
		)
	}

	@DisplayName("Dimensions of matrix is same as provided")
	@Test
	fun testMatrixSize() {
		val matrix = makeMatrix(Dimensions(2, 3), indexInit)
		assertEquals(Dimensions(2, 3), matrix.dimensions)
	}

	@DisplayName("Matrix rectangle indices are (0, 0) x dimensions")
	@Test
	fun testMatrixRectangleStart() {
		val rectangle = makeMatrix(Dimensions(3, 4), indexInit).rectangle
		assertAll(
			Executable { assertEquals(ZERO_INDEX, rectangle.topLeft) },
			Executable { assertEquals(Index(3, 4), rectangle.bottomRight) }
		)
	}

	@DisplayName("get returns expected value")
	@Test
	fun testGet() {
		val matrix = makeMatrix(Dimensions(2, 2), indexInit)
		assertAll(
			Executable { assertEquals(Index(0, 0), matrix[Index(0, 0)]) },
			Executable { assertEquals(Index(0, 1), matrix[Index(0, 1)]) },
			Executable { assertEquals(Index(1, 0), matrix[Index(1, 0)]) },
			Executable { assertEquals(Index(1, 1), matrix[Index(1, 1)]) },
			Executable { assertEquals(Index(0, 0), matrix[0, 0]) },
			Executable { assertEquals(Index(0, 1), matrix[0, 1]) },
			Executable { assertEquals(Index(1, 0), matrix[1, 0]) },
			Executable { assertEquals(Index(1, 1), matrix[1, 1]) }
		)
	}

	@DisplayName("Rectangle outside parent matrix causes IOB exception")
	@Test
	fun submatrix_invalidRectangleConstruct() {
		val matrix = makeMatrix(Dimensions(2, 3), indexInit)
		val submatrix = { tl: Index, br: Index -> matrix.submatrix(Rectangle(tl, br)) }
		assertAll(
			Executable { assertThrows<IOB>(IOB::class.java, { submatrix(Index(-1, 0), Index(2, 3)) }) },
			Executable { assertThrows<IOB>(IOB::class.java, { submatrix(Index(0, -1), Index(2, 3)) }) },
			Executable { assertThrows<IOB>(IOB::class.java, { submatrix(Index(0, 0), Index(3, 3)) }) },
			Executable { assertThrows<IOB>(IOB::class.java, { submatrix(Index(0, 0), Index(2, 4)) }) },
			Executable { submatrix(Index(0, 0), Index(2, 3)) }
		)
	}

	@DisplayName("Can make a zero bounds submatrix")
	@Test
	fun submatrix_zeroBounds() {
		val matrix = makeMatrix(Dimensions(2, 2), indexInit)
		val submatrix = matrix.submatrix(Rectangle(Index(0, 0), Index(0, 0)))
		assertEquals(Dimensions(0, 0), submatrix.dimensions)
	}

	@DisplayName("Can submatrix a zero-sized matrix")
	@Test
	fun submatrix_ofZeroBoundsMatrix() {
		val submatrix = { matrix: Matrix<*> -> matrix.submatrix(Rectangle(ZERO_INDEX, ZERO_INDEX))}

		assertAll(
			Executable { assertEquals(Dimensions(0, 0), submatrix(makeMatrix(Dimensions(0, 0), indexInit)).dimensions) },
			Executable { assertEquals(Dimensions(0, 0), submatrix(makeMatrix(Dimensions(0, 2), indexInit)).dimensions) },
			Executable { assertEquals(Dimensions(0, 0), submatrix(makeMatrix(Dimensions(2, 0), indexInit)).dimensions) }
		)
	}

	@DisplayName("rectangle indices of submatrix should be relative to parent")
	@Test
	fun submatrix_absoluteRectangleIndices() {
		val matrix = makeMatrix(Dimensions(3, 4), indexInit)
		val submatrix = matrix.submatrix(Rectangle(Index(1, 1), Index(3, 4)))
		assertEquals(Rectangle(Index(1, 1), Index(3, 4)), submatrix.rectangle)
	}

	@DisplayName("rectangle indices of submatrix should be relative to submatrix")
	@Test
	fun submatrix_rectangleIndices() {
		val matrix = makeMatrix(Dimensions(3, 4), indexInit)
		val submatrix = matrix.submatrix(Rectangle(Index(1, 1), Index(3, 4)))
		assertEquals(Rectangle(Index(0, 0), Index(2, 3)), submatrix.rectangle)
	}

	@DisplayName("Test dimensions field of submatrix")
	@Test
	fun submatrix_testSize() {
		val matrix = makeMatrix(Dimensions(3, 4), indexInit)
		val submatrix = matrix.submatrix(Rectangle(Index(1, 1), Index(3, 4)))
		assertEquals(Dimensions(2, 3), submatrix.dimensions)
	}

	@DisplayName("Get on submatrix returns expected value")
	@Test
	fun submatrix_testGet() {
		val matrix = makeMatrix(Dimensions(3, 4), indexInit)
		val submatrix = matrix.submatrix(Rectangle(Index(1, 1), Index(3, 3)))
		assertAll(
			Executable { assertEquals(Index(1, 1), submatrix[Index(0, 0)]) },
			Executable { assertEquals(Index(1, 2), submatrix[Index(0, 1)]) },
			Executable { assertEquals(Index(2, 1), submatrix[Index(1, 0)]) },
			Executable { assertEquals(Index(2, 2), submatrix[Index(1, 1)]) },
			Executable { assertEquals(Index(1, 1), submatrix[0, 0]) },
			Executable { assertEquals(Index(1, 2), submatrix[0, 1]) },
			Executable { assertEquals(Index(2, 1), submatrix[1, 0]) },
			Executable { assertEquals(Index(2, 2), submatrix[1, 1]) }
		)
	}

	@DisplayName("Get on submatrix reflects parent matrix")
	@Test
	fun submatrix_getReflectsParent() {
		val matrix = makeMatrix(Dimensions(1, 1), indexInit)
		if (matrix is MutableMatrix<Index>) {
			val submatrix = matrix.submatrix(Rectangle(ZERO_INDEX, Index(1, 1)))
			matrix[0, 0] = Index(1, 1)
			assertEquals(Index(1, 1), submatrix[0, 0])
		}
	}

	@DisplayName("When extended bounds is true, submatrix get allows indexing outside submatrix")
	@Test
	fun submatrix_getExtendedBounds() {
		val matrix = makeMatrix(Dimensions(3, 3), indexInit)
		val submatrix = matrix.submatrix(Rectangle(Index(1, 1), Index(2, 2)), true)
		assertAll(
			Executable { assertEquals(Index(0, 0), submatrix[-1, -1]) },
			Executable { assertEquals(Index(0, 1), submatrix[-1, 0]) },
			Executable { assertEquals(Index(1, 0), submatrix[0, -1]) },
			Executable { assertEquals(Index(2, 2), submatrix[1, 1]) },
			Executable { assertEquals(Index(2, 1), submatrix[1, 0]) },
			Executable { assertEquals(Index(1, 2), submatrix[0, 1]) }
		)
	}

	@DisplayName("Get on boundsExtended submatrix cause IOB exception when outside parent")
	@Test
	fun submatrix_getExtendedBoundsIllegal() {
		val matrix = makeMatrix(Dimensions(3, 3), indexInit)
		val submatrix = matrix.submatrix(Rectangle(Index(1, 1), Index(2, 2)), true)
		assertAll(
			Executable { assertThrows<IOB>(IOB::class.java, { submatrix[-2, 0] }) },
			Executable { assertThrows<IOB>(IOB::class.java, { submatrix[0, -2] }) },
			Executable { assertThrows<IOB>(IOB::class.java, { submatrix[2, 0] }) },
			Executable { assertThrows<IOB>(IOB::class.java, { submatrix[0, 2] }) }
		)
	}

	@DisplayName("Get on non-BoundsExtended submatrix cause IOB when outside submatrix bounds")
	@Test
	fun submatrix_getNonExtendedBoundsIllegalIndex() {
		val matrix = makeMatrix(Dimensions(3, 3), indexInit)
		val submatrix = matrix.submatrix(Rectangle(Index(1, 1), Index(2, 2)), false)
		assertAll(
			Executable { assertThrows<IOB>(IOB::class.java, { submatrix[-1, -1] }) },
			Executable { assertThrows<IOB>(IOB::class.java, { submatrix[-1, 0] }) },
			Executable { assertThrows<IOB>(IOB::class.java, { submatrix[0, -1] }) },
			Executable { assertThrows<IOB>(IOB::class.java, { submatrix[1, 1] }) },
			Executable { assertThrows<IOB>(IOB::class.java, { submatrix[1, 0] }) },
			Executable { assertThrows<IOB>(IOB::class.java, { submatrix[0, 1] }) }
		)
	}

	@DisplayName("Rectangle outside parent submatrix causes IOB exception")
	@Test
	fun subsubmatrix_invalidRectangleConstruct() {
		val matrix = makeMatrix(Dimensions(4, 5), indexInit)
		val submatrix = matrix.submatrix(Rectangle(Index(1, 1), Index(3, 4)))
		val subsubmatrix = { tl: Index, br: Index -> submatrix.submatrix(Rectangle(tl, br), true) }
		assertAll(
			Executable { assertThrows<IOB>(IOB::class.java, { subsubmatrix(Index(-1, 0), Index(1, 2)) }) },
			Executable { assertThrows<IOB>(IOB::class.java, { subsubmatrix(Index(0, -1), Index(1, 2)) }) },
			Executable { assertThrows<IOB>(IOB::class.java, { subsubmatrix(Index(0, 0), Index(3, 3)) }) },
			Executable { assertThrows<IOB>(IOB::class.java, { subsubmatrix(Index(0, 0), Index(2, 4)) }) },
			Executable { subsubmatrix(Index(0, 0), Index(2, 3)) }
		)
	}

	@DisplayName("Can make a zero bounds subsubmatrix")
	@Test
	fun subsubmatrix_zeroBounds() {
		val matrix = makeMatrix(Dimensions(2, 2), indexInit)
		val submatrix = matrix.submatrix(Rectangle(Index(0, 0), Index(1, 1)))
		val subsubmatrix = submatrix.submatrix(Rectangle(Index(0, 0), Index(0, 0)))
		assertEquals(Dimensions(0, 0), subsubmatrix.dimensions)
	}

	@DisplayName("Can submatrix a zero-sized submatrix")
	@Test
	fun subsubmatrix_ofZeroBoundsMatrix() {
		val matrix = makeMatrix(Dimensions(4, 4), indexInit)
		val makeSubmatrix = { dimensions: Dimensions ->
			val rectangle = Rectangle(ZERO_INDEX, Index(dimensions.height, dimensions.width))
			matrix.submatrix(rectangle)
		}
		val submatrix = { submatrix: Matrix<*> ->
			val rectangle = Rectangle(ZERO_INDEX, ZERO_INDEX)
			submatrix.submatrix(rectangle)
		}

		assertAll(
			Executable { assertEquals(Dimensions(0, 0), submatrix(makeSubmatrix(Dimensions(1, 1))).dimensions) },
			Executable { assertEquals(Dimensions(0, 0), submatrix(makeSubmatrix(Dimensions(0, 2))).dimensions) },
			Executable { assertEquals(Dimensions(0, 0), submatrix(makeSubmatrix(Dimensions(2, 0))).dimensions) }
		)
	}

	@DisplayName("rectangle indices of subsubmatrix should be relative to topmost parent")
	@Test
	fun subsubmatrix_absoluteRectangleIndices() {
		val matrix = makeMatrix(Dimensions(3, 4), indexInit)
		val submatrix = matrix.submatrix(Rectangle(Index(1, 1), Index(3, 4)))
		val subsubmatrix = submatrix.submatrix(Rectangle(Index(1, 1), Index(2, 3)))
		assertEquals(Rectangle(Index(2, 2), Index(3, 4)), subsubmatrix.rectangle)
	}

	@DisplayName("rectangle indices of subsubmatrix should be relative to subsubmatrix")
	@Test
	fun subsubmatrix_rectangleIndices() {
		val matrix = makeMatrix(Dimensions(3, 4), indexInit)
		val submatrix = matrix.submatrix(Rectangle(Index(1, 1), Index(3, 4)))
		val subsubmatrix = submatrix.submatrix(Rectangle(Index(1, 1), Index(2, 3)))
		assertEquals(Rectangle(Index(0, 0), Index(1, 2)), subsubmatrix.rectangle)
	}

	@DisplayName("Test dimensions field of subsubmatrix")
	@Test
	fun subsubmatrix_testSize() {
		val matrix = makeMatrix(Dimensions(3, 4), indexInit)
		val submatrix = matrix.submatrix(Rectangle(Index(1, 1), Index(3, 4)))
		val subsubmatrix = submatrix.submatrix(Rectangle(Index(1, 1), Index(2, 3)))
		assertEquals(Dimensions(1, 2), subsubmatrix.dimensions)
	}

	@DisplayName("Get on subsubmatrix returns expected value")
	@Test
	fun subsubmatrix_testGet() {
		val matrix = makeMatrix(Dimensions(3, 4), indexInit)
		val submatrix = matrix.submatrix(Rectangle(Index(1, 1), Index(3, 3)))
		val subsubmatrix = submatrix.submatrix(Rectangle(Index(1, 1), Index(2, 2)))
		assertAll(
			Executable { assertEquals(Index(2, 2), subsubmatrix[0, 0]) }
		)
	}

	@DisplayName("Get on subsubmatrix reflects parent matrix")
	@Test
	fun subsubmatrix_getReflectsParent() {
		val matrix = makeMatrix(Dimensions(1, 1), indexInit)
		if (matrix is MutableMatrix<Index>) {
			val submatrix = matrix.submatrix(Rectangle(ZERO_INDEX, Index(1, 1)))
			val subsubmatrix = submatrix.submatrix(Rectangle(ZERO_INDEX, Index(1, 1)))
			matrix[0, 0] = Index(1, 1)
			assertEquals(Index(1, 1), subsubmatrix[0, 0])
		}
	}

	@DisplayName("When extended bounds is true, submatrix get allows indexing to topmost matrix")
	@Test
	fun subsubmatrix_getExtendedBounds() {
		val matrix = makeMatrix(Dimensions(4, 4), indexInit)
		val submatrix = matrix.submatrix(Rectangle(Index(1, 1), Index(3, 3)), true)
		val subsubmatrix = submatrix.submatrix(Rectangle(Index(1, 1), Index(2, 2)), true)
		assertAll(
			Executable { assertEquals(Index(0, 0), subsubmatrix[-2, -2]) },
			Executable { assertEquals(Index(0, 2), subsubmatrix[-2, 0]) },
			Executable { assertEquals(Index(2, 0), subsubmatrix[0, -2]) },
			Executable { assertEquals(Index(3, 3), subsubmatrix[1, 1]) },
			Executable { assertEquals(Index(3, 2), subsubmatrix[1, 0]) },
			Executable { assertEquals(Index(2, 3), subsubmatrix[0, 1]) }
		)
	}

	@DisplayName("Get on boundsExtended submatrix cause IOB exception when outside top most parent")
	@Test
	fun subsubmatrix_getExtendedBoundsIllegal() {
		val matrix = makeMatrix(Dimensions(4, 4), indexInit)
		val submatrix = matrix.submatrix(Rectangle(Index(1, 1), Index(3, 3)), true)
		val subsubmatrix = submatrix.submatrix(Rectangle(Index(1, 1), Index(2, 2)), true)
		assertAll(
			Executable { assertThrows<IOB>(IOB::class.java, { subsubmatrix[-3, 0] }) },
			Executable { assertThrows<IOB>(IOB::class.java, { subsubmatrix[0, -3] }) },
			Executable { assertThrows<IOB>(IOB::class.java, { subsubmatrix[3, 0] }) },
			Executable { assertThrows<IOB>(IOB::class.java, { subsubmatrix[0, 3] }) }
		)
	}

	@DisplayName("Get on non-BoundsExtended subsubmatrix cause IOB when outside subsubmatrix bounds")
	@Test
	fun subsubmatrix_getNonExtendedBoundsIllegalIndex() {
		val matrix = makeMatrix(Dimensions(4, 4), indexInit)
		val submatrix = matrix.submatrix(Rectangle(Index(1, 1), Index(3, 3)), false)
		val subsubmatrix = submatrix.submatrix(Rectangle(Index(1, 1), Index(2, 2)), false)
		assertAll(
			Executable { assertThrows<IOB>(IOB::class.java, { subsubmatrix[-1, -1] }) },
			Executable { assertThrows<IOB>(IOB::class.java, { subsubmatrix[-1, 0] }) },
			Executable { assertThrows<IOB>(IOB::class.java, { subsubmatrix[0, -1] }) },
			Executable { assertThrows<IOB>(IOB::class.java, { subsubmatrix[1, 1] }) },
			Executable { assertThrows<IOB>(IOB::class.java, { subsubmatrix[1, 0] }) },
			Executable { assertThrows<IOB>(IOB::class.java, { subsubmatrix[0, 1] }) }
		)
	}

	// iterator tests
	@DisplayName("Iterator iterates over all elements once")
	@Test
	fun iteratorTest() {
		val matrix = makeMatrix(Dimensions(2, 3), indexInit)
		val values = ArrayList<Index>()
		val it = matrix.iterator()
		while (it.hasNext()) {
			values += it.next()
		}
		assertAll(
			Executable { assertTrue(values.containsAll(setOf(
				Index(0, 0), Index(0, 1), Index(0, 2),
				Index(1, 0), Index(1, 1), Index(1, 2))))
			},
			Executable { assertTrue(values.size == 6, "Too many values returned") }
		)
	}

	@DisplayName("Values match expected value at indices for iterator")
	@Test
	fun iteratorIndicesTest() {
		val matrix = makeMatrix(Dimensions(2, 3), indexInit)
		val values = ArrayList<Index>()
		val indices = ArrayList<Index>()
		val it = matrix.iterator()
		while (it.hasNext()) {
			indices += it.nextIndex()!!
			values += it.next()
		}
		assertTrue(indices == values)
	}

	@DisplayName("Iterator.nextIndex is null after iterator is exhausted")
	@Test
	fun iteratorIndexNullTest() {
		val matrix1 = makeMatrix(Dimensions(0, 0), indexInit)
		val matrix2 = makeMatrix(Dimensions(0, 1), indexInit)
		val matrix3 = makeMatrix(Dimensions(1, 0), indexInit)
		val matrix4 = makeMatrix(Dimensions(1, 1), indexInit)

		assertAll(
			Executable { assertNull(matrix1.iterator().nextIndex()) },
			Executable { assertNull(matrix2.iterator().nextIndex()) },
			Executable { assertNull(matrix3.iterator().nextIndex()) },
			Executable {
				val it = matrix4.iterator()
				it.next()
				assertNull(it.nextIndex())
			}
		)
	}

	@DisplayName("Submatrix iterator iterates over all elements once")
	@Test
	fun submatrix_iteratorTest() {
		val matrix = makeMatrix(Dimensions(3, 4), indexInit)
		val submatrix = matrix.submatrix(Rectangle(Index(1, 1), Index(3, 4)))
		val values = ArrayList<Index>()
		val it = submatrix.iterator()
		while (it.hasNext()) {
			values += it.next()
		}
		assertAll(
			Executable { assertTrue(values.containsAll(setOf(
				Index(1, 1), Index(1, 2), Index(1, 3),
				Index(2, 1), Index(2, 2), Index(2, 3))))
			},
			Executable { assertTrue(values.size <= 6, "Too many values returned") }
		)
	}

	@DisplayName("Submatrix values match expected value at indices for iterator")
	@Test
	fun submatrix_iteratorIndicesTest() {
		val matrix = makeMatrix(Dimensions(4, 5), indexInit)
		val submatrix = matrix.submatrix(Rectangle(Index(1, 1), Index(3, 4)))
		val values = ArrayList<Index>()
		val indices = ArrayList<Index>()
		val it = submatrix.iterator()
		while (it.hasNext()) {
			indices += it.nextIndex()!!
			values += it.next()
		}
		assertAll(indices.asSequence()
			.zip(values.asSequence())
			.map { Executable { assertEquals(it.first + RIGHT + DOWN, it.second) } }
			.asStream()
		)
	}
}

interface MutableMatrixTests : MatrixTests {
	override fun makeMatrix(dimensions: Dimensions, init: (Index) -> Index): MutableMatrix<Index>

	// set tests
	@DisplayName("Test set")
	@Test
	fun testSet() {

	}

	@DisplayName("Set on submatrix updates value")
	@Test
	fun submatrix_testSet() {
		val indices = listOf(Index(0, 0), Index(0, 1), Index(1, 0), Index(1, 1))
		indices.map { DynamicTest.dynamicTest("submatrix[$it]") {
			val matrix = makeMatrix(Dimensions(3, 4), indexInit)
			val submatrix = matrix.submatrix(Rectangle(Index(1, 1), Index(3, 3)))
			submatrix[it] = Index(-1, -1)
			assertEquals(Index(-1, -1), submatrix[it])
		} }
	}

	@DisplayName("Set on submatrix is reflected parent matrix")
	@Test
	fun submatrix_setReflectsParent() {
		val matrix = makeMatrix(Dimensions(2, 2), indexInit)
		val submatrix = matrix.submatrix(Rectangle(Index(1, 1), Index(2, 2)))
		submatrix[0, 0] = Index(-1, -1)
		assertEquals(Index(-1, -1), matrix[1, 1])
	}

	@DisplayName("When extended bounds is true, submatrix set allows indexing outside submatrix")
	@TestFactory
	fun submatrix_setExtendedBounds(): List<DynamicTest> {
		val indices = listOf(Index(-1, -1), Index(0, -1), Index(-1, 0),
			Index(1, 1), Index(1, 0), Index(0, 1))
		return indices.map { DynamicTest.dynamicTest("submatrix[$it]") {
			val index = it
			val matrix = makeMatrix(Dimensions(3, 3), indexInit)
			val submatrix = matrix.submatrix(Rectangle(Index(1, 1), Index(2, 2)), true)
			assumingThat(index + UP + LEFT == submatrix[index]) {
				submatrix[index] = Index(-1, -1)
				assertEquals(Index(-1, -1), submatrix[index])
			}
		} }
	}

	@DisplayName("Set on boundsExtended submatrix cause IOB exception when outside parent")
	@Test
	fun submatrix_setExtendedBoundsIllegal() {
		val matrix = makeMatrix(Dimensions(3, 3), indexInit)
		val submatrix = matrix.submatrix(Rectangle(Index(1, 1), Index(2, 2)), true)
		assertAll(
			Executable { assertThrows<IOB>(IOB::class.java, { submatrix[-2, 0] = Index(0, 0) }) },
			Executable { assertThrows<IOB>(IOB::class.java, { submatrix[0, -2] = Index(0, 0) }) },
			Executable { assertThrows<IOB>(IOB::class.java, { submatrix[2, 0] = Index(0, 0) }) },
			Executable { assertThrows<IOB>(IOB::class.java, { submatrix[0, 2] = Index(0, 0) }) }
		)
	}

	@DisplayName("Set on non-BoundsExtended submatrix cause IOB when outside submatrix bounds")
	@Test
	fun submatrix_setNonExtendedBoundsIllegalIndex() {
		val matrix = makeMatrix(Dimensions(3, 3), indexInit)
		val submatrix = matrix.submatrix(Rectangle(Index(1, 1), Index(2, 2)), false)
		assertAll(
			Executable { assertThrows<IOB>(IOB::class.java, { submatrix[-1, -1] = Index(0, 0) }) },
			Executable { assertThrows<IOB>(IOB::class.java, { submatrix[-1, 0] = Index(0, 0) }) },
			Executable { assertThrows<IOB>(IOB::class.java, { submatrix[0, -1] = Index(0, 0) }) },
			Executable { assertThrows<IOB>(IOB::class.java, { submatrix[1, 1] = Index(0, 0) }) },
			Executable { assertThrows<IOB>(IOB::class.java, { submatrix[1, 0] = Index(0, 0) }) },
			Executable { assertThrows<IOB>(IOB::class.java, { submatrix[0, 1] = Index(0, 0) }) }
		)
	}

	@DisplayName("Set on subsubmatrix returns expected value")
	@Test
	fun subsubmatrix_testSet() {
		val matrix = makeMatrix(Dimensions(3, 4), indexInit)
		val submatrix = matrix.submatrix(Rectangle(Index(1, 1), Index(3, 3)))
		val subsubmatrix = submatrix.submatrix(Rectangle(Index(1, 1), Index(2, 2)))
		subsubmatrix[0, 0] = Index(-1, -1)
		assertEquals(Index(-1, -1), subsubmatrix[0, 0])
	}

	@DisplayName("Set on subsubmatrix reflects parent matrix")
	@Test
	fun subsubmatrix_setReflectsParent() {
		val matrix = makeMatrix(Dimensions(3, 4), indexInit)
		val submatrix = matrix.submatrix(Rectangle(Index(1, 1), Index(3, 3)))
		val subsubmatrix = submatrix.submatrix(Rectangle(Index(1, 1), Index(2, 2)))
		subsubmatrix[0, 0] = Index(-1, -1)
		assertEquals(Index(-1, -1), matrix[2, 2])
	}

	@DisplayName("When extended bounds is true, submatrix set allows indexing to topmost matrix")
	@TestFactory
	fun subsubmatrix_setExtendedBounds(): List<DynamicTest> {
		val indices = listOf(Index(-2, -2), Index(0, -2), Index(-2, 0),
			Index(1, 1), Index(1, 0), Index(0, 1))
		return indices.map { DynamicTest.dynamicTest("submatrix[$it]") {
			val index = it
			val matrix = makeMatrix(Dimensions(4, 4), indexInit)
			val submatrix = matrix.submatrix(Rectangle(Index(1, 1), Index(3, 3)), true)
			val subsubmatrix = submatrix.submatrix(Rectangle(Index(1, 1), Index(2, 2)), true)
			assumingThat(index + UP + LEFT == subsubmatrix[index]) {
				subsubmatrix[index] = Index(-1, -1)
				assertAll(
					Executable { assertEquals(Index(-1, -1), matrix[index + (2 * DOWN) + (2 * RIGHT)]) },
					Executable { assertEquals(Index(-1, -1), submatrix[index + DOWN + RIGHT]) },
					Executable { assertEquals(Index(-1, -1), subsubmatrix[index]) }
				)
			}
		} }
	}

	@DisplayName("Set on boundsExtended submatrix cause IOB exception when outside top most parent")
	@Test
	fun subsubmatrix_setExtendedBoundsIllegal() {
		val matrix = makeMatrix(Dimensions(4, 4), indexInit)
		val submatrix = matrix.submatrix(Rectangle(Index(1, 1), Index(3, 3)), true)
		val subsubmatrix = submatrix.submatrix(Rectangle(Index(1, 1), Index(2, 2)), true)
		assertAll(
			Executable { assertThrows<IOB>(IOB::class.java, { subsubmatrix[-3, 0] = Index(-1, -1) }) },
			Executable { assertThrows<IOB>(IOB::class.java, { subsubmatrix[0, -3] = Index(-1, -1) }) },
			Executable { assertThrows<IOB>(IOB::class.java, { subsubmatrix[3, 0] = Index(-1, -1) }) },
			Executable { assertThrows<IOB>(IOB::class.java, { subsubmatrix[0, 3] = Index(-1, -1) }) }
		)
	}

	@DisplayName("Set on non-BoundsExtended subsubmatrix cause IOB when outside subsubmatrix bounds")
	@Test
	fun subsubmatrix_setNonExtendedBoundsIllegalIndex() {
		val matrix = makeMatrix(Dimensions(4, 4), indexInit)
		val submatrix = matrix.submatrix(Rectangle(Index(1, 1), Index(3, 3)), false)
		val subsubmatrix = submatrix.submatrix(Rectangle(Index(1, 1), Index(2, 2)), false)
		assertAll(
			Executable { assertThrows<IOB>(IOB::class.java, { subsubmatrix[-1, -1] = Index(-1, -1) }) },
			Executable { assertThrows<IOB>(IOB::class.java, { subsubmatrix[-1, 0] = Index(-1, -1) }) },
			Executable { assertThrows<IOB>(IOB::class.java, { subsubmatrix[0, -1] = Index(-1, -1) }) },
			Executable { assertThrows<IOB>(IOB::class.java, { subsubmatrix[1, 1] = Index(-1, -1) }) },
			Executable { assertThrows<IOB>(IOB::class.java, { subsubmatrix[1, 0] = Index(-1, -1) }) },
			Executable { assertThrows<IOB>(IOB::class.java, { subsubmatrix[0, 1] = Index(-1, -1) }) }
		)
	}

	// iterator set tests
	@DisplayName("Matrix iterator set updates values")
	@Test
	fun matrix_iteratorSetTest() {
		val matrix = makeMatrix(Dimensions(2, 3), indexInit)
		val it = matrix.iterator()
		while (it.hasNext()) {
			val value = it.next()
			it.set(value + UP)
		}
		assertAll(
			listOf(Index(0, 0), Index(0, 1), Index(0, 2),
				Index(1, 0), Index(1, 1), Index(1, 2)
			).asSequence()
				.map { Executable { assertTrue(matrix[it] == it + UP) } }
				.asStream()
		)
	}

	@DisplayName("Submatrix iterator set updates values")
	@Test
	fun submatrix_iteratorSetTest() {
		val matrix = makeMatrix(Dimensions(3, 4), indexInit)
		val submatrix = matrix.submatrix(Rectangle(Index(1, 1), Index(3, 4)))
		val it = submatrix.iterator()
		while (it.hasNext()) {
			val value = it.next()
			it.set(value + UP)
		}
		assertAll(
			listOf(Index(1, 1), Index(1, 2), Index(1, 3),
				Index(2, 1), Index(2, 2), Index(2, 3)
			).asSequence()
				.map { Executable { assertTrue(submatrix[it + UP + LEFT] == it + UP) } }
				.asStream()
		)
	}
}

@DisplayName("Tests on matrixOf methods")
class MatrixOfTests {
	@Nested
	@DisplayName("On calling matrixOf()")
	class EmptyMatrixTests {
		val matrix = matrixOf<Int>()

		@Test
		@DisplayName("returns a dimensions zero matrix")
		fun testForSizeZero() {
			assertEquals(Dimensions(0, 0), matrix.dimensions)
		}

		@Test
		@DisplayName("calling get on matrix throw an IOB exception")
		fun testGetThrowsException() {
			assertThrows<IOB>(IOB::class.java, { matrix[0, 0] } )
		}
	}

	@Nested
	@DisplayName("Given an empty Array to make a matrix")
	class EmptyArrayMatrix {
		val matrix = matrixOf(*arrayOf<List<Int>>())

		@Test
		@DisplayName("returns a dimensions zero matrix")
		fun testForSizeZero() {
			assertEquals(Dimensions(0, 0), matrix.dimensions)
		}

		@Test
		@DisplayName("calling get on matrix throw an IOB exception")
		fun testGetThrowsException() {
			assertThrows<IOB>(IOB::class.java, { matrix[0, 0] } )
		}
	}

	@Nested
	@DisplayName("Given an Array with empty lists to make a matrix")
	class EmptyColumnArrayMatrix {
		val matrix = matrixOf<Int>(listOf(), listOf())

		@Test
		@DisplayName("returns a dimensions zero matrix")
		fun testForSizeZero() {
			assertEquals(Dimensions(2, 0), matrix.dimensions)
		}

		@Test
		@DisplayName("calling get on matrix throw an IOB exception")
		fun testGetThrowsException() {
			assertThrows<IOB>(IOB::class.java, { matrix[0, 0] } )
		}
	}

	@Nested
	@DisplayName("On making a matrix with correctly formatted height")
	class CorrectMatrixOfTest {
		val matrix = matrixOf(listOf(1, 2, 3), listOf(4, 5, 6))

		@Test
		@DisplayName("Test dimensions")
		fun testSize() {
			assertEquals(Dimensions(2, 3), matrix.dimensions)
		}

		@Test
		@DisplayName("Test initializtion")
		fun testValues() {
			assertAll(
				Executable { assertEquals(1, matrix[0, 0]) },
				Executable { assertEquals(2, matrix[0, 1]) },
				Executable { assertEquals(3, matrix[0, 2]) },
				Executable { assertEquals(4, matrix[1, 0]) },
				Executable { assertEquals(5, matrix[1, 1]) },
				Executable { assertEquals(6, matrix[1, 2]) }
			)
		}
	}

	@Nested
	@DisplayName("On making a matrix with height of mismatched lengths")
	class MismatchedMatrixofTest {
		@Test
		@DisplayName("It should throw an IOB exception")
		fun testMake() {
			assertThrows<IOB>(IOB::class.java) { matrixOf(listOf(1, 2), listOf(3)) }
		}
	}
}
