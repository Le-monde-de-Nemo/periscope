package fr.eirb.lemondedenemo.periscope.display;

import fr.eirb.lemondedenemo.periscope.api.display.TankDisplay;
import fr.eirb.lemondedenemo.periscope.api.events.FishesReceivedEvent;
import fr.eirb.lemondedenemo.periscope.api.events.FishesReceivedEvent.FishDestination;
import fr.eirb.lemondedenemo.periscope.api.events.manager.EventHandler;
import fr.eirb.lemondedenemo.periscope.api.events.manager.Listener;
import fr.eirb.lemondedenemo.periscope.api.utils.Fish;
import fr.eirb.lemondedenemo.periscope.utils.Coords;
import fr.eirb.lemondedenemo.periscope.utils.RealFish;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.Logger;

public class FishTankListener implements Listener {
  private final TankDisplay display;
  private final HashMap<String, Fish> inPlaceFishes = new HashMap<>();
  private final Logger logger;

  public FishTankListener(Logger logger, TankDisplay display) {
    this.display = display;
    this.logger = logger;
  }

  @EventHandler
  public void onFishes(FishesReceivedEvent fishesReceivedEvent) {
    List<FishDestination> fishes = fishesReceivedEvent.fishes();
    logger.debug("Received fishes : {}", fishes);
    double widthRatio = display.getWidth() / 100;
    double heightRatio = display.getHeight() / 100;

    for (FishDestination fishDestination : fishes) {
      // transform relative coordinates into absolute coordinates
      Coords absoluteCoords =
          new Coords(
              fishDestination.destination().x() * widthRatio,
              fishDestination.destination().y() * heightRatio);

      Fish fish = fishDestination.fish();
      Fish absoluteFish =
          new RealFish(
              fish.getName(),
              fish.getLength() * widthRatio,
              fish.getHeight() * heightRatio,
              fish.getImageFile());

      if (inPlaceFishes.containsKey(absoluteFish.getName()))
        this.display.moveFish(absoluteFish.getName(), absoluteCoords, fishDestination.duration());
      else {
        inPlaceFishes.put(absoluteFish.getName(), absoluteFish);
        this.display.addFish(absoluteFish.getName(), absoluteFish, absoluteCoords);
      }
    }
  }
}
