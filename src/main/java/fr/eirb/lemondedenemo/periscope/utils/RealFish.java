package fr.eirb.lemondedenemo.periscope.utils;

import fr.eirb.lemondedenemo.periscope.api.utils.Fish;
import java.io.File;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;

public class RealFish implements Fish {
  private final File imageFile;
  private final String name;
  private double length;
  private double height;

  public RealFish(String name, double length, double height, File imageFile) {
    this(name, length, height, imageFile.getName());
  }

  public RealFish(String name, double length, double height) {
    this(name, length, height, "nemo.png");
  }

  public RealFish(String name, double length, double height, String resourceName) {
    this.name = name;
    // set default asset
    String dataFolder;
    try {
      dataFolder = (String) Config.getInstance().getProperties().get("resources");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    File ressourceFile = new File(dataFolder, resourceName);

    if (!ressourceFile.exists()) {
      LogManager.getLogger(Fish.class)
          .warn("Ressource {} not found, switch to default asset", resourceName);
      RealFish defaultFish = new RealFish(name, length, height);
      this.imageFile = defaultFish.imageFile;
    } else {
      this.imageFile = ressourceFile;
    }
    this.length = length;
    this.height = height;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public double getLength() {
    return this.length;
  }

  @Override
  public void setLength(int length) {
    this.length = length;
  }

  @Override
  public double getHeight() {
    return this.height;
  }

  @Override
  public void setHeight(int height) {
    this.height = height;
  }

  @Override
  public File getImageFile() {
    return this.imageFile;
  }
}
