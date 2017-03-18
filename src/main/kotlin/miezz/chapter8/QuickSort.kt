package miezz.chapter8

class QuickSort(val cutoff: Int) : SortingAlgorithm {
	val insertionSort = InsertionSort()

	override tailrec fun sort(input: IntArray, begin: Int, end: Int) {
		if (end - begin < cutoff) {
			insertionSort.sort(input, begin, end)
			return
		}

		val high = end - 1
		val pivot = choosePivot(input, begin, high)
		swap(input, high, pivot)
		val middle = partition(input, begin, high)
		if (end - middle > middle - begin) {
			@Suppress("NON_TAIL_RECURSIVE_CALL")
			sort(input, begin, middle)
			sort(input, middle + 1, end)
		} else {
			@Suppress("NON_TAIL_RECURSIVE_CALL")
			sort(input, middle + 1, end)
			sort(input, begin, middle)
		}
	}

	private fun choosePivot(input: IntArray, low: Int, high: Int): Int {
		val lowValue = input[low]
		val highValue = input[high]
		val middle = (high + low) / 2
		val middleValue = input[middle]

		return if (lowValue < highValue) {
			if (lowValue >= middleValue) low
			else if (middleValue <= highValue) middle
			else high
		} else {
			if (highValue >= middleValue) high
			else if (middleValue <= lowValue) middle
			else low
		}
	}

	private fun partition(input: IntArray, low: Int, high: Int): Int {
		val pivotValue = input[high]

		var i = low
		// search for the first higher value
		while(i < high && input[i] <= pivotValue) i++
		for (j in i+1..high-1) {
			if (input[j] < pivotValue)
				swap(input, i++, j)
		}
		swap(input, i, high)
		return i
	}

	private fun swap(input: IntArray, i1: Int, i2: Int) {
		val tmp = input[i1]
		input[i1] = input[i2]
		input[i2] = tmp
	}
}