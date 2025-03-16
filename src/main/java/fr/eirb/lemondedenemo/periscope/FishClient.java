package fr.eirb.lemondedenemo.periscope;

import fr.eirb.lemondedenemo.periscope.api.Client;
import fr.eirb.lemondedenemo.periscope.api.network.packets.HandShakeInitPacket;
import fr.eirb.lemondedenemo.periscope.api.network.packets.PingPacket;
import fr.eirb.lemondedenemo.periscope.events.FishEventManager;
import fr.eirb.lemondedenemo.periscope.network.FishConnection;
import java.io.IOException;
import java.util.Optional;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FishClient implements Client {

  private final Logger logger;
  private final FishEventManager events;
  private final FishConnection connection;

  public FishClient(String address, int port) {
    this.logger = LogManager.getLogger("Client Rézo");
    this.logger.atLevel(Level.INFO);
    this.events = new FishEventManager(this.logger);
    this.connection = new FishConnection(this.logger, address, port, this.events);
  }

  public void start() {

    try {
      this.connection.connect();
    } catch (IOException e) {
      this.logger.error(e);
      return;
    }
    this.connection.send(new HandShakeInitPacket(Optional.of("N1")));
    // waiting for the server to close the connection
    int id = 0;
    for (; ; ) {
      try {
        this.connection.send(new PingPacket(id++));
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        this.logger.error(e);
      }
    }
  }

  @Override
  public FishConnection getConnection() {
    return this.connection;
  }

  @Override
  public FishEventManager getEvents() {
    return this.events;
  }

  @Override
  public Logger getLogger() {
    return this.logger;
  }
}
