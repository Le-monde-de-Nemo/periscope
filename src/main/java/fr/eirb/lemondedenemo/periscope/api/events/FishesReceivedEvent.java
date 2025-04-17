package fr.eirb.lemondedenemo.periscope.api.events;

import fr.eirb.lemondedenemo.periscope.api.events.manager.Event;
import fr.eirb.lemondedenemo.periscope.api.utils.Fish;
import fr.eirb.lemondedenemo.periscope.utils.Coords;
import java.util.List;

public record FishesReceivedEvent(List<FishDestination> fishes) implements Event {
  public record FishDestination(Fish fish, Coords destination, double duration) {

    @Override
    public String toString() {
      return String.format(
          "New fish : %s; destination : x: %d y: %d; size: %dx%d; duration: %f",
          fish.toString(),
          destination.x(),
          destination.y(),
          fish.getLength(),
          fish.getHeight(),
          duration);
    }
  }
}
