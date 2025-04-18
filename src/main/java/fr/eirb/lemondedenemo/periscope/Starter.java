package fr.eirb.lemondedenemo.periscope;

import fr.eirb.lemondedenemo.periscope.api.Client;
import fr.eirb.lemondedenemo.periscope.utils.Config;
import java.io.IOException;

public class Starter {

  public static void main(String[] args) throws IOException {
    Config config = Config.getInstance();
    String address = config.getProperties().getProperty("controller-address", "127.0.0.1");
    String port = config.getProperties().getProperty("controller-port", "12345");
    Client fishClient = new FishClient(address, Integer.parseInt(port));
    fishClient.start();
  }
}
