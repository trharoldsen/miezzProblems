package miezz.utils

class ArrayMatrixTest : MutableMatrixTests {
	override fun makeMatrix(dimensions: Dimensions, init: (Index) -> Index) =
		ArrayMatrix(dimensions, init)
}