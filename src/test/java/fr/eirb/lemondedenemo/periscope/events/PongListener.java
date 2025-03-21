package fr.eirb.lemondedenemo.periscope.events;

import fr.eirb.lemondedenemo.periscope.api.events.PongReceiveEvent;
import fr.eirb.lemondedenemo.periscope.api.events.manager.EventHandler;
import fr.eirb.lemondedenemo.periscope.api.events.manager.Listener;
import org.apache.logging.log4j.Logger;

public class PongListener implements Listener {

  private final Logger logger;

  public PongListener(Logger logger) {
    logger.info("PongListener created");
    this.logger = logger;
  }

  @EventHandler
  public void onPongReceiveEvent(PongReceiveEvent event) {
    logger.info("Pong received: " + event.id());
  }
}
