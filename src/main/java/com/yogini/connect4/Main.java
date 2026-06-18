package com.yogini.connect4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

	private Controller controller;

	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("game.fxml"));
		GridPane rootGridPane = fxmlLoader.load();

		controller = fxmlLoader.getController();

		Scene scene = new Scene(rootGridPane);

		stage.setTitle("Connect 4");
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
	}
}
