package fr.eirb.lemondedenemo.periscope;

import fr.eirb.lemondedenemo.periscope.api.Client;
import fr.eirb.lemondedenemo.periscope.api.commands.manager.CommandManager;
import fr.eirb.lemondedenemo.periscope.api.display.TankDisplay;
import fr.eirb.lemondedenemo.periscope.api.events.HandShakeReceiveEvent;
import fr.eirb.lemondedenemo.periscope.api.events.manager.EventHandler;
import fr.eirb.lemondedenemo.periscope.api.events.manager.Listener;
import fr.eirb.lemondedenemo.periscope.api.network.packets.GetContinouslyFishesPacket;
import fr.eirb.lemondedenemo.periscope.api.network.packets.HandShakeInitPacket;
import fr.eirb.lemondedenemo.periscope.commands.FishCommandManager;
import fr.eirb.lemondedenemo.periscope.commands.REPL;
import fr.eirb.lemondedenemo.periscope.display.FishTankDisplay;
import fr.eirb.lemondedenemo.periscope.display.FishTankListener;
import fr.eirb.lemondedenemo.periscope.events.FishEventManager;
import fr.eirb.lemondedenemo.periscope.network.FishConnection;
import fr.eirb.lemondedenemo.periscope.network.FishPingRunner;
import fr.eirb.lemondedenemo.periscope.utils.Config;
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
  private final Config config;
  private final FishEventManager events;
  private final FishConnection connection;
  private final FishCommandManager commands;
  private final REPL repl;
  private final ScheduledExecutorService executor;
  private final TankDisplay tankDisplay;
  private final FishTankListener fishTankListener;

  public FishClient(String address, int port) {
    this.logger = LogManager.getLogger("Client Rézo");
    this.logger.atLevel(Level.INFO);
    try {
      this.config = Config.getInstance();
    } catch (IOException e) {
      this.logger.error("Error while loading config", e);
      throw new RuntimeException(e);
    }
    this.events = new FishEventManager(this.logger);
    this.connection = new FishConnection(this.logger, address, port, this.events);
    this.commands = new FishCommandManager(this.events, this.connection);
    this.repl = new REPL(this.logger, this.commands, System.in, System.out);
    this.executor =
        Executors.newSingleThreadScheduledExecutor(
            r -> {
              Thread thread = new Thread(r);
              thread.setName("Ping Runner");
              return thread;
            });
    this.tankDisplay = new FishTankDisplay();
    this.fishTankListener = new FishTankListener(this.logger, this.tankDisplay);

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
    Optional<String> id =
        Optional.ofNullable(FishClient.this.config.getProperties().getProperty("id", null));
    this.connection.send(new HandShakeInitPacket(id));
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
      if (!event.success()) {
        FishClient.this.logger.warn("Handshake received but no screen available");
        try {
          FishClient.this.connection.disconnect();
        } catch (IOException e) {
          FishClient.this.logger.error("Cannot close connection", e);
        }
        return;
      }

      FishClient.this.logger.info("Handshake received, start repl, PingRunner and display");
      long timeout = 30;
      try {
        timeout =
            Long.parseLong(
                FishClient.this.config.getProperties().getProperty("display-timeout-value", "30"));
      } catch (NumberFormatException e) {
        FishClient.this.logger.warn("Invalid display timeout value", e);
      }
      FishClient.this.repl.start();
      FishClient.this.executor.scheduleAtFixedRate(
          new FishPingRunner(
              FishClient.this.logger, FishClient.this.connection, FishClient.this.events),
          0,
          timeout,
          TimeUnit.SECONDS);
      FishClient.this.tankDisplay.start(event.width(), event.height());
      FishClient.this.events.addListener(FishClient.this.fishTankListener);

      FishClient.this.connection.send(new GetContinouslyFishesPacket());

      FishClient.this.events.removeListener(this);
    }
  }
}
