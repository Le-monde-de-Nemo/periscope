package fr.eirb.lemondedenemo.periscope.network;

import fr.eirb.lemondedenemo.periscope.api.events.*;
import fr.eirb.lemondedenemo.periscope.api.network.Connection;
import fr.eirb.lemondedenemo.periscope.api.network.packets.Packet;
import fr.eirb.lemondedenemo.periscope.events.FishEventManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import org.apache.logging.log4j.Logger;

public class FishConnection implements Connection {

  private static final int TIMEOUT = 5000;

  private final Logger logger;
  private final FishEventManager events;
  private final InetSocketAddress address;

  private final Socket socket;
  private PrintWriter writer;
  private Thread reader;

  public FishConnection(Logger logger, String address, int port, FishEventManager events) {
    this.logger = logger;
    this.events = events;
    this.address = new InetSocketAddress(address, port);
    this.socket = new Socket();
  }

  @Override
  public void connect() throws IOException {
    this.socket.connect(this.address, TIMEOUT);
    this.logger.info(
        "Connected to server at "
            + this.address.getHostName()
            + ":"
            + this.address.getPort()
            + ".");
    this.events.fireEvent(new ConnectionReady(this.address));

    // Create in out streams
    this.writer = new PrintWriter(this.socket.getOutputStream(), false);
    this.reader =
        new Thread(
            () -> {
              try {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = this.socket.getInputStream().read(buffer)) != -1) {
                  this.receive(new String(buffer, 0, read));
                }
              } catch (IOException e) {
                this.logger.error(e);
              }
            });
    this.reader.setName("ConnectionReader");
    this.reader.start();
  }

  public boolean isConnected() {
    return this.socket.isConnected();
  }

  @Override
  public void disconnect() throws IOException {
    if (!this.socket.isConnected()) {
      return;
    }
    if (!this.reader.isInterrupted()) this.reader.interrupt();
    this.writer.close();
    this.socket.close();
    this.logger.info("Disconnected from server.");
  }

  private void receive(String message) {
    if (message.isEmpty()) {
      return;
    }
    this.logger.debug("Received message: " + message);
    String[] components = message.split(" ");
    switch (components[0].toLowerCase(Locale.ROOT)) {
      case "no", "greeting" ->
          this.events.fireEvent(
              new HandShakeReceiveEvent(
                  Optional.ofNullable(
                      components[0].equalsIgnoreCase("greeting") ? components[1] : null)));
      case "pong" -> {
        try {
          this.events.fireEvent(new PongReceiveEvent(Integer.parseInt(components[1])));
        } catch (NumberFormatException e) {
          this.logger.error(
              "Invalid value for pong packet. Server may be corrupted, exiting to prevent next errors.",
              e);
          System.exit(1);
        }
      }
      case "list" -> {
        // TODO : parse fishes list
        this.events.fireEvent(new FishesReceivedEvent(new ArrayList<>()));
      }
      case "bye" -> {
        this.events.fireEvent(new QuitAcknowledgedEvent());
        System.exit(0);
      }
    }
  }

  @Override
  public void send(Packet packet) {
    this.writer.println(packet.serialize());
    this.writer.flush();
  }
}
