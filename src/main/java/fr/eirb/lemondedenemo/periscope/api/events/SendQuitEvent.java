package fr.eirb.lemondedenemo.periscope.api.events;

import fr.eirb.lemondedenemo.periscope.api.events.manager.Cancellable;
import fr.eirb.lemondedenemo.periscope.api.events.manager.Event;

public class SendQuitEvent implements Event, Cancellable {

  private boolean cancelled;

  @Override
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }

  public boolean isCancelled() {
    return this.cancelled;
  }

}
