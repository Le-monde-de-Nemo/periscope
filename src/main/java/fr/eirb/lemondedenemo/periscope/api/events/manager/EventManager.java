package fr.eirb.lemondedenemo.periscope.api.events.manager;

/** Manages events and listeners. */
public interface EventManager {

  /**
   * Adds a listener to the event manager.
   *
   * @param listener the listener to add
   */
  void addListener(Listener listener);

  /**
   * Removes a listener from the event manager.
   *
   * @param listener the listener to remove
   */
  void removeListener(Listener listener);

  /**
   * Fires an event.
   *
   * @param event the event to fire
   */
  void fireEvent(Event event);
}
