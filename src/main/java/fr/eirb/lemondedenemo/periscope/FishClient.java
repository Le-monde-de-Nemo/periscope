package fr.eirb.lemondedenemo.periscope;

import fr.eirb.lemondedenemo.periscope.api.Client;
import fr.eirb.lemondedenemo.periscope.api.commands.manager.CommandManager;
import fr.eirb.lemondedenemo.periscope.api.network.packets.HandShakeInitPacket;
import fr.eirb.lemondedenemo.periscope.commands.FishCommandManager;
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
  private final ScheduledExecutorService executor;

  public FishClient(String address, int port) {
    this.logger = LogManager.getLogger("Client Rézo");
    this.logger.atLevel(Level.INFO);
    this.events = new FishEventManager(this.logger);
    this.connection = new FishConnection(this.logger, address, port, this.events);
    this.commands = new FishCommandManager(this.events, this.connection);
    this.executor = Executors.newSingleThreadScheduledExecutor();
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  this.executor.shutdownNow();
                  try {
                    this.connection.disconnect();
                  } catch (IOException e) {
                    this.logger.error("Cannot close connection.", e);
                  }
                }));
  }

  public void start() {
    try {
      this.connection.connect();
    } catch (IOException e) {
      this.logger.error(e);
      return;
    }
    this.connection.send(new HandShakeInitPacket(Optional.of("N1")));
    this.executor.scheduleAtFixedRate(new FishPingRunner(this.logger, this.connection, this.events), 0, 30, TimeUnit.MILLISECONDS);
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
}
