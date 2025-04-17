package fr.eirb.lemondedenemo.periscope.network;

import fr.eirb.lemondedenemo.periscope.api.events.*;
import fr.eirb.lemondedenemo.periscope.api.network.Connection;
import fr.eirb.lemondedenemo.periscope.api.network.packets.Packet;
import fr.eirb.lemondedenemo.periscope.events.FishEventManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import org.apache.logging.log4j.Logger;

public class FishConnection implements Connection {

  private static final int TIMEOUT = 5000;

  private final Logger logger;
  private final FishEventManager events;
  private final InetSocketAddress address;
  private final FishNetworkParser parser;

  private final Socket socket;
  private PrintWriter writer;
  private Thread reader;

  public FishConnection(Logger logger, String address, int port, FishEventManager events) {
    this.logger = logger;
    this.events = events;
    this.address = new InetSocketAddress(address, port);
    this.socket = new Socket();
    this.parser = new FishNetworkParser(events);
  }

  @Override
  public void connect() throws IOException {
    this.socket.connect(this.address, TIMEOUT);
    this.logger.info(
        "Connected to server at {} : {}.", this.address.getHostName(), this.address.getPort());
    this.events.fireEvent(new ConnectionReady(this.address));

    // Create in out streams
    this.writer = new PrintWriter(this.socket.getOutputStream(), false);
    this.reader =
        new Thread(
            () -> {
              try {
                BufferedReader in =
                    new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                  this.receive(line);
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
    while (!this.reader.isInterrupted())
      ;
    this.writer.close();
    this.socket.close();
    this.logger.info("Disconnected from server.");
  }

  private void receive(String message) {
    if (message.isEmpty()) {
      return;
    }
    this.logger.debug("Received message: {}", message);
    this.parser.parse(message);
  }

  @Override
  public void send(Packet packet) {
    this.writer.println(packet.serialize());
    this.writer.flush();
  }
}
