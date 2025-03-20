package fr.eirb.lemondedenemo.periscope.api.network.packets;

public record GetOnceFishesPacket() implements Packet {

  @Override
  public String serialize() {
    return "getFishes";
  }
}
