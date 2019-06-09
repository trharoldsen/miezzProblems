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
 * Dimensions (height x width) of a rectangle.
 * @property height number of rows specified by this Size
 * @property width number of columns specified by this Size
 */
data class Dimensions(val height: Int, val width: Int) {
	init {
		if (height < 0)
			throw IllegalArgumentException("height less than 0: $height")
		if (width < 0)
			throw IllegalArgumentException("width less than 0: $width")
	}
	/** the total number of elements in a matrix of this size */
	val numElements: Int get() = height * width
	override fun toString(): String = "Size{$height rows x $width columns)}"
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
	operator fun plus(offset: Offset): Index = shift(offset)

	fun shift(offset: Offset): Index {
		return Index(row + offset.rows, column + offset.columns)
	}

	/**
	 * Returns the offset between this index and [other].  If [other] is to the
	 * bottom right of this index, then the returned offset will have positive row
	 * and column values.
	 */
	operator fun minus(other: Index): Offset = offsetTo(other)

	fun offsetTo(other: Index): Offset {
		return Offset(row - other.row, column - other.column)
	}

	fun offsetFrom(other: Index): Offset = other.offsetTo(this)

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

	override fun toString() = "Offset($rows, $columns)"
}

operator fun Int.times(offset: Offset) = offset * this

/**
 * A rectangle shape describing a matrices shape.
 * @property topLeft the top left index (inclusive) of this rectangle
 * @property bottomRight the bottom right index (exclusive) of this rectangle
 */
data class Rectangle(val topLeft: Index, val bottomRight: Index): Iterable<Index> {
	constructor(top: Int, left: Int, bottom: Int, right: Int) :
		this(Index(top, left), Index(bottom, right))
	constructor(topLeft: Index, dimensions: Dimensions):
		this(topLeft, Index(topLeft.row + dimensions.height, topLeft.column + dimensions.width))

	init {
		if (top > bottom)
			throw IllegalArgumentException("Rectange has negative height")
		if (left > right)
			throw IllegalArgumentException("Rectangle has negative width")
	}

	/**
	 * The dimensions of this rectangle.
	 */
	val dimensions: Dimensions get() {
		return Dimensions(height, width)
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
	val height: Int get() = bottom - top
	val width: Int get() = right - left

	/**
	 * Returns true if [index] exists inside this rectangle
	 */
	operator fun contains(index: Index): Boolean {
		return index.row in top..(bottom - 1) &&
			index.column in left..(right - 1)
	}

	override fun iterator(): Iterator<Index> {
		return (top..bottom-1).asSequence().flatMap { row ->
			(left..right-1).asSequence().map { col -> Index(row, col) }
		}.iterator()
	}

	fun rows(): IntProgression = top..bottom-1
	fun columns(): IntProgression = left..right-1

	override fun toString() = "Rectangle{$topLeft, $bottomRight}"
}

/**
 * A read-only 2 dimensional matrix.  This class does not support shrinking or growing the matrix.
 */
interface Matrix<out T>: Iterable<T> {
	/**
	 * The size of this matrix.
	 */
	val dimensions: Dimensions get() = rectangle.dimensions

	/**
	 * The rectangle defining the bounds of a submatrix.  For a top level matrix, this
	 * is equivalent to [rectangle].
	 */
	val rectangle: Rectangle

	/**
	 * True if this matrix contains no elements
	 */
	fun isEmpty() = dimensions.height == 0 || dimensions.width == 0

	/**
	 * Returns the element at [index].
	 */
	operator fun get(index: Index): T

	/**
	 * Returns the element at index ([row], [column]).
	 */
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
		submatrixRectangleCheck(rectangle, dimensions)
		return SubMatrix(this, extendBounds, rectangle)
	}

	override fun iterator(): MatrixIterator<T> = MatrixIteratorImpl(this)

	fun indices(): Rectangle = rectangle

	fun withIndex(): Iterable<MatrixIndexedValue<T>>
		= indices().map { MatrixIndexedValue(it, this[it]) }
}

data class MatrixIndexedValue<out T>(val index: Index, val value: T)

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
		submatrixRectangleCheck(rectangle, dimensions)
		return SubMatrix(this, extendBounds, rectangle)
	}

	override fun iterator(): MutableMatrixIterator<T> = MatrixIteratorImpl(this)
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

private class MatrixIteratorImpl<T>(
	private val matrix: Matrix<T>
) : MutableMatrixIterator<T> {
	private var nextIndex: Index? =
		if (matrix.dimensions.height > 0 && matrix.dimensions.width > 0) ZERO_INDEX else null
	private var curIndex: Index? = null

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
	private fun Index.increment(): Index? {
		var next = this + RIGHT
		if (next.column < matrix.dimensions.width)
			return next
		next = Index(this.row + 1, 0)
		if (next.row < matrix.dimensions.height)
			return next
		return null
	}

	val mutableMatrix get() = matrix as MutableMatrix<T>
	override fun set(value: T) {
		val curIndex = this.curIndex ?:
			throw IllegalStateException("next() not yet called")
		mutableMatrix[curIndex] = value
	}
}

private class SubMatrix<T>(
	val parent: Matrix<T>,
	val extendBounds: Boolean,
	bounds: Rectangle
) : MutableMatrix<T> {
	override val rectangle: Rectangle = bounds

	val mutableParent: MutableMatrix<T>
		get() = parent as MutableMatrix<T>

	override val dimensions: Dimensions
		get() = rectangle.dimensions

	override fun get(index: Index): T {
		if (!extendBounds) rangeCheck(index, dimensions)
		val adjusted = adjustIndex(index)
		return parent[adjusted]
	}

	private fun adjustIndex(index: Index): Index =
		rectangle.topLeft + (index - ZERO_INDEX)

	override fun submatrix(rectangle: Rectangle, extendBounds: Boolean): MutableMatrix<T> {
		submatrixRectangleCheck(rectangle, dimensions)
		val adjustedTL = adjustIndex(rectangle.topLeft)
		val adjustedBR = adjustIndex(rectangle.bottomRight)
		val adjustedRect = Rectangle(adjustedTL, adjustedBR)
		return SubMatrix(parent, extendBounds, adjustedRect)
	}

	override fun set(index: Index, value: T) {
		if (!extendBounds) rangeCheck(index, dimensions)
		val adjusted = adjustIndex(index)
		mutableParent[adjusted] = value
	}

	override fun iterator(): MutableMatrixIterator<T> = MatrixIteratorImpl(this)
}

private fun rangeCheck(index: Index, dimensions: Dimensions) {
	if (index.row < 0 || index.column < 0 || index.row >= dimensions.height ||
		index.column >= dimensions.width)
		throw IndexOutOfBoundsException("Index: $index, Size: $dimensions")
}

private fun submatrixRectangleCheck(rectangle: Rectangle, dimensions: Dimensions) {
	val topLeft = rectangle.topLeft
	if (topLeft.row < 0 || topLeft.column < 0)
		throw IndexOutOfBoundsException("Index: $topLeft, Size: $dimensions")

	val bottomRight = rectangle.bottomRight
	if (bottomRight.row > dimensions.height || bottomRight.column > dimensions.width)
		throw IndexOutOfBoundsException("Index: $topLeft, Size: $dimensions")
}

private fun unflattenIndex(flat: Int, dimensions: Dimensions): Index {
	val row = flat / dimensions.width
	val column = flat.rem(dimensions.width)
	return Index(row, column)
}

private fun flattenIndex(index: Index, dimensions: Dimensions): Int {
	return index.row * dimensions.width + index.column
}

/**
 * A [Matrix] implemented as a flattened array structure.
 */
class ArrayMatrix<T>(
	override val dimensions: Dimensions,
	init: (Index) -> T
) : MutableMatrix<T> {
	constructor(rows: Int, columns: Int, init: (Index) -> T)
		: this(Dimensions(rows, columns), init)

	@Suppress("USELESS_CAST")
	private val array: Array<Any?> =
		Array(dimensions.numElements) { init(unflattenIndex(it, dimensions)) as T }

	override val rectangle: Rectangle
		get() = this.rectangle

	override operator fun get(index: Index): T {
		rangeCheck(index, dimensions)
		@Suppress("UNCHECKED_CAST")
		return array[flattenIndex(index, dimensions)] as T
	}

	override operator fun set(index: Index, value: T) {
		rangeCheck(index, dimensions)
		array[flattenIndex(index, dimensions)] = value
	}
}

fun <T> matrixOf(): Matrix<T> = mutableMatrixOf()

fun <T> matrixOf(vararg rows: List<T>): Matrix<T> = mutableMatrixOf(*rows)

fun <T> mutableMatrixOf(): MutableMatrix<T> {
	return object : MutableMatrix<T> {
		override val rectangle: Rectangle
			get() = Rectangle(ZERO_INDEX, ZERO_INDEX)

		override fun get(index: Index): T {
			throw IndexOutOfBoundsException("$index")
		}

		override fun set(index: Index, value: T) {
			throw IndexOutOfBoundsException("$index")
		}
	}
}

fun <T> mutableMatrixOf(vararg rows: List<T>): MutableMatrix<T> {
	if (rows.isEmpty())
		return mutableMatrixOf()
	val size = Dimensions(rows.size, rows[0].size)
	return ArrayMatrix(size) {
		val row = rows[it.row]
		row[it.column]
	}
}
