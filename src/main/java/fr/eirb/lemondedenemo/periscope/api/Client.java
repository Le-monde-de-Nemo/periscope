package fr.eirb.lemondedenemo.periscope.api;

import fr.eirb.lemondedenemo.periscope.api.events.manager.EventManager;
import fr.eirb.lemondedenemo.periscope.api.network.Connection;
import org.apache.logging.log4j.Logger;

/** Represents a client. */
public interface Client {

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
   * Gets the logger.
   *
   * @return the logger
   */
  Logger getLogger();
}
