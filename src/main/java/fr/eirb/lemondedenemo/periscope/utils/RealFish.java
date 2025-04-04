package fr.eirb.lemondedenemo.periscope.utils;

import fr.eirb.lemondedenemo.periscope.api.utils.Fish;
import java.io.File;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;

public class RealFish implements Fish {
  private final File imageFile;
  private int length;
  private int height;

  public RealFish(int length, int height, File imageFile) {
    this.length = length;
    this.height = height;
    this.imageFile = imageFile;
  }

  public RealFish(int length, int height) {
    this(length, height, "nemo.png");
  }

  public RealFish(int length, int height, String resourceName) {
    // set default asset
    String dataFolder;
    try {
      dataFolder = (String) Config.getInstance().getProperties().get("resources");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    File imageFile = new File(dataFolder + "/" + resourceName);

    if (!imageFile.exists()) {
      LogManager.getLogger(Fish.class)
          .warn("Ressource " + resourceName + " not found, switch to default asset");
      RealFish defaultFish = new RealFish(length, height);
      this.imageFile = defaultFish.imageFile;
    } else {
      this.imageFile = imageFile;
    }
    this.length = length;
    this.height = height;
  }

  @Override
  public int getLength() {
    return this.length;
  }

  @Override
  public void setLength(int length) {
    this.length = length;
  }

  @Override
  public int getHeight() {
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
