package fr.eirb.lemondedenemo.periscope.api.events.manager;

public interface Cancellable {

  void setCancelled(boolean cancelled);

  boolean isCancelled();

}
