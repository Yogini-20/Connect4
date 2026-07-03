package com.yogini.connect4;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

	private static final int COLUMNS = 7;
	private static final int ROWS = 6;
	private static final int CIRCLE_DIAMETER = 80;
	private static final String discColor1 = "#24303E";
	private static final String discColor2 = "#4CAA88";

	private static String PLAYER_ONE = "Player One";
	private static String PLAYER_TWO = "Player Two";

	@FXML
	public TextField playerOneTextField;
	@FXML
	public TextField playerTwoTextField;
	@FXML
	public Button setNamesButton;

	private boolean isPlayerOneTurn = true;

	private Disc[][] insertedDiscsArray = new Disc[ROWS][COLUMNS]; // For Structural Changes: for the developer

	@FXML
	public GridPane rootGridPane;
	@FXML
	public Pane insertedDiscsPane;
	@FXML
	public Label playerNameLabel;

	private boolean isAllowedToInsert = true;   // Flag to avoid same color disc being added multiple times.

	private boolean isGameStarted = false;  // A boolean flag that indicates checks whether the game has started or not.

	public void createPlayground() {

		// Run this after the JavaFX UI has finished loading.
		// It automatically gives keyboard focus to the "playerOneTextField".
		Platform.runLater(() -> playerOneTextField.requestFocus());

		Shape rectangleWithHoles = createGameStructuralGrid();
		rootGridPane.add(rectangleWithHoles, 0, 1); // Adding the rectangleWithHoles to the Grid Pane

		List<Rectangle> rectangleList = createClickableColumn(); // Since the rectangleList contains all the rectangle so we again need to use for loop to add all this rectangle within our rootGridPane.
		for (Rectangle rectangle: rectangleList) {
			rootGridPane.add(rectangle, 0, 1);
		}

		setNamesButton.setOnAction(event -> {

			// Check if both player names are entered
			if (playerOneTextField.getText().trim().isEmpty() ||
					playerTwoTextField.getText().trim().isEmpty()) {

				// Show warning if any name is missing
				Alert alert = new Alert(Alert.AlertType.WARNING);
				alert.setTitle("Connect Four");
				alert.setHeaderText("Player names are required.");
				alert.setContentText("Please enter names for both players before starting the game.");
				alert.show();

				return;
			}

			// Get the names entered by the players.
			PLAYER_ONE = playerOneTextField.getText().trim();
			PLAYER_TWO = playerTwoTextField.getText().trim();

			// Display the current player's name
			playerNameLabel.setText(isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO);

			// Allow the game to start
			isGameStarted = true;

		});
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

				// Do not allow disc insertion until the game starts
				if (!isGameStarted) {
					Alert alert = new Alert(Alert.AlertType.WARNING);
					alert.setTitle("Connect Four");
					alert.setHeaderText("Game not started");
					alert.setContentText("Please enter both player names and click 'Set Names'.");
					alert.show();
					return;
				}

				// Insert a disc only if another disc is not already falling
				if (isAllowedToInsert) {
					isAllowedToInsert = false;  // When disc is being dropped then no more disc will be inserted
					insertDisc(new Disc(isPlayerOneTurn), column);
				}
			});

			rectangleList.add(rectangle);
		}

		return rectangleList;
	}

	private void insertDisc(Disc disc, int column) {

		// To insert a disc at specific position
		int row = ROWS - 1; // Counter variable - To determine position of our new disc.
		while (row >= 0){ // which row is empty within our column
			if (getDiscIfPresent(row, column) == null) { // To check emptiness.
				break;
			}
			row--;
		}
		if (row < 0) { // If row actually full. If it is full, we can't insert anymore disc.
			return;
		}

		insertedDiscsArray[row][column] = disc;   // For structural changes: For developers
		insertedDiscsPane.getChildren().add(disc);  // For visual changes: For players

		// This for X-axis
		disc.setTranslateX(column * (CIRCLE_DIAMETER + 5) + (double) CIRCLE_DIAMETER / 4);

		// This for Y-axis.
		TranslateTransition translateTransition = getTranslateTransitionOnYaxis(disc, column, row);
		translateTransition.play();
	}

	private TranslateTransition getTranslateTransitionOnYaxis(Disc disc, int column, int currentRow) {
		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), disc); // This disc fall from top to bottom
		translateTransition.setToY(currentRow * (CIRCLE_DIAMETER + 5) + (double) CIRCLE_DIAMETER / 4);
		translateTransition.setOnFinished(event -> {  // When disc is placed at correct position we are toggling between player1 and player2

			isAllowedToInsert = true;  // Finally, when disc is dropped allow next player to insert disc.
			// When the game ends
			if (gameEnded(currentRow, column)){
				gameOver();
			}

			isPlayerOneTurn = !isPlayerOneTurn;  // Toggle between the players. isPlayerOneTurn now become player 2 turn. Now player2 got the right side to play his turn. If previously the player1 has inserted the disc then now it is turned for player2 & vice versa, change color of disc

			// To display whose turn is this
			playerNameLabel.setText(isPlayerOneTurn? PLAYER_ONE : PLAYER_TWO);
		});
		return translateTransition;
	}

	private boolean gameEnded(int row, int column) {
		// Where the last disc inserted.
		// Vertical points(holes).

		List<Point2D> verticalPoints = IntStream.rangeClosed(row - 3, row + 3) // Range of row values = 0,1,2,3,4,5  A small example: Player has inserted his last disc at row=2, column=3
				.mapToObj(r -> new Point2D(r, column))    // Index of each element present in column [row][column]: (0,3), (1,3), (2,3), (3,3), (4,3), (5,3) --> in JAVA : Point2D Class that hold the value in terms of x and y coordinate
				.collect(Collectors.toList()); // r variable because row value will constantly change

		List<Point2D> horizontalPoints = IntStream.rangeClosed(column - 3, column + 3)
				.mapToObj(c -> new Point2D(row, c)) // The value of row will be constant
				.collect(Collectors.toList());

		// Checking Diagonals
		Point2D startPoint1 = new Point2D(row - 3, column + 3);   // Particular point
		List<Point2D> diagonal1Points = IntStream.rangeClosed(0, 6)
				.mapToObj(i -> startPoint1.add(i, -i))
				.collect(Collectors.toList());

		Point2D startPoint2 = new Point2D(row - 3, column - 3);
		List<Point2D> diagonal2Points = IntStream.rangeClosed(0, 6)
				.mapToObj(i -> startPoint2.add(i, i))
				.collect(Collectors.toList());

		return checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
				|| checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points);

	}

	// Check out possible combinations
	private boolean checkCombinations(List<Point2D> points) {

		// Checking 4 combination of chain
		int chain = 0;

		for ( Point2D point : points) {

			int rowIndexForArray = (int) point.getX(); // Get the row index form point object
			int columnIndexForArray = (int) point.getY();

			Disc disc = getDiscIfPresent(rowIndexForArray, columnIndexForArray);

			if(disc != null && disc.isPlayerOneMove == isPlayerOneTurn) { // If the last inserted disc belongs to the current player

				chain++;
				if (chain == 4) {
					return true;  // Return to the gameEnded method
				}
			} else {
				chain = 0;  // Then again start fresh with int chain = 0; and from here (int rowIndexForArray = (int) point.getX(); )
			}
		}
		return false;  // Worst case - If we're not getting any chain then simply return false.
	}

	private Disc getDiscIfPresent(int rowIndexForArray, int columnIndexForArray) {       // To prevent ArrayOutOfBoundException
		if (rowIndexForArray >= ROWS || rowIndexForArray < 0 || columnIndexForArray >= COLUMNS || columnIndexForArray < 0) // If row or column index is invalid
			return null;

		return insertedDiscsArray[rowIndexForArray][columnIndexForArray];
	}

	private void gameOver() {
		String winner = isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO;
		System.out.println("Winner is " + winner);

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect Four");
		alert.setHeaderText("The Winner is " + winner);
		alert.setContentText("Want to play again? ");

		ButtonType yesBtn = new ButtonType("Yes");
		ButtonType noBtn = new ButtonType("No, Exit");
		alert.getButtonTypes().setAll(yesBtn, noBtn);

		// This statement will actually ensure that all the enclosing code will be executed only after the animation has ended.
		Platform.runLater(()-> {   // Helps us to resolve IllegalStateException.
			Optional<ButtonType> btnClicked = alert.showAndWait();
			if (btnClicked.isPresent() && btnClicked.get() == yesBtn) { // without isPresent() the app can crash. It is shown in warning by IntelliJ IDEA
				// ... user chose YES so RESET the game
				resetGame();
			} else {
				// ... user chose NO ..... so Exit the Game
				Platform.exit();
				System.exit(0);
			}
		});

	}

	public void resetGame() {

		insertedDiscsPane.getChildren().clear();    // Visually, Remove all Inserted Disc from Pane

		for (int row = 0; row < insertedDiscsArray.length; row++) { // Structurally, Make all elements of insertedDiscsArray[][] to null
			for (int col = 0; col < insertedDiscsArray[row].length; col++) {
				insertedDiscsArray[row][col] = null;
			}
		}

		// Clear player names from text fields
		playerOneTextField.clear();
		playerTwoTextField.clear();

		// Reset stored player names
		PLAYER_ONE = "Player One";
		PLAYER_TWO = "Player Two";

		isPlayerOneTurn = true; // Let player1 start the game
		isGameStarted = false;
		isAllowedToInsert = true;

		// Clear current player label
		playerNameLabel.setText("Player One");

		// Put cursor in first text field
		playerOneTextField.requestFocus();
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
		createPlayground(); // Prepare a fresh playground
	}
}
