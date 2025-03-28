package fr.eirb.lemondedenemo.periscope.api.network.packets;

public record AddFishPacket(String name, String method) implements Packet {

  @Override
  public String serialize() {
    return this.name + " " + this.method;
  }
}
