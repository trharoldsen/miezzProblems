package miezz.chapter10.tictactoe

import javafx.application.Application
import javafx.application.Platform
import javafx.scene.canvas.Canvas
import javafx.scene.control.MenuItem
import javafx.scene.control.ScrollPane
import javafx.scene.input.KeyCombination
import javafx.scene.layout.BorderPane
import tornadofx.*

class TicTacFootApp : App(Main::class)


fun main(args: Array<String>) {
	Application.launch(TicTacFootApp::class.java, *args)
}

class Main : View("TicTacFoot") {
	override val root = BorderPane()
	val menuItemUndo: MenuItem
	val canvas: Canvas

	init {
		var menuItemUndo: MenuItem? = null
		var canvas: Canvas? = null

		with(root) {
			top {
				menubar {
					menu("_Game") {
						isMnemonicParsing = true
						item("_New Game") {
							isMnemonicParsing = true
							accelerator = KeyCombination.valueOf("Ctrl+N")
							setOnAction { onNewGame() }
						}

						menuItemUndo = item("_Undo") {
							isMnemonicParsing = true
							accelerator = KeyCombination.valueOf("Ctrl+Z")
							setOnAction { onUndo() }
						}

						item("_Quit") {
							isMnemonicParsing = true
							accelerator = KeyCombination.valueOf("Ctrl+Q")
							setOnAction { onQuit() }
						}
					}
				}
			}
			center {
				val scrollPane = ScrollPane()
				val gameCanvas = GameCanvas(500.0, 500.0)
				scrollPane.add(gameCanvas)
				add(scrollPane)
				gameCanvas.resize(30.0, 30.0)
				canvas = gameCanvas
			}
		}

		this.menuItemUndo = menuItemUndo!!
		this.canvas = canvas!!
	}

	fun onNewGame() {
		println("New Game")
	}

	fun onUndo() {
		println("Undo")
	}

	fun onQuit() {
		Platform.exit()
	}
}

class NewGameDialog(val parent: Main) {

}