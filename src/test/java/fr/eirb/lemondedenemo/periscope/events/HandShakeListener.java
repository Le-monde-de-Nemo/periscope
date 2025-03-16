package fr.eirb.lemondedenemo.periscope.events;

import fr.eirb.lemondedenemo.periscope.api.events.HandShakeReceiveEvent;
import fr.eirb.lemondedenemo.periscope.api.events.PongReceiveEvent;
import fr.eirb.lemondedenemo.periscope.api.events.manager.EventHandler;
import fr.eirb.lemondedenemo.periscope.api.events.manager.Listener;

public class HandShakeListener implements Listener {

  int called = 0;

  @EventHandler
  public void onHandShakeReceive(HandShakeReceiveEvent event) {
    System.out.println("Connection accepted ? " + event.id().isPresent());
    ++called;
  }

  @EventHandler
  public void onPongReceive(PongReceiveEvent event) {
    ++called;
  }
}
