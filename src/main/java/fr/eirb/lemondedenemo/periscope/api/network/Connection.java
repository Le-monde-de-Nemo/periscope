package fr.eirb.lemondedenemo.periscope.api.network;

import fr.eirb.lemondedenemo.periscope.api.network.packets.Packet;
import java.io.IOException;

/** Represents a connection. */
public interface Connection {

  /**
   * Connects to the server.
   *
   * @throws IOException if an I/O error occurs when creating the socket.
   */
  void connect() throws IOException;

  /**
   * Disconnects from the server.
   *
   * @throws IOException if an I/O error occurs when closing the socket.
   */
  void disconnect() throws IOException;

  /**
   * Sends a packet to the server.
   *
   * @param packet the packet to send.
   */
  void send(Packet packet);
}
