package fr.eirb.lemondedenemo.periscope.api.network.packets;

public record PingPacket(int id) implements Packet {

  @Override
  public String serialize() {
    return "ping " + this.id;
  }
}
