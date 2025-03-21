package fr.eirb.lemondedenemo.periscope.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Fish {
  public int length;
  public int height;
  public File imageFile;

  public Fish(int length, int height, File imageFile) {
    this.length = length;
    this.height = height;
    this.imageFile = imageFile;
  }

  public Fish(int length, int height) throws FileNotFoundException {
    this(length, height, "nemo.png");
  }

  public Fish(int length, int height, String resourceName) throws FileNotFoundException {
    // set default asset
    String dataFolder;
    try {
      dataFolder = (String) Config.getInstance().getProperties().get("resources");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    File imageFile = new File(dataFolder + "/" + resourceName);

    if (!imageFile.exists()) throw new FileNotFoundException(resourceName);
    this.imageFile = imageFile;
    this.length = length;
    this.height = height;
  }
}
