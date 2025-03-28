package fr.eirb.lemondedenemo.periscope.api.network.packets;

public record DeleteFishPacket(String name) implements Packet {

  @Override
  public String serialize() {
    return this.name;
  }
}
