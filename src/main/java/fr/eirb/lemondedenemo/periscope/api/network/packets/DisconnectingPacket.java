package fr.eirb.lemondedenemo.periscope.api.network.packets;

public record DisconnectingPacket() implements Packet {

  @Override
  public String serialize() {
    return "log out";
  }
}
