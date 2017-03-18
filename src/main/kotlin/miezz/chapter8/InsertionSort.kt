package miezz.chapter8

class InsertionSort : SortingAlgorithm {
	override fun sort(input: IntArray, begin: Int, end: Int) {
		for (i in begin+1..end-1) {
			val tmp = input[i]
			var j = i
			while (j > begin) {
				if (tmp < input[j-1])
					input[j] = input[j-1]
				else
					break
				j -= 1
			}
			input[j] = tmp
		}
	}
}
