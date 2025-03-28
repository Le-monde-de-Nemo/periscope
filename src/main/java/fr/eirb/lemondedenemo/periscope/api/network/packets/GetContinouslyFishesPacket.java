package fr.eirb.lemondedenemo.periscope.api.network.packets;

public record GetContinouslyFishesPacket() implements Packet {

  @Override
  public String serialize() {
    return "getFishesContinuously";
  }
}
