package fr.eirb.lemondedenemo.periscope.api.events;

import fr.eirb.lemondedenemo.periscope.api.events.manager.Event;

public record HandShakeReceiveEvent(
    boolean success, String id, int vueX, int vueY, int width, int height) implements Event {
  public static HandShakeReceiveEvent failed() {
    return new HandShakeReceiveEvent(false, null, 0, 0, 0, 0);
  }
}
