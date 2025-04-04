package fr.eirb.lemondedenemo.periscope.network;

import fr.eirb.lemondedenemo.periscope.api.events.manager.EventHandler;
import fr.eirb.lemondedenemo.periscope.api.events.manager.EventManager;
import fr.eirb.lemondedenemo.periscope.api.events.manager.Listener;
import fr.eirb.lemondedenemo.periscope.api.network.Connection;
import fr.eirb.lemondedenemo.periscope.api.network.packets.PingPacket;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.logging.log4j.Logger;

public class FishPingRunner implements Runnable {

  private final Logger logger;
  private final Connection connection;
  private final AtomicReference<Instant> lastAcknowledgedTime;
  private int id;

  public FishPingRunner(Logger logger, Connection connection, EventManager eventManager) {
    this.logger = logger;
    this.connection = connection;
    this.lastAcknowledgedTime = new AtomicReference<>(Instant.now());
    this.id = 0;
    eventManager.addListener(new PingReader());
  }

  @Override
  public void run() {
    if (this.lastAcknowledgedTime.get().plus(5L, ChronoUnit.SECONDS).isBefore(Instant.now())) {
      this.logger.error("Last acknowledged ping was too long ago, server may be off.");
      System.exit(2);
      return;
    }
    this.connection.send(new PingPacket(this.id++));
  }

  public class PingReader implements Listener {

    @EventHandler
    public void onPing(PingPacket ping) {
      FishPingRunner.this.lastAcknowledgedTime.set(Instant.now());
    }
  }
}
