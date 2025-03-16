package fr.eirb.lemondedenemo.periscope.api.network.packets;

/** Represents a packet. */
public interface Packet {

  /**
   * Serializes the packet.
   *
   * @return the serialized packet
   */
  String serialize();
}
