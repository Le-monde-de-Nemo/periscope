package fr.eirb.lemondedenemo.periscope;

import fr.eirb.lemondedenemo.periscope.display.HelloApplication;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.Properties;

public class Starter {

  public static void main(String[] args) throws IOException {
    File file = new File("affichage.cfg");
    if (!file.exists()) {
      InputStream stream = Starter.class.getResourceAsStream("/affichage.cfg");
      if (stream == null) throw new NoSuchFileException("/affichage.cfg");
      Files.copy(stream, file.toPath());
    }
    try (InputStream stream = Files.newInputStream(file.toPath())) {
      Properties properties = new Properties();
      properties.load(stream);
      System.out.println(properties.get("controller-address"));
      HelloApplication.startDisplay(args);
    }
  }
}
