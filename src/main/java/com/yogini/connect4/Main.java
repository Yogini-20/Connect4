package com.yogini.connect4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

	private Controller controller;

	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("game.fxml"));
		GridPane rootGridPane = fxmlLoader.load();

		controller = fxmlLoader.getController(); // those method used to pass fxml data to controller. In temperature app other method wee used to do that.

		MenuBar menuBar = createMenu();
		menuBar.prefWidthProperty().bind(stage.widthProperty()); // To cover all the space of the menuPane.

		Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
		menuPane.getChildren().add(menuBar);

		Scene scene = new Scene(rootGridPane);

		stage.setTitle("Connect 4");
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
	}

	private MenuBar createMenu() {

		Menu fileMenu = new Menu("File");

		MenuItem newGame = new MenuItem("New game");
		MenuItem resetGame = new MenuItem("Reset game");
		SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
		MenuItem exitGame = new MenuItem("Exit game");

		fileMenu.getItems().addAll(newGame, resetGame, separatorMenuItem, exitGame);

		Menu helpMenu = new Menu("Help");

		MenuItem aboutGame = new MenuItem("About Connect4");
		SeparatorMenuItem separator = new SeparatorMenuItem();
		MenuItem aboutMe = new MenuItem("About me");

		helpMenu.getItems().addAll(aboutGame, separator, aboutMe);

		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(fileMenu, helpMenu);

		return menuBar;
	}
}
