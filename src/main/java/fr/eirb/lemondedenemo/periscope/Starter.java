package fr.eirb.lemondedenemo.periscope;

import fr.eirb.lemondedenemo.periscope.api.Client;
import java.io.FileNotFoundException;

public class Starter {

  public static void main(String[] args) throws InterruptedException, FileNotFoundException {
    Client fishClient = new FishClient("localhost", 5555);
    fishClient.start();
  }
}
