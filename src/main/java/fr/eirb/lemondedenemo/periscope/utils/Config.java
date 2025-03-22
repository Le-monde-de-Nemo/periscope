package fr.eirb.lemondedenemo.periscope.utils;

import fr.eirb.lemondedenemo.periscope.Starter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.Properties;

public class Config {
  private static Config instance;
  private final Properties properties;

  private Config() throws IOException {
    File file = new File("affichage.cfg");
    if (!file.exists()) {
      InputStream stream = Starter.class.getResourceAsStream("/affichage.cfg");
      if (stream == null) throw new NoSuchFileException("/affichage.cfg");
      Files.copy(stream, file.toPath());
    }
    try (InputStream stream = Files.newInputStream(file.toPath())) {
      this.properties = new Properties();
      properties.load(stream);
    }
  }

  public static Config getInstance() throws IOException {
    if (instance == null) {
      instance = new Config();
    }
    return instance;
  }

  public Properties getProperties() {
    return properties;
  }
}
