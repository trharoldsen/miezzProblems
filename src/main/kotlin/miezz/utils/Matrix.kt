package miezz.utils

/** Constant for Index(0, 0) */
val ZERO_INDEX = Index(0, 0)
/** Constant offset for moving up one row */
val UP = Offset(-1, 0)
/** Constant offset for moving down one row */
val DOWN = Offset(1, 0)
/** Constant offset for moving left one row */
val LEFT = Offset(0, -1)
/** Constant offset for moving right one row */
val RIGHT = Offset(0, 1)

/**
 * Size of a matrix as defined by the number of rows and columns in the matrix.
 * @property rows number of rows specified by this Size
 * @property columns number of columns specified by this Size
 */
data class Size(val rows: Int, val columns: Int) {
	init {
		if (rows < 0)
			throw IllegalArgumentException("rows($rows) less than 0")
		if (columns < 0)
			throw IllegalArgumentException("columns($columns) less than 0")
	}
	/** the total number of elements in a matrix of this size */
	val numElements: Int get() = rows * columns
	/** Returns a rectangle based at (0,0) with this size */
	fun asRectangle() = Rectangle(ZERO_INDEX, Index(rows, columns))
	override fun toString(): String = "Size{$rows rows x $columns columns)}"
}

/**
 * An index in a matrix.
 * @property row the row in the matrix
 * @property column the column in the matrix
 */
data class Index(val row: Int, val column: Int) {
	/**
	 * Returns the index of this index shifted by [offset]
	 */
	operator fun plus(offset: Offset) : Index {
		return Index(row + offset.rows, column + offset.columns)
	}

	/**
	 * Returns the offset between this index and [other].  If [other] is to the
	 * bottom right of this index, then the returned offset will have positive row
	 * and column values.
	 */
	operator fun minus(other: Index): Offset {
		return Offset(row - other.row, column - other.column)
	}

	override fun toString() = "[$row, $column]"
}

/**
 * A row, column offset from a matrix index.
 */
data class Offset(val rows: Int, val columns: Int) {
	/** Returns the offset computed by adding this offset with [other] */
	operator fun plus(other: Offset): Offset =
		Offset(rows + other.rows, columns + other.columns)
	/** Returns the index computed by shifting [index] by this offset. */
	operator fun plus(index: Index) = index.plus(this)
	/** Returns the offset computed by subtracting [other] from this offset */
	operator fun minus(other: Offset): Offset =
		Offset(rows - other.rows, columns - other.columns)
	/**
	 * Returns the offset computed by multiplying this offset (both row and column)
	 * by [value]
	 * */
	operator fun times(value: Int) =
		Offset(rows * value, columns * value)

	/**
	 * Returns the offset computed by dividing this offset (both row and column)
	 * by [value]
	 * */
	operator fun div(value: Int) =
		Offset(rows / value, columns / value)
	override fun toString() = "Offset($rows, $columns)"
}

operator fun Int.times(offset: Offset) = offset * this

/**
 * A rectangle shape describing a matrices shape.
 * @property topLeft the top left index (inclusive) of this rectangle
 * @property bottomRight the bottom right index (exclusive) of this rectangle
 */
data class Rectangle(val topLeft: Index, val bottomRight: Index) {
	constructor(top: Int, left: Int, bottom: Int, right: Int) :
		this(Index(top, left), Index(bottom, right))

	init {
		if (top > bottom)
			throw IllegalArgumentException("top ($top) greater than bottom ($bottom)")
		if (left > right)
			throw IllegalArgumentException("left ($left) greater than of right ($right)")
	}

	/**
	 * The size of this rectangle.
	 */
	val size: Size get() {
		val rows = bottomRight.row - topLeft.row
		val columns = bottomRight.column - topLeft.column
		return Size(rows, columns)
	}

	/** Shorthand for [topLeft]`.column` */
	val left: Int get() = topLeft.column
	/** Shorthand for [bottomRight]`.column` */
	val right: Int get() = bottomRight.column
	/** Shorthand for [topLeft]`.row` */
	val top: Int get() = topLeft.row
	/** Shorthand for [bottomRight]`.row` */
	val bottom: Int get() = bottomRight.row
	/** Top right corner of this matrix */
	val topRight: Index get() = Index(top, right)
	/** Bottom left corner of this matrix */
	val bottomLeft: Index get() = Index(bottom, left)

	/**
	 * Returns true if [index] exists inside this rectangle
	 */
	operator fun contains(index: Index): Boolean {
		return index.row in top..(bottom - 1) &&
			index.column in left..(right - 1)
	}

	override fun toString() = "Rectangle{$topLeft, $bottomRight}"
}

/**
 * A read-only 2 dimensional matrix.  This class does not support shrinking or growing the matrix.
 */
interface Matrix<out T>: Iterable<T> {
	/**
	 * The size of this matrix.
	 */
	val size: Size get() = rectangle.size

	/**
	 * True if this matrix contains no elements
	 */
	fun isEmpty() = size.rows == 0 || size.columns == 0

	/**
	 * The rectangle structure of this matrix.  This is equivalent to
	 * `Rectangle(Index(0, 0), Index(matrix.size.rows, matrix.size.columns))`.
	 */
	val rectangle: Rectangle

	/**
	 * The rectangle defining the bounds of a submatrix.  For a top level matrix, this
	 * is equivalent to [rectangle].
	 */
	val absoluteRectangle: Rectangle

	/**
	 * Returns the element at [index].
	 */
	operator fun get(index: Index): T
	operator fun get(row: Int, column: Int) = get(Index(row, column))

	/**
	 * Returns the submatrix formed from the elements bound by [rectangle].  The
	 * rectangle is always relative to the indices in this matrix, not its parent
	 * matrix for submatrices.
	 * @param rectangle the rectangle bounding the submatrix
	 * @param extendBounds if true, allows for accessing elements outside the
	 *     submatrices bounds but within the parent matrices bounds
	 * @throws IndexOutOfBoundsException if rectangle extends beyond the bounds of
	 *     this matrix
	 */
	fun submatrix(rectangle: Rectangle, extendBounds: Boolean=false): Matrix<T> {
		submatrixRectangleCheck(rectangle, size)
		return SubMatrix(this, extendBounds, rectangle)
	}

	override fun iterator(): MatrixIterator<T> = BaseMatrixIterator(this)
}

/**
 * A 2 dimensional with support for updating elements.  This class does not support
 * shrinking or growing the matrix.
 */
interface MutableMatrix<T> : Matrix<T>, MutableIterable<T> {
	/**
	 * Sets the element at [index] to [value].
	 */
	operator fun set(index: Index, value: T): Unit
	operator fun set(row: Int, column: Int, value: T): Unit {
		set(Index(row, column), value)
	}

	override fun submatrix(rectangle: Rectangle, extendBounds: Boolean): MutableMatrix<T>{
		submatrixRectangleCheck(rectangle, size)
		return SubMutableMatrix(this, extendBounds, rectangle)
	}

	override fun iterator(): MutableMatrixIterator<T> = BaseMutableMatrixIterator(this)
}

/**
 * An iterator over individual elements in a [Matrix].
 */
interface MatrixIterator<out T> : Iterator<T> {
	/**
	 * Returns the index of the element that will be retrieved through a call to next.
	 * This method does not progress the iterator.
	 * @return the next index or null if the matrix has reached the end.
	 */
	fun nextIndex(): Index?
}

/**
 * A MutableIterator over individual elements in a [Matrix].  This method supports
 * updating the last value in the matrix through the [set] method.
 */
interface MutableMatrixIterator<T> : MatrixIterator<T>, MutableIterator<T> {
	/**
	 * Sets the value of the last element accessed to [value].
	 * @throws IllegalStateException if the iterator is in an invalid state.
	 */
	fun set(value: T)

	/**
	 * Unsupported for matrices.
	 * @throws UnsupportedOperationException
	 */
	override fun remove() {
		throw UnsupportedOperationException("invalid operation on matrix")
	}
}

private open class BaseMatrixIterator<out T>(
	protected val matrix: Matrix<T>
) : MatrixIterator<T> {
	private var nextIndex: Index? =
		if (matrix.size.rows > 0 && matrix.size.columns > 0) ZERO_INDEX else null
	protected var curIndex: Index? = null

	override fun hasNext(): Boolean = nextIndex != null

	override fun next(): T {
		if (!hasNext())
			throw NoSuchElementException()
		val newIndex = nextIndex!!
		this.curIndex = newIndex
		nextIndex = newIndex.increment()
		return matrix[newIndex]
	}

	override fun nextIndex(): Index? = nextIndex

	/** Increments the index to the next location in the matrix */
	protected open fun Index.increment(): Index? {
		var next = this + RIGHT
		if (next.column < matrix.size.columns)
			return next
		next = Index(this.row + 1, 0)
		if (next.row < matrix.size.rows)
			return next
		return null
	}
}

private class BaseMutableMatrixIterator<T>(
	mutableMatrix: MutableMatrix<T>
) : BaseMatrixIterator<T>(mutableMatrix), MutableMatrixIterator<T> {
	val mutableMatrix get() = matrix as MutableMatrix<T>
	override fun set(value: T) {
		val curIndex = this.curIndex ?:
			throw IllegalStateException("next() not yet called")
		mutableMatrix[curIndex] = value
	}
}

private open class SubMatrix<out T>(
	open val parent: Matrix<T>,
	protected val extendBounds: Boolean,
	bounds: Rectangle
) : Matrix<T> {
	val offset = bounds.topLeft
	override val rectangle: Rectangle = bounds.size.asRectangle()
	override val absoluteRectangle: Rectangle
		get() = Rectangle(offset, offset + (rectangle.bottomRight - ZERO_INDEX) )

	override val size get() = rectangle.size

	override fun get(index: Index): T {
		if (!extendBounds) rangeCheck(index, size)
		val adjusted = adjustIndex(index)
		return parent[adjusted]
	}

	protected fun adjustIndex(index: Index): Index =
		offset + (index - ZERO_INDEX)

	override fun submatrix(rectangle: Rectangle, extendBounds: Boolean): Matrix<T> {
		submatrixRectangleCheck(rectangle, size)
		val adjustedTL = adjustIndex(rectangle.topLeft)
		val adjustedBR = adjustIndex(rectangle.bottomRight)
		val adjustedRect = Rectangle(adjustedTL, adjustedBR)
		return SubMatrix(parent, extendBounds, adjustedRect)
	}

	override fun iterator(): MatrixIterator<T> = BaseMatrixIterator(this)
}

private open class SubMutableMatrix<T>(
	override val parent: MutableMatrix<T>,
	extendBounds: Boolean,
	rectangle: Rectangle
) : SubMatrix<T>(parent, extendBounds, rectangle), MutableMatrix<T> {
	override fun set(index: Index, value: T) {
		if (!extendBounds) rangeCheck(index, size)
		val adjusted = adjustIndex(index)
		parent[adjusted] = value
	}

	override fun submatrix(rectangle: Rectangle, extendBounds: Boolean): MutableMatrix<T> {
		submatrixRectangleCheck(rectangle, size)
		val adjustedTL = adjustIndex(rectangle.topLeft)
		val adjustedBR = adjustIndex(rectangle.bottomRight)
		val adjustedRect = Rectangle(adjustedTL, adjustedBR)
		return SubMutableMatrix(parent, extendBounds, adjustedRect)
	}

	override fun iterator(): MutableMatrixIterator<T> = BaseMutableMatrixIterator(this)
}

private fun rangeCheck(index: Index, dimensions: Size) {
	if (index.row < 0 || index.column < 0 || index.row >= dimensions.rows ||
		index.column >= dimensions.columns)
		throw IndexOutOfBoundsException("Index: $index, Size: $dimensions")
}

private fun submatrixRectangleCheck(rectangle: Rectangle, dimensions: Size) {
	val topLeft = rectangle.topLeft
	if (topLeft.row < 0 || topLeft.column < 0)
		throw IndexOutOfBoundsException("Index: $topLeft, Size: $dimensions")

	val bottomRight = rectangle.bottomRight
	if (bottomRight.row > dimensions.rows || bottomRight.column > dimensions.columns)
		throw IndexOutOfBoundsException("Index: $topLeft, Size: $dimensions")
}

private fun unflattenIndex(flat: Int, dimensions: Size): Index {
	val row = flat / dimensions.columns
	val column = flat.rem(dimensions.columns)
	return Index(row, column)
}

private fun flattenIndex(index: Index, dimensions: Size): Int {
	return index.row * dimensions.columns + index.column
}

/**
 * A [Matrix] implemented as a flattened array structure.
 */
class ArrayMatrix<T>(
	override val size: Size,
	init: (Index) -> T
) : MutableMatrix<T> {
	constructor(rows: Int, columns: Int, init: (Index) -> T)
		: this(Size(rows, columns), init)

	@Suppress("USELESS_CAST")
	private val array: Array<Any?> =
		Array(size.numElements) { init(unflattenIndex(it, size)) as T }

	override val rectangle: Rectangle
		get() {
			val topLeft = Index(0, 0)
			val bottomRight = Index(size.rows, size.columns)
			return Rectangle(topLeft, bottomRight)
		}

	override val absoluteRectangle: Rectangle
		get() = rectangle

	override operator fun get(index: Index): T {
		rangeCheck(index, size)
		@Suppress("UNCHECKED_CAST")
		return array[flattenIndex(index, size)] as T
	}

	override operator fun set(index: Index, value: T) {
		rangeCheck(index, size)
		array[flattenIndex(index, size)] = value
	}
}

fun <T> matrixOf(): Matrix<T> {
	return object : Matrix<T> {
		override val rectangle: Rectangle
			get() = Rectangle(ZERO_INDEX, ZERO_INDEX)

		override val absoluteRectangle: Rectangle
			get() = rectangle

		override fun get(index: Index): T {
			throw IndexOutOfBoundsException("$index")
		}
	}
}

fun <T> matrixOf(vararg rows: List<T>): Matrix<T> {
	if (rows.isEmpty())
		return matrixOf()
	val size = Size(rows.size, rows[0].size)
	return ArrayMatrix(size) {
		val row = rows[it.row]
		row[it.column]
	}
}
