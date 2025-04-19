package fr.eirb.lemondedenemo.periscope.commands;

import fr.eirb.lemondedenemo.periscope.api.commands.manager.CommandResult;
import fr.eirb.lemondedenemo.periscope.api.events.FishesReceivedEvent;
import fr.eirb.lemondedenemo.periscope.api.events.FishesReceivedEvent.FishDestination;
import fr.eirb.lemondedenemo.periscope.api.events.HandShakeReceiveEvent;
import fr.eirb.lemondedenemo.periscope.api.events.manager.EventHandler;
import fr.eirb.lemondedenemo.periscope.api.events.manager.Listener;
import fr.eirb.lemondedenemo.periscope.api.utils.Fish;
import fr.eirb.lemondedenemo.periscope.utils.Coords;
import java.util.ArrayList;
import java.util.List;

/** Keep track of the fishes inside the tank and its specifications */
public class FishStatusCommandListener implements Listener {
  private final List<FishDestination> fishes = new ArrayList<>();
  private String clientId = null;
  private Coords tankPosition;
  private Coords tankSize;

  /**
   * Keep track of the fishes present in the Tank
   *
   * @param event list of fishes with their destination received
   */
  @EventHandler
  public void onFishes(FishesReceivedEvent event) {
    fishes.clear();
    fishes.addAll(event.fishes());
  }

  /**
   * Get client id and tank specifications
   *
   * @param event handshake event
   */
  @EventHandler
  public void onHandShake(HandShakeReceiveEvent event) {
    this.clientId = event.id();
    this.tankPosition = new Coords(event.vueX(), event.vueY());
    this.tankSize = new Coords(event.width(), event.height());
  }

  /**
   * Get information on the tank
   *
   * @return CommandResult with the tank specifications
   */
  public CommandResult getStatus() {
    if (clientId == null) {
      return new FishCommandResult(false, "Not connected");
    }
    // Client id
    StringBuilder stringBuilder =
        new StringBuilder("Connected to the controller as " + clientId + "\n");

    // Tank specifications
    stringBuilder
        .append("Tank located at ")
        .append(tankPosition)
        .append(" with size ")
        .append(tankSize)
        .append("\n");

    // Number of fishes
    if (fishes.size() <= 1) stringBuilder.append(fishes.size()).append(" fish found\n");
    else stringBuilder.append(fishes.size()).append(" fishes found\n");

    // List of fishes
    for (FishDestination fishDestination : this.fishes) {
      Fish fish = fishDestination.fish();
      Coords position = fishDestination.destination();
      stringBuilder
          .append("Fish ")
          .append(fish.getName())
          .append(" at ")
          .append(position)
          .append(" with size ")
          .append(fish.getLength())
          .append("x")
          .append(fish.getHeight());

      if (fishDestination.duration() == 0) stringBuilder.append(" not started");
      else stringBuilder.append(" started");

      stringBuilder.append("\n");
    }

    return new FishCommandResult(true, stringBuilder.toString());
  }
}
