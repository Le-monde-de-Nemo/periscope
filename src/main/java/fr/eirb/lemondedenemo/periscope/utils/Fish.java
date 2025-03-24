package fr.eirb.lemondedenemo.periscope.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Fish {
  private int length;
  private int height;
  private final File imageFile;
  
  public void setLength(int length) {
    this.length = length;
  }
  
  public int getLength() {
    return this.length;
  }
  
  public void setHeight(int height) {
    this.height = height;
  }
  
  public int getHeight() {
    return this.length;
  }
  
  public File getImageFile() {
    return this.imageFile;
  }

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
