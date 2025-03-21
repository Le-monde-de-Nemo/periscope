package fr.eirb.lemondedenemo.periscope;

import fr.eirb.lemondedenemo.periscope.display.FishTankDisplay;
import fr.eirb.lemondedenemo.periscope.utils.Coords;
import fr.eirb.lemondedenemo.periscope.utils.Fish;
import java.io.FileNotFoundException;

public class Starter {

  public static void main(String[] args) throws InterruptedException, FileNotFoundException {
    FishTankDisplay tankDisplay = new FishTankDisplay();
    tankDisplay.start();

    tankDisplay.addFish("ok", new Fish(100, 100, "ndacremont.png"), new Coords(400, 400));
    tankDisplay.addFish("ok2", new Fish(20, 20), new Coords(400, 400));
    tankDisplay.addFish("ok3", new Fish(30, 30), new Coords(400, 400));

    Thread.sleep(1000);
    tankDisplay.moveFish("ok", new Coords(600, 600), 5000);
    tankDisplay.moveFish("ok2", new Coords(400, 700), 5000);
    tankDisplay.moveFish("ok3", new Coords(300, 700), 5000);
    Thread.sleep(5000);
    tankDisplay.moveFish("ok", new Coords(0, 0), 5000);
  }
}
