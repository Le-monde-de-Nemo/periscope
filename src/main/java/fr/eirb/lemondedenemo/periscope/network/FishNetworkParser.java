package fr.eirb.lemondedenemo.periscope.network;

import fr.eirb.lemondedenemo.periscope.api.events.CommandResultReceiveEvent;
import fr.eirb.lemondedenemo.periscope.api.events.FishesReceivedEvent;
import fr.eirb.lemondedenemo.periscope.api.events.FishesReceivedEvent.FishDestination;
import fr.eirb.lemondedenemo.periscope.api.events.HandShakeReceiveEvent;
import fr.eirb.lemondedenemo.periscope.api.events.PongReceiveEvent;
import fr.eirb.lemondedenemo.periscope.api.events.QuitAcknowledgedEvent;
import fr.eirb.lemondedenemo.periscope.api.events.manager.EventManager;
import fr.eirb.lemondedenemo.periscope.api.network.NetworkParser;
import fr.eirb.lemondedenemo.periscope.utils.Coords;
import fr.eirb.lemondedenemo.periscope.utils.RealFish;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FishNetworkParser implements NetworkParser {

  private static final String SUCCESS_GROUP = "success";
  private final EventManager eventManager;
  Logger logger = LogManager.getLogger(FishNetworkParser.class);

  public FishNetworkParser(EventManager eventManager) {
    this.eventManager = eventManager;
  }

  /**
   * Parse the fish and the destination of the list
   *
   * @param fishPattern pattern of a fish representation
   * @param fishesGroup string to parse
   * @return list of fishDestination
   */
  private static List<FishDestination> getFishDestinations(String fishesGroup) {

    Pattern fishPattern =
        Pattern.compile(
            "\\[(?<id>\\w+) at (?<destX>\\d+)x(?<destY>\\d+),(?<fishLength>\\d+)x(?<fishHeight>\\d+),(?<duration>\\d+)\\]");
    Matcher fishMatcher = fishPattern.matcher(fishesGroup);

    List<FishDestination> fishes = new ArrayList<>();

    while (fishMatcher.find()) {
      String fishId = fishMatcher.group("id");
      int destinationX = Integer.parseInt(fishMatcher.group("destX"));
      int destinationY = Integer.parseInt(fishMatcher.group("destY"));
      int fishLength = Integer.parseInt(fishMatcher.group("fishLength"));
      int fishHeight = Integer.parseInt(fishMatcher.group("fishHeight"));
      double duration = Double.parseDouble(fishMatcher.group("duration"));

      fishes.add(
          new FishDestination(
              new RealFish(fishId, fishLength, fishHeight, fishId),
              new Coords(destinationX, destinationY),
              duration));
    }
    return fishes;
  }

  @Override
  public void parse(String message) {
    logger.debug("Parsing : {}", message);

    Matcher matcher = null;
    Packets currentPacket = null;
    for (Packets packets : Packets.values()) {
      matcher = packets.getPattern().matcher(message);
      if (matcher.find()) {
        currentPacket = packets;
        break;
      }
    }

    if (currentPacket == null) {
      throw new IllegalArgumentException();
    }

    switch (currentPacket) {
      case GREETING -> {
        if (matcher.group(SUCCESS_GROUP) == null) {
          this.eventManager.fireEvent(HandShakeReceiveEvent.failed());
          return;
        }
        this.eventManager.fireEvent(
            new HandShakeReceiveEvent(
                matcher.group(SUCCESS_GROUP) != null,
                matcher.group("id"),
                Integer.parseInt(matcher.group("vueX")),
                Integer.parseInt(matcher.group("vueY")),
                Integer.parseInt(matcher.group("vueWidth")),
                Integer.parseInt(matcher.group("vueHeight"))));
      }
      case PONG ->
          this.eventManager.fireEvent(new PongReceiveEvent(Integer.parseInt(matcher.group("id"))));
      case COMMAND_RESULT ->
          this.eventManager.fireEvent(
              new CommandResultReceiveEvent(matcher.group(SUCCESS_GROUP) != null));
      case GET_FISHES -> {
        String fishesGroup = matcher.group("fishes");
        List<FishDestination> fishes = getFishDestinations(fishesGroup);

        this.eventManager.fireEvent(new FishesReceivedEvent(fishes));
      }
      case EXIT -> this.eventManager.fireEvent(new QuitAcknowledgedEvent());
    }
  }
}
