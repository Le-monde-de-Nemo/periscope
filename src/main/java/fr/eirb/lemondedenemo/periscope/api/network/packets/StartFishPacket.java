package fr.eirb.lemondedenemo.periscope.api.network.packets;

public record StartFishPacket(String name) implements Packet {

  @Override
  public String serialize() {
    return "startFish " + this.name;
  }
}
