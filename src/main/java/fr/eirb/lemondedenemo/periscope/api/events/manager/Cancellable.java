package fr.eirb.lemondedenemo.periscope.api.events.manager;

public interface Cancellable {

  boolean isCancelled();

  void setCancelled(boolean cancelled);
}
