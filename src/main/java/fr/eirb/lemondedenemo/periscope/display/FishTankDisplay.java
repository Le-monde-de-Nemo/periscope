package fr.eirb.lemondedenemo.periscope.display;

import fr.eirb.lemondedenemo.periscope.utils.Coords;
import fr.eirb.lemondedenemo.periscope.utils.Fish;
import java.util.HashMap;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FishTankDisplay extends Application implements TankDisplay {

  private static FishTankDisplay instance;
  private final HashMap<String, FishItem> fishes = new HashMap<>();
  private Stage primaryStage;

  private static FishTankDisplay getInstance() {
    return instance;
  }

  @Override
  public void start(Stage primaryStage) {
    instance = this;
    this.primaryStage = primaryStage;

    Pane pane = new Pane();
    Scene scene = new Scene(pane, 800, 800);
    pane.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));

    primaryStage.setTitle("Fish Tank");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  @Override
  public void start() {
    new Thread(Application::launch).start();
    // return only when the instance is created
    while (getInstance() == null) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public void addFish(String id, Fish fish, Coords coords) {
    Platform.runLater(
        () -> {
          FishItem rect = new FishItem(fish.length, fish.height, Color.RED);
          getInstance().fishes.put(id, rect);
          rect.setX(coords.x());
          rect.setY(coords.y());

          Pane pane = (Pane) getInstance().primaryStage.getScene().getRoot();
          pane.getChildren().add(rect);
        });
  }

  @Override
  public void stop() {
    Platform.exit();
  }

  @Override
  public void removeFish(String id) {
    Platform.runLater(
        () -> {
          Rectangle fishItem = getInstance().fishes.get(id);
          if (fishItem == null) return;

          Pane pane = (Pane) getInstance().primaryStage.getScene().getRoot();
          pane.getChildren().remove(fishItem);
        });
  }

  @Override
  public void moveFish(String id, Coords coords, int duration) {
    Platform.runLater(
        () -> {
          FishItem fishItem = getInstance().fishes.get(id);
          if (fishItem == null) return;

          TranslateTransition transition = new TranslateTransition();
          transition.setNode(fishItem);
          transition.setToX(coords.x() - fishItem.getX()); // Translation relative
          transition.setToY(coords.y() - fishItem.getY());
          transition.setDuration(Duration.millis(duration));

          transition.play();
        });
  }

  private static class FishItem extends Rectangle {

    public FishItem() {}

    public FishItem(double width, double height) {
      super(width, height);
    }

    public FishItem(double width, double height, Paint fill) {
      super(width, height, fill);
    }

    public FishItem(double x, double y, double width, double height) {
      super(x, y, width, height);
    }
  }
}
