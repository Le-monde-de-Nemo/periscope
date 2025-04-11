package fr.eirb.lemondedenemo.periscope.api;

import fr.eirb.lemondedenemo.periscope.api.commands.manager.CommandManager;
import fr.eirb.lemondedenemo.periscope.api.events.manager.EventManager;
import fr.eirb.lemondedenemo.periscope.api.network.Connection;
import org.apache.logging.log4j.Logger;

/** Represents a client. */
public interface Client {

  /** Start client */
  void start();

  /**
   * Gets the connection.
   *
   * @return the connection
   */
  Connection getConnection();

  /**
   * Gets the event manager.
   *
   * @return the event manager
   */
  EventManager getEvents();

  /**
   * Gets the command manager.
   *
   * @return the command manager
   */
  CommandManager getCommands();

  /**
   * Gets the logger.
   *
   * @return the logger
   */
  Logger getLogger();
}
