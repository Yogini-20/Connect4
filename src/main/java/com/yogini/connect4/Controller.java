package com.yogini.connect4;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

	private static final int COLUMNS = 7;
	private static final int ROWS = 6;
	private static final int CIRCLE_DIAMETER = 80;
	private static final String discColor1 = "#24303E";
	private static final String discColor2 = "#4CAA88";

	private static String PLAYER_ONE = "Player One";
	private static String PLAYER_TWO = "Player Two";

	private boolean isPlayerOneTurn = true;

	@FXML
	public GridPane rootGridPane;
	@FXML
	public Pane insertedDiscsPane;
	@FXML
	public Label playerNameLabel;

	public void createPlayground() {

		Shape rectangleWithHoles = createGameStructuralGrid();
		rootGridPane.add(rectangleWithHoles, 0, 1); // Adding the rectangleWithHoles to the Grid Pane

		List<Rectangle> rectangleList = createClickableColumn(); // Since the rectangleList contains all the rectangle so we again need to use for loop to add all this rectangle within our rootGridPane.
		for (Rectangle rectangle: rectangleList) {
			rootGridPane.add(rectangle, 0, 1);
		}

	}

	private Shape createGameStructuralGrid() {
		Shape rectangleWithHoles = new Rectangle((COLUMNS + 1) * CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);  // Dimension of rectangle. Adding an extra to ROW and COLUMNS providing us with margin in UI

		// Create holes in rectangle
		for (int row = 0; row < ROWS; row++) { // Nested for loop to create the rows and columns of circles. Because it is like a 2D array we use nested for loop.
			for (int col = 0; col < COLUMNS; col++) {
				Circle circle = getCircle(col, row);

				rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle); // From this(rectangleWithHoles) we want to subtract this(circle).
			}
		}

		rectangleWithHoles.setFill(Color.WHITE);

		return rectangleWithHoles;
	}

	private static Circle getCircle(int col, int row) {
		Circle circle = new Circle();
		circle.setRadius((double) CIRCLE_DIAMETER / 2);  // This is for one circle
		circle.setCenterX((double) CIRCLE_DIAMETER / 2);
		circle.setCenterY((double) CIRCLE_DIAMETER / 2);
		circle.setSmooth(true); // Smoothing the edges of the holes

		circle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + (double) CIRCLE_DIAMETER / 4); // This is for multiple circle, plus 5 makes some space between each of the circles
		circle.setTranslateY(row * (CIRCLE_DIAMETER + 5) + (double) CIRCLE_DIAMETER / 4); // The + CIRC_DIA / 4 is actually 20, it gives some space on the left and top
		return circle;
	}

	private List<Rectangle> createClickableColumn() {

		List<Rectangle> rectangleList = new ArrayList<>();

		for (int col = 0; col < COLUMNS; col++){
			Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + (double) CIRCLE_DIAMETER / 4);  // applying margin with TranslateAnimation, so the blue rectangle appears exactly on top of the holes.

			// Rectangles with Hover Effect - means changing the appearance of a UI component when the mouse pointer is placed over it.
			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));

			// Click event
			final int column = col;
			rectangle.setOnMouseClicked(event -> {
				insertDisc(new Disc(isPlayerOneTurn), column);
			});

			rectangleList.add(rectangle);
		}

		return rectangleList;
	}

	private static void insertDisc(Disc disc, int column) {

	}

	private static class Disc extends Circle {

		private final boolean isPlayerOneMove;

		public Disc(boolean isPlayerOneMove) {  // Constructor

			this.isPlayerOneMove = isPlayerOneMove;
			setRadius((double) CIRCLE_DIAMETER / 2);
			setFill(isPlayerOneMove ? Color.valueOf(discColor1) : Color.valueOf(discColor2));
			setCenterX((double) CIRCLE_DIAMETER / 2);
			setCenterY((double) CIRCLE_DIAMETER / 2);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}
