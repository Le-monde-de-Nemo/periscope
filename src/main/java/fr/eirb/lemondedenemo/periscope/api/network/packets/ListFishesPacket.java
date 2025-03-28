package fr.eirb.lemondedenemo.periscope.api.network.packets;

import java.util.Optional;

public record ListFishesPacket(Optional<Integer> amount) implements Packet {

  @Override
  public String serialize() {
    return "ls" + amount.map(String::valueOf).map(n -> " " + n).orElse("");
  }
}
