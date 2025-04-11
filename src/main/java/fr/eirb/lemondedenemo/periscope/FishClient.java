package fr.eirb.lemondedenemo.periscope;

import fr.eirb.lemondedenemo.periscope.api.Client;
import fr.eirb.lemondedenemo.periscope.api.commands.manager.CommandManager;
import fr.eirb.lemondedenemo.periscope.api.events.HandShakeReceiveEvent;
import fr.eirb.lemondedenemo.periscope.api.events.manager.EventHandler;
import fr.eirb.lemondedenemo.periscope.api.events.manager.Listener;
import fr.eirb.lemondedenemo.periscope.api.network.packets.HandShakeInitPacket;
import fr.eirb.lemondedenemo.periscope.commands.FishCommandManager;
import fr.eirb.lemondedenemo.periscope.commands.REPL;
import fr.eirb.lemondedenemo.periscope.display.FishTankDisplay;
import fr.eirb.lemondedenemo.periscope.display.TankDisplay;
import fr.eirb.lemondedenemo.periscope.events.FishEventManager;
import fr.eirb.lemondedenemo.periscope.network.FishConnection;
import fr.eirb.lemondedenemo.periscope.network.FishPingRunner;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FishClient implements Client {

  private final Logger logger;
  private final FishEventManager events;
  private final FishConnection connection;
  private final FishCommandManager commands;
  private final REPL repl;
  private final ScheduledExecutorService executor;
  private final TankDisplay tankDisplay;

  public FishClient(String address, int port) {
    this.logger = LogManager.getLogger("Client Rézo");
    this.logger.atLevel(Level.INFO);
    this.events = new FishEventManager(this.logger);
    this.connection = new FishConnection(this.logger, address, port, this.events);
    this.commands = new FishCommandManager(this.events, this.connection);
    this.repl = new REPL(this.logger, this.commands, System.in, System.out);
    this.executor = Executors.newSingleThreadScheduledExecutor();
    this.tankDisplay = new FishTankDisplay();
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  if (!this.repl.isInterrupted()) this.repl.interrupt();
                  this.executor.shutdownNow();
                  try {
                    this.connection.disconnect();
                  } catch (IOException e) {
                    this.logger.error("Cannot close connection.", e);
                  }

                  this.tankDisplay.stop();
                }));
  }

  @Override
  public void start() {
    try {
      this.connection.connect();
    } catch (IOException e) {
      this.logger.error(e);
      return;
    }

    this.events.addListener(new HandshakeReceiver());
    this.connection.send(new HandShakeInitPacket(Optional.of("N1")));
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
  public CommandManager getCommands() {
    return this.commands;
  }

  @Override
  public Logger getLogger() {
    return this.logger;
  }

  public class HandshakeReceiver implements Listener {

    @EventHandler
    public void onHandshake(HandShakeReceiveEvent event) {
      if (event.id().isEmpty()) {
        logger.warn("Handshake received but no screen available");
        try {
          FishClient.this.connection.disconnect();
        } catch (IOException e) {
          logger.error("Cannot close connection", e);
        }
        return;
      }

      logger.info("Handshake received, start repl, PingRunner and display");
      FishClient.this.repl.start();
      FishClient.this.executor.scheduleAtFixedRate(
          new FishPingRunner(
              FishClient.this.logger, FishClient.this.connection, FishClient.this.events),
          0,
          30,
          TimeUnit.SECONDS);
      FishClient.this.events.removeListener(this);
      FishClient.this.tankDisplay.start(400, 400);
    }
  }
}
