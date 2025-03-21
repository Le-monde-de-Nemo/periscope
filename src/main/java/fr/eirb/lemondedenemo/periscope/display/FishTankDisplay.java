package fr.eirb.lemondedenemo.periscope.display;

import fr.eirb.lemondedenemo.periscope.utils.Config;
import fr.eirb.lemondedenemo.periscope.utils.Coords;
import fr.eirb.lemondedenemo.periscope.utils.Fish;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
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
          FishItem image;
          try {
            image = new FishItem(new Image(new FileInputStream(fish.imageFile)));
          } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
          }

          image.setX(coords.x());
          image.setY(coords.y());
          image.setFitHeight(fish.height);
          image.setFitWidth(fish.length);

          Pane pane = (Pane) getInstance().primaryStage.getScene().getRoot();
          pane.getChildren().add(image);
          getInstance().fishes.put(id, image);
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
          FishItem fishItem = getInstance().fishes.get(id);
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

  private static class FishItem extends ImageView {

    public FishItem() {}

    public FishItem(String url) {
      super(url);
    }

    public FishItem(Image image) {
      super(image);
    }
  }
}
