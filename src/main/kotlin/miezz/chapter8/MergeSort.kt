package miezz.chapter8

class MergeSort(val cutoff: Int=16): SortingAlgorithm {
	private val insertionSort = InsertionSort()

	override fun sort(input: IntArray, begin: Int, end: Int) {
		data class IntSubArray(val array: IntArray, val begin: Int) {
			operator fun get(index: Int): Int {
				return array[index]
			}

			operator fun set(index: Int, value: Int) {
				array[index] = value
			}
		}

		@Suppress("UNCHECKED_CAST")
		var from = IntSubArray(input, 0)
		var to = IntSubArray(IntArray(end - begin), 0)

		fun merge(begin: Int, mid: Int, end: Int) {
			val arrayBegin = begin + from.begin
			val arrayMid = mid + from.begin
			val arrayEnd = end + from.begin

			var i = arrayBegin
			var j = arrayMid
			var k = begin + to.begin

			// perform the merge
			while (i < arrayMid && j < arrayEnd) {
				to[k++] = if (from[i] <= from[j])
					from[i++]
				else
					from[j++]
			}
			while (i < mid) { to[k++] = from[i++] }
			while (j < end) { to[k++] = from[j++] }
		}

		// start with an insertion sort on all of the smallest units
		var i = begin
		while (i < end - cutoff) {
			insertionSort.sort(from.array, i, i+cutoff)
			i += cutoff
		}
		insertionSort.sort(from.array, i, end)

		var step = cutoff
		val arraySize = end - begin
		while (step < arraySize) {
			// merge subsections
			i = begin
			while (i < end - step * 2) {
				merge(i, i + step, i + step * 2)
				i += step * 2
			}
			merge(i, minOf(i + step, end), end)

			// swap the pointers
			with(Pair(from, to)) { to = first; from = second }
			step *= 2
		}

		if (from.array !== input)
			System.arraycopy(from.array, 0, input, begin, end - begin)
	}
}
