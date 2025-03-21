package fr.eirb.lemondedenemo.periscope.api.network.packets;

import java.util.Optional;

public record HandShakeInitPacket(Optional<String> id) implements Packet {

  @Override
  public String serialize() {
    return "hello" + this.id.map(id -> " in as " + id).orElse("");
  }
}
