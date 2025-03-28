package fr.eirb.lemondedenemo.periscope.utils;

import fr.eirb.lemondedenemo.periscope.Starter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Config {
  private static Config instance;
  private final Properties properties;
  private final Logger logger = LogManager.getLogger();

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

    createDataFolder();
  }

  public static Config getInstance() throws IOException {
    if (instance == null) {
      instance = new Config();
    }
    return instance;
  }

  private void createDataFolder() throws IOException {
    String dataFolderName;
    dataFolderName = (String) getProperties().get("resources");

    File dataFolder = new File(dataFolderName);
    if (dataFolder.exists()) return;

    logger.warn("Data folder doesn't exists, create it with default fishes assets");
    Files.createDirectory(dataFolder.toPath());
    InputStream stream = Starter.class.getResourceAsStream("/fishes_default/nemo.png");
    if (stream == null) throw new NoSuchFileException("/fishes_default/nemo.png");
    Files.copy(stream, new File(dataFolder, "nemo.png").toPath());
  }

  public Properties getProperties() {
    return properties;
  }
}
