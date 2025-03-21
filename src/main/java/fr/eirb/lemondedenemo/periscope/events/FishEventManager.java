package fr.eirb.lemondedenemo.periscope.events;

import fr.eirb.lemondedenemo.periscope.api.events.manager.Event;
import fr.eirb.lemondedenemo.periscope.api.events.manager.EventHandler;
import fr.eirb.lemondedenemo.periscope.api.events.manager.EventManager;
import fr.eirb.lemondedenemo.periscope.api.events.manager.Listener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.Logger;

public class FishEventManager implements EventManager {

  private final Logger logger;
  private final List<Listener> listeners;

  public FishEventManager(Logger logger) {
    this.logger = logger;
    this.listeners = new ArrayList<>();
  }

  @Override
  public void addListener(Listener listener) {
    this.listeners.add(listener);
  }

  @Override
  public void removeListener(Listener listener) {
    this.listeners.remove(listener);
  }

  @Override
  public void fireEvent(Event event) {
    for (Listener listener : listeners) {
      Arrays.stream(listener.getClass().getMethods())
          .filter(method -> method.isAnnotationPresent(EventHandler.class))
          .filter(method -> method.getParameterCount() == 1)
          .filter(method -> method.getParameterTypes()[0].isAssignableFrom(event.getClass()))
          .forEach(
              method -> {
                try {
                  method.invoke(listener, event);
                } catch (Exception e) {
                  this.logger.error(e);
                }
              });
    }
  }
}
