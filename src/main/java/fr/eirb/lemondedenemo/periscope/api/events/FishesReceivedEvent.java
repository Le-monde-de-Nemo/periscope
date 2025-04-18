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
          "Fish destination: %s; destination : x: %.0f y: %.0f; size: %.0fx%.0f; duration: %.0f",
          fish.getName(),
          destination.x(),
          destination.y(),
          fish.getLength(),
          fish.getHeight(),
          duration);
    }
  }
}
