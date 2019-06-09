package miezz.chapter10.tictactoe

import javafx.scene.canvas.Canvas
import javafx.scene.input.ScrollEvent
import javafx.scene.paint.Color


class GameCanvas : Canvas {
	var zoomUnits = 1.1
	var boardWidth: Double = 1.0
	var boardHeight: Double = 1.0

	constructor() : super()
	constructor(width: Double, height: Double): super(width, height)

	init {
		boardWidth = width
		boardHeight = height

		addEventHandler(ScrollEvent.SCROLL) { event ->
			if (event.isControlDown) {
				if (event.deltaY > 0) {
					boardWidth *= zoomUnits
					boardHeight *= zoomUnits
				} else {
					boardWidth /= zoomUnits
					boardHeight /= zoomUnits
				}
				val parentWidth = parent.prefWidth(-1.0)
				val parentHeight = parent.prefHeight(-1.0)
				width = boardWidth.coerceIn(parentWidth, minOf(parentWidth, boardWidth))
				height = boardHeight.coerceIn(parentHeight, minOf(parentHeight, boardHeight))
				draw()
				println("board: (${boardWidth}x$boardHeight")
			}
		}

		// Redraw canvas when size changes.
		draw()
	}

	private fun draw() {
		println("draw")

		val width = width
		val height = height

		val gc = graphicsContext2D
		gc.clearRect(0.0, 0.0, width, height)
		gc.restore()

		gc.stroke = Color.RED
		gc.strokeLine(0.0, 0.0, boardWidth, boardHeight)
		gc.strokeLine(0.0, boardHeight, boardWidth, 0.0)
	}

	override fun isResizable(): Boolean {
		return true
	}

	override fun prefWidth(width: Double): Double {
		return this.width
	}

	override fun prefHeight(height: Double): Double {
		return this.height
	}
}
