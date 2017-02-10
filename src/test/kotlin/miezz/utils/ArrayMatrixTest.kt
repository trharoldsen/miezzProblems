package miezz.utils

class ArrayMatrixTest : MutableMatrixTests {
	override fun makeMatrix(size: Size, init: (Index) -> Index) =
		ArrayMatrix(size, init)
}