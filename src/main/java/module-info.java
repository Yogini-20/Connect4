module com.yogini.connect4 {
	requires javafx.controls;
	requires javafx.fxml;


	opens com.yogini.connect4 to javafx.fxml;
	exports com.yogini.connect4;
}