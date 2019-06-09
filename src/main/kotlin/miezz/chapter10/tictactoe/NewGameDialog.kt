package miezz.chapter10.tictactoe

import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window
/*

class NewGameDialog(owner: Window) : Stage(StageStyle.UNDECORATED) {
	private var xOffset: Double = 0.0
	private var yOffset: Double = 0.0
	private val rowsBox = DimensionsComboBox(3)
	private val columnsBox = DimensionsComboBox(3)

	val rows: Int
		get() = rowsBox.value
	val columns: Int
		get() = columnsBox.value

	lateinit var exitMode: ExitMode

	init {
		this.title = "New Game Options"
		this.height = 200.0
		this.width = 400.0
		this.isResizable = false
		this.initOwner(owner)
		this.initModality(Modality.WINDOW_MODAL)

		setOnCloseRequest {
			exitMode = ExitMode.CANCEL
		}

		val root = VBox()
		scene = Scene(root)

        root.setOnMousePressed { event ->
            xOffset = event.sceneX
            yOffset = event.sceneY
        }
        root.setOnMouseDragged { event ->
            x = event.screenX - xOffset
	        y = event.screenY - yOffset
        }

		root.children += makeSizeBox()
		root.children += makeChooseBox()
	}

	private fun makeSizeBox(): HBox {
		val sizeBox = HBox()
		sizeBox.alignment = Pos.CENTER_LEFT
		sizeBox.children += Label("Size: ")
		sizeBox.children += rowsBox
		sizeBox.children += Label(" rows x ")
		sizeBox.children += columnsBox
		sizeBox.children += Label(" columns")
		return sizeBox
	}

	private fun makeChooseBox(): HBox {
		val chooseBox = HBox()
		chooseBox.alignment = Pos.CENTER_RIGHT
		val buttonCancel = Button("Cancel")
		val buttonOkay = Button("Okay")
		chooseBox.spacing = 15.0
		chooseBox.children += buttonCancel
		chooseBox.children += buttonOkay

		buttonCancel.isCancelButton = true
		buttonOkay.isDefaultButton = true

		buttonCancel.setOnAction {
			exitMode = ExitMode.CANCEL
			close()
		}
		buttonOkay.setOnAction {
			exitMode = ExitMode.OKAY
			close()
		}
		return chooseBox
	}

	enum class ExitMode {
		OKAY, CANCEL
	}
}

private class DimensionsComboBox(initialValue: Int) : ComboBox<Int>() {
	private var lastValue = initialValue
	var minimum = 3
	var maximum = 50

	init {
		value = initialValue
		isEditable = true
		items.addAll(IntRange(1, 10))
		editor.minWidth = 35.0
		prefWidth = 60.0
		minWidth = 50.0

		setOnAction {
			val textValue = editor.text.toIntOrNull()

			val newValue = when {
				textValue == null -> lastValue
				textValue < minimum -> minimum
				textValue > maximum -> maximum
				else -> textValue
			}

			value = newValue
			lastValue = newValue
		}
	}
}

*/
