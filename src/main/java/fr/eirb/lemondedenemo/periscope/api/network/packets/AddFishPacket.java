package fr.eirb.lemondedenemo.periscope.api.network.packets;

import fr.eirb.lemondedenemo.periscope.api.utils.Fish;
import fr.eirb.lemondedenemo.periscope.utils.Coords;

public record AddFishPacket(String name, Fish fish, Coords location, String method)
    implements Packet {

  @Override
  public String serialize() {
    return String.format(
        "addFish %s at %dx%d,%dx%d, %s",
        name, location.x(), location.y(), fish.getLength(), fish.getHeight(), method);
  }
}
