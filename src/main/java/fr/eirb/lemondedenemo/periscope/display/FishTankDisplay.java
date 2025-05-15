package fr.eirb.lemondedenemo.periscope.display;

import fr.eirb.lemondedenemo.periscope.api.display.TankDisplay;
import fr.eirb.lemondedenemo.periscope.api.utils.Fish;
import fr.eirb.lemondedenemo.periscope.utils.Coords;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FishTankDisplay extends Application implements TankDisplay {

  private static final Logger logger = LogManager.getLogger(FishTankDisplay.class);
  private static FishTankDisplay instance;
  private final Map<String, FishItem> fishes = new HashMap<>();
  private Stage primaryStage;

  private static FishTankDisplay getInstance() {
    return instance;
  }

  @Override
  public void start(Stage primaryStage) {
    logger.info("Starting fish tank display");
    this.primaryStage = primaryStage;
    Parameters parameters = getParameters();
    List<String> dimensions = parameters.getUnnamed();
    primaryStage.setResizable(false);

    Pane pane = new Pane();
    Scene scene =
        new Scene(
            pane, Double.parseDouble(dimensions.get(0)), Double.parseDouble(dimensions.get(1)));

    pane.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));

    primaryStage.setTitle(dimensions.get(2));
    primaryStage.setScene(scene);
    primaryStage.show();
    instance = this;
  }

  @Override
  public void start(double width, double height, String title) {
    new Thread(() -> launch(String.valueOf(width), String.valueOf(height), title)).start();
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
  public double getWidth() {
    return getInstance().primaryStage.getWidth();
  }

  @Override
  public double getHeight() {
    return getInstance().primaryStage.getHeight();
  }

  @Override
  public void addFish(String id, Fish fish, Coords coords) {
    Platform.runLater(
        () -> {
          logger.info("Add fish {} at coords x:{} y:{}", id, coords.x(), coords.y());
          FishItem image;
          try {
            image = new FishItem(new Image(new FileInputStream(fish.getImageFile())));
          } catch (FileNotFoundException e) {
            logger.warn("Fish asset not found");
            throw new RuntimeException(e);
          }

          image.setX(coords.x());
          image.setY(coords.y() - fish.getHeight());
          image.setFitHeight(fish.getHeight());
          image.setFitWidth(fish.getLength());

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
          logger.info("Remove fish {}", id);
          FishItem fishItem = getInstance().fishes.get(id);
          if (fishItem == null) return;

          Pane pane = (Pane) getInstance().primaryStage.getScene().getRoot();
          pane.getChildren().remove(fishItem);
        });
  }

  @Override
  public void moveFish(String id, Coords coords, double duration) {
    Platform.runLater(
        () -> {
          FishItem fishItem = getInstance().fishes.get(id);
          if (fishItem == null) return;

          logger.debug(
              "Move fish {} from x:{} y:{} to x:{} y:{} in {} seconds",
              id,
              fishItem.getX(),
              fishItem.getY(),
              coords.x(),
              coords.y(),
              duration);
          TranslateTransition transition = new TranslateTransition();
          transition.setNode(fishItem);
          transition.setToX(coords.x() - fishItem.getX()); // Translation relative
          transition.setToY(coords.y() - fishItem.getY() - fishItem.getFitHeight());
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
