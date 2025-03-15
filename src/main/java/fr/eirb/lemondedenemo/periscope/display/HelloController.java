package fr.eirb.lemondedenemo.periscope.display;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
  @FXML private Label welcomeText;

  @FXML
  protected void onHelloButtonClick() {
    welcomeText.setText("Welcome to JavaFX Application!");
  }
}
